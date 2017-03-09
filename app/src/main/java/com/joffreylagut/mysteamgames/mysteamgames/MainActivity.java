package com.joffreylagut.mysteamgames.mysteamgames;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.joffreylagut.mysteamgames.mysteamgames.customclass.Game;
import com.joffreylagut.mysteamgames.mysteamgames.customclass.GameListItem;
import com.joffreylagut.mysteamgames.mysteamgames.customclass.OwnedGame;
import com.joffreylagut.mysteamgames.mysteamgames.customclass.User;
import com.joffreylagut.mysteamgames.mysteamgames.data.UserContract;
import com.joffreylagut.mysteamgames.mysteamgames.data.UserDbHelper;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.AnimatedTabHostListener;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.GameListSorter;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SteamAPICalls;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static android.support.design.widget.Snackbar.make;
import static com.joffreylagut.mysteamgames.mysteamgames.utilities.SteamAPICalls.createGameImageURL;

public class MainActivity extends AppCompatActivity implements GameListAdapter.ListItemClickListener, NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    Double totalMoneySpent = 0.00;
    UserDbHelper userDbHelper;
    SharedPreferences sharedPreferences;
    CoordinatorLayout coordinatorLayout;
    // Declaration of the global values of this activity.
    private User currentUser = new User();
    private Toast message = null;
    private SQLiteDatabase mDb;
    // Declaration of all the view that we will interact with.
    private ImageView ivProfile;
    private ProgressBar pbLoading;
    private TextView tvAccountName;
    private TextView tvNumberOfGames;
    private TextView tvTotalTimePlayed;
    private TextView tvMoneySpent;
    private RecyclerView rvAllGames;
    private RecyclerView rvRecentGames;
    private RecyclerView rvFavoriteGames;
    private TextView tvTotalPricePerHour;

    // Variable used to determine if we want to display ASC or DESC menu item
    private boolean currentSortAsc;
    private String currentSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.activity_main_title);

        // Stepho is providing us an access to the mobile database with Chrome.
        // We initialize it here.
        Stetho.initializeWithDefaults(this);

        // We set the view by calling this private method
        setViews();

        // We are declaring a new UserDbHelper to access to the db.
        userDbHelper = UserDbHelper.getInstance(this);
        mDb = userDbHelper.getWritableDatabase();

        // We display the Asc menu item
        currentSortAsc = false;
        currentSort = "time_played";

        // We load the SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // If the user have already used the app, the saved informations will be displayed.
        Long steamID = Long.valueOf(sharedPreferences.getString("etp_steamID", "0"));
        refreshUserProfileInformationFromDb(steamID);

        // We are generating the URL and then ask the Steam API
        SteamAPICalls.getURLPlayerProfileInformation(sharedPreferences.getString("etp_steamID", ""));
        RetrieveProfileInformation myProfileInformation = new RetrieveProfileInformation();
        URL[] listURLAPIToCall = {
                SteamAPICalls.getURLPlayerProfileInformation(sharedPreferences.getString("etp_steamID", "")),
                SteamAPICalls.getURLPlayerOwnedGames(sharedPreferences.getString("etp_steamID", ""))
        };
        myProfileInformation.execute(listURLAPIToCall);
    }

    private void setViews() {

        // First, we have to set the DrawerLayout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Someday
        /**FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
         fab.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        .setAction("Action", null).show();
        }
        });**/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Now we setup the TabHost and its tabs
        TabHost host = (TabHost) findViewById(R.id.tabhost);
        host.setup();

        //Tab recent
        TabHost.TabSpec spec = host.newTabSpec(getResources().getString(R.string.tab_recent_title));
        spec.setContent(R.id.tab_recent);
        spec.setIndicator(getResources().getString(R.string.tab_recent_title));
        host.addTab(spec);

        //Tab favorite
        spec = host.newTabSpec(getResources().getString(R.string.tab_favorite_title));
        spec.setContent(R.id.tab_favorite);
        spec.setIndicator(getResources().getString(R.string.tab_favorite_title));
        host.addTab(spec);
        host.setOnTabChangedListener(new AnimatedTabHostListener(this, host));

        //Tab all
        spec = host.newTabSpec(getResources().getString(R.string.tab_all_title));
        spec.setContent(R.id.tab_all);
        spec.setIndicator(getResources().getString(R.string.tab_all_title));
        host.addTab(spec);

        // Then, we are linking our views with the layout
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        rvAllGames = (RecyclerView) findViewById(R.id.rv_all_games);
        rvRecentGames = (RecyclerView) findViewById(R.id.rv_recent_games);
        rvFavoriteGames = (RecyclerView) findViewById(R.id.rv_favorite_games);
        rvAllGames = (RecyclerView) findViewById(R.id.rv_all_games);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);

        View hView = navigationView.getHeaderView(0);
        ivProfile = (ImageView) hView.findViewById(R.id.nav_iv_profile);
        tvAccountName = (TextView) hView.findViewById(R.id.nav_tv_account_name);
        tvNumberOfGames = (TextView) hView.findViewById(R.id.nav_tv_nb_games);
        tvTotalTimePlayed = (TextView) hView.findViewById(R.id.nav_tv_nb_hours_played);
        tvTotalPricePerHour = (TextView) hView.findViewById(R.id.nav_tv_price_per_hour);
        tvMoneySpent = (TextView) hView.findViewById(R.id.nav_tv_money_spent);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_list_menu, menu);

        if (currentSortAsc) {
            MenuItem menuAsc = menu.findItem(R.id.menu_ascendant);
            menuAsc.setVisible(true);

            MenuItem menuDsc = menu.findItem(R.id.menu_descendant);
            menuDsc.setVisible(false);
        } else {
            MenuItem menuAsc = menu.findItem(R.id.menu_ascendant);
            menuAsc.setVisible(false);

            MenuItem menuDsc = menu.findItem(R.id.menu_descendant);
            menuDsc.setVisible(true);
        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;

        } else if (id == R.id.nav_logout) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().remove("etp_steamID").commit();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_send) {
            make(coordinatorLayout, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // We are looking which item is clicked to do the corresponding action
        switch (item.getItemId()) {
            case R.id.menu_sort_alphabetical:
                // We change the current sort of the list
                currentSort = "alphabetical";
                // We call the method that will sort and display the new list
                sortAndShowGameItemList();
                break;
            case R.id.menu_sort_time_played:
                // We change the current sort of the list
                currentSort = "time_played";
                // We call the method that will sort and display the new list
                sortAndShowGameItemList();
                break;

            case R.id.menu_sort_total_price:
                // We change the current sort of the list
                currentSort = "price";
                // We call the method that will sort and display the new list
                sortAndShowGameItemList();
                break;
            case R.id.menu_sort_price_per_hour:
                // We change the current sort of the list
                currentSort = "price_per_hour";
                // We call the method that will sort and display the new list
                sortAndShowGameItemList();
                break;

            case R.id.menu_ascendant:
                currentSortAsc = false;
                invalidateOptionsMenu();
                // We call the method that will sort and display the new list
                sortAndShowGameItemList();
                break;

            case R.id.menu_descendant:
                currentSortAsc = true;
                invalidateOptionsMenu();
                // We call the method that will sort and display the new list
                sortAndShowGameItemList();
                break;
        }
        return true;
    }

    private void sortAndShowGameItemList() {

        // We have to get the adapters
        GameListAdapter allGamesListAdapter = (GameListAdapter) rvAllGames.getAdapter();
        GameListAdapter recentGamesListAdapter = (GameListAdapter) rvRecentGames.getAdapter();
        GameListAdapter favoritesGamesListAdapter = (GameListAdapter) rvFavoriteGames.getAdapter();

        // To then get the lists
        List<GameListItem> allGamesSortedList = allGamesListAdapter.getGameList();
        List<GameListItem> recentGamesSortedList = recentGamesListAdapter.getGameList();
        List<GameListItem> favoriteGamesSortedList = favoritesGamesListAdapter.getGameList();

        // Now we have to sort the lists
        switch (currentSort) {
            case "alphabetical":
                allGamesSortedList = GameListSorter.sortByName(allGamesSortedList, currentSortAsc);
                recentGamesSortedList = GameListSorter.sortByName(recentGamesSortedList, currentSortAsc);
                favoriteGamesSortedList = GameListSorter.sortByName(favoriteGamesSortedList, currentSortAsc);
                break;
            case "time_played":
                allGamesSortedList = GameListSorter.sortByTimePlayed(allGamesSortedList, currentSortAsc);
                recentGamesSortedList = GameListSorter.sortByTimePlayed(recentGamesSortedList, currentSortAsc);
                favoriteGamesSortedList = GameListSorter.sortByTimePlayed(favoriteGamesSortedList, currentSortAsc);
                break;
            case "price_per_hour":
                /*// Here, we have to remove all the free to play
                Iterator<GameListItem> it = sortedList.iterator();
                while(it.hasNext()){
                    GameListItem game = it.next();
                    if(game.getGamePrice() == 0){
                        it.remove();
                    }
                }*/
                allGamesSortedList = GameListSorter.sortByPricePerHour(allGamesSortedList, currentSortAsc);
                recentGamesSortedList = GameListSorter.sortByPricePerHour(recentGamesSortedList, currentSortAsc);
                favoriteGamesSortedList = GameListSorter.sortByPricePerHour(favoriteGamesSortedList, currentSortAsc);
                break;
            case "price":
                allGamesSortedList = GameListSorter.sortByPrice(allGamesSortedList, currentSortAsc);
                recentGamesSortedList = GameListSorter.sortByPrice(recentGamesSortedList, currentSortAsc);
                favoriteGamesSortedList = GameListSorter.sortByPrice(favoriteGamesSortedList, currentSortAsc);
                break;
        }

        // Now that we have the sortedlists, we put the new adapters in the recyclerviews
        allGamesListAdapter.setGameList(allGamesSortedList);
        rvAllGames.setAdapter(allGamesListAdapter);

        recentGamesListAdapter.setGameList(recentGamesSortedList);
        rvRecentGames.setAdapter(recentGamesListAdapter);

        favoritesGamesListAdapter.setGameList(favoriteGamesSortedList);
        rvFavoriteGames.setAdapter(favoritesGamesListAdapter);

    }

    private void refreshUserProfileInformationFromDb(long steamID) {

        currentUser = userDbHelper.getUserBySteamID(mDb, steamID);

        if (currentUser == null) return;
        // Then, we get the list of owned games from OwnedGames table.
        List<OwnedGame> userOwnedGames = new ArrayList<>();
        OwnedGame currentOwnedGame;
        Game currentGame;
        Cursor result;
        Cursor gameRow;
        currentUser.setNbMinutesPlayed(0);

        result = userDbHelper.getOwnedGamesByUserID(mDb, String.valueOf(currentUser.getUserID()));
        if(result.getCount() !=0){
            result.moveToFirst();
            while(result.isAfterLast() == false){
                currentOwnedGame = new OwnedGame();
                currentGame = new Game();
                currentGame.setGameID(result.getInt(result.getColumnIndex(
                        UserContract.OwnedGamesEntry.COLUMN_GAME_ID)));
                // We have to retriev all the game information in DB.
                gameRow = userDbHelper.getGameBy_ID(mDb, String.valueOf(currentGame.getGameID()));
                if(gameRow.getCount() != 0){
                    gameRow.moveToFirst();
                    currentGame.setGameName(gameRow.getString(gameRow.getColumnIndex(
                            UserContract.GameEntry.COLUMN_GAME_NAME)));
                    currentGame.setSteamID(Long.valueOf(gameRow.getString(gameRow.getColumnIndex(
                            UserContract.GameEntry.COLUMN_STEAM_ID))));
                    currentGame.setMarketplace(gameRow.getString(gameRow.getColumnIndex(
                            UserContract.GameEntry.COLUMN_MARKETPLACE)));

                    try {
                        currentGame.setGameLogo(new URL(
                                gameRow.getString(gameRow.getColumnIndex(
                                        UserContract.GameEntry.COLUMN_GAME_LOGO))));
                        currentGame.setGameIcon(new URL(
                                gameRow.getString(gameRow.getColumnIndex(
                                        UserContract.GameEntry.COLUMN_GAME_ICON))));

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                // TODO : Check if it works
                gameRow.close();

                currentOwnedGame.setGame(currentGame);
                currentOwnedGame.setGamePrice(result.getDouble(result.getColumnIndex(
                        UserContract.OwnedGamesEntry.COLUMN_GAME_PRICE)));
                currentOwnedGame.setTimePlayedForever(result.getInt(result.getColumnIndex(
                        UserContract.OwnedGamesEntry.COLUMN_TIME_PLAYED_FOREVER)));
                currentOwnedGame.setTimePlayed2Weeks(result.getInt(result.getColumnIndex(
                        UserContract.OwnedGamesEntry.COLUMN_TIME_PLAYED_2_WEEKS)));
                userOwnedGames.add(currentOwnedGame);

                if (result.getDouble(result.getColumnIndex(
                        UserContract.OwnedGamesEntry.COLUMN_GAME_PRICE)) != -1.00) {
                    totalMoneySpent = totalMoneySpent + result.getDouble(result.getColumnIndex(
                            UserContract.OwnedGamesEntry.COLUMN_GAME_PRICE));
                }
                currentUser.setNbMinutesPlayed(currentUser.getNbMinutesPlayed() + currentOwnedGame.getTimePlayedForever());
                result.moveToNext();
            }
            result.close();
        }
        currentUser.setOwnedGames(userOwnedGames);
        // Now we can display the user's information.
        if(currentUser.getAccountName() != null){
            tvAccountName.setText(currentUser.getAccountName());
        }
        tvTotalTimePlayed.setText(String.valueOf(currentUser.getNbMinutesPlayed()));
        tvNumberOfGames.setText(String.valueOf(currentUser.getOwnedGames().size()));

        tvTotalTimePlayed.setText(SteamAPICalls.convertTimePlayed(currentUser.getNbMinutesPlayed()));

        if(currentUser.getAccountPicture() != null){
            String urlImageToLoad = currentUser.getAccountPicture().toString();
            Picasso.with(this).load(urlImageToLoad).into(ivProfile);
        }
        Double nbHoursTotal = Double.valueOf(currentUser.getNbMinutesPlayed()) / 60;
        Double totalPricePerHour = totalMoneySpent / nbHoursTotal;
        DecimalFormat df = new DecimalFormat("#.##");
        tvTotalPricePerHour.setText(String.valueOf(df.format(totalPricePerHour)) + sharedPreferences.getString("lp_currency", "$") + "/h");
        tvMoneySpent.setText(String.valueOf(df.format(totalMoneySpent)) + sharedPreferences.getString("lp_currency", "$"));

        // We insert the data in our RecyclerViews

        List<GameListItem> recentGamesList = createGameListItemList(
                currentUser.getRecentlyPlayedGames());
        GameListAdapter recentGamesListAdapter = new GameListAdapter(recentGamesList, this);
        rvRecentGames.setAdapter(recentGamesListAdapter);
        rvRecentGames.setLayoutManager(new GridLayoutManager(this, 2));

        List<GameListItem> favoriteGamesList = createGameListItemList(
                currentUser.getFavoriteGames());
        GameListAdapter favoriteGamesListAdapter = new GameListAdapter(recentGamesList, this);
        rvFavoriteGames.setAdapter(favoriteGamesListAdapter);
        rvFavoriteGames.setLayoutManager(new GridLayoutManager(this, 2));

        List<GameListItem> allGamesList = createGameListItemList(currentUser.getOwnedGames());
        GameListAdapter allGamesListAdapter = new GameListAdapter(allGamesList, this);
        rvAllGames.setAdapter(allGamesListAdapter);
        rvAllGames.setLayoutManager(new GridLayoutManager(this, 2));

        // We change the current sort of the lists
        currentSort = "time_played";
        // We call the method that will sort and display the new lists
        sortAndShowGameItemList();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String newPrice = data.getStringExtra("newPrice");
                int adapterPosition = data.getIntExtra("adapterPosition", 0);
                GameListAdapter gameListAdapter = (GameListAdapter) rvAllGames.getAdapter();
                GameListItem itemToEdit = gameListAdapter.getGameList().get(adapterPosition);
                itemToEdit.setGamePrice(Double.valueOf(newPrice));
                gameListAdapter.getGameList().set(adapterPosition, itemToEdit);
                gameListAdapter.notifyItemChanged(adapterPosition);
            }
        }
    }

    @Override
    public void ListItemClicked(String clickedItemName) {
        if (message != null) {
            message.cancel();
        }
        message = Toast.makeText(this, "Click on " + clickedItemName, Toast.LENGTH_LONG);
        message.show();
    }

    public void ParseJSONProfile(String JSONProfil) {

        try {
            // We are directly going to the object containing our player information.
            JSONObject player = new JSONObject(JSONProfil)
                    .getJSONObject("response")
                    .getJSONArray("players").getJSONObject(0);

            // We insert the information in our container.
            long steamID = Long.valueOf(player.getString("steamid"));
            String accountName = player.getString("personaname");
            String accountPicture = player.getString("avatarfull");

            // We first check if the user already exist in db
            userDbHelper = UserDbHelper.getInstance(this);
            User userInDB = userDbHelper.getUserBySteamID(mDb, steamID);

            if (userInDB != null) {
                // The user already exist, we have to update his informations.
                userDbHelper.updateUserBySteamID(mDb, String.valueOf(steamID),
                        accountName, accountPicture);
            }else{
                // The user doesn't exist, we have to insert him in DB.
                userDbHelper.addNewUser(mDb, String.valueOf(steamID)
                        , accountName, accountPicture);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void ParseJSONGames(String JSONGames){
        try {
            JSONArray jsonArrayGames = new JSONObject(JSONGames).getJSONObject("response").getJSONArray("games");
            User user;
            int jsonArrayLenght = jsonArrayGames.length();
            for (int i=0; i < jsonArrayGames.length(); i++){

                // We retrieve and store all of the information in variables.
                JSONObject jsonGame = jsonArrayGames.getJSONObject(i);

                String appID = jsonGame.getString("appid");
                String name = jsonGame.getString("name");
                String playtime_2weeks = "";
                if(jsonGame.has("playtime_2weeks")) playtime_2weeks = jsonGame.getString("playtime_2weeks");
                String playtime_forever = jsonGame.getString("playtime_forever");
                String img_icon_url = jsonGame.getString("img_icon_url");
                img_icon_url = createGameImageURL(img_icon_url, appID).toString();
                String img_logo_url = jsonGame.getString("img_logo_url");
                img_logo_url = SteamAPICalls.createGameImageURL(img_logo_url, appID).toString();

                UserDbHelper userDbHelper = new UserDbHelper(this);

                // We retrieve the User in DB.
                user = userDbHelper.getUserBySteamID(mDb, Long.valueOf(sharedPreferences.getString("etp_steamID", "")));

                // First, we check if the game already exist in db.
                Cursor cursor = userDbHelper.getGameBySteamID(mDb, appID);
                if(cursor.getCount() != 0){
                    // The game already exist, we have to update it in db.
                    userDbHelper.updateGameBySteamID(mDb, appID, name, img_logo_url,
                            img_icon_url, "Steam");
                }else{
                    // The game doesn't exist, we have to add it in db.
                    userDbHelper.addNewGame(mDb, appID, name, img_logo_url,
                            img_icon_url, "Steam");
                }

                // Now that the game is in DB, we have to check if the game is already owned by
                // the user in DB.
                // We retrieve the gameUD in DB.
                cursor = userDbHelper.getGameBySteamID(mDb, appID);
                String gameID = "0";
                if(cursor.getCount() != 0){
                    gameID = cursor.getString(cursor.getColumnIndex(UserContract.GameEntry._ID));
                }

                cursor = userDbHelper.getOwnedGame(mDb, String.valueOf(user.getUserID()), gameID);
                if(cursor.getCount() != 0){
                    // The game is already owned by the user, we have to update it.
                    userDbHelper.updateOwnedGame(mDb, String.valueOf(user.getUserID()), gameID,
                            playtime_forever, playtime_2weeks, null, null);
                }else{
                    // The game isn't already owned in db, we add it.
                    userDbHelper.addNewOwnedGame(mDb, String.valueOf(user.getUserID()), gameID,
                            playtime_forever, playtime_2weeks, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<GameListItem> createGameListItemList(List<OwnedGame> ownedGames){
        List<GameListItem> gameListItems = new ArrayList<>();
        GameListItem item;

        for (OwnedGame ownedGame : ownedGames){
            item = new GameListItem();
            item.setGameTimePlayed(ownedGame.getTimePlayedForever());
            item.setGameImage(ownedGame.getGame().getGameLogo());
            item.setGameName(ownedGame.getGame().getGameName());
            item.setGamePrice(ownedGame.getGamePrice());
            item.setGameID(ownedGame.getGame().getGameID());
            item.setUserID(currentUser.getUserID());
            gameListItems.add(item);
        }

        return gameListItems;
    }

    // TODO Change the AsyncTask into a Loader
    class RetrieveProfileInformation extends AsyncTask<URL, Void, String[]> {


        protected void onPreExecute() {
            pbLoading.setVisibility(View.VISIBLE);
        }

        protected String[] doInBackground(URL... urls) {
            // Do some validation here
            String[] responses = new String[urls.length];
            int i = 0;
            for(URL currentURL : urls) {
                try {
                    HttpURLConnection urlConnection = (HttpURLConnection) currentURL.openConnection();
                    try {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        responses[i] = stringBuilder.toString();
                        i++;
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage(), e);
                    responses[i] = "Error";
                    i++;
                }
            }
            Log.i("INFO", responses[0]);
            Log.i("INFO", responses[1]);
            if(!responses[0].equals("Error with userJSON")) ParseJSONProfile(responses[0]);
            if(!responses[1].equals("Error with gamesJSON")) ParseJSONGames(responses[1]);
            return responses;
        }

        protected void onPostExecute(String[] responses) {
            pbLoading.setVisibility(View.INVISIBLE);

            Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.new_steam_data_loaded, Snackbar.LENGTH_LONG)
                    .setAction(R.string.snackbar_action_refresh, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            refreshUserProfileInformationFromDb(Long.valueOf(sharedPreferences.getString("etp_steamID", "")));
                        }
                    });
            snackbar.show();

        }
    }
}
