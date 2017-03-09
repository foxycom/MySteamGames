package com.joffreylagut.mysteamgames.mysteamgames;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.joffreylagut.mysteamgames.mysteamgames.data.UserContract;
import com.joffreylagut.mysteamgames.mysteamgames.data.UserDbHelper;
import com.joffreylagut.mysteamgames.mysteamgames.objects.Game;
import com.joffreylagut.mysteamgames.mysteamgames.objects.GameListItem;
import com.joffreylagut.mysteamgames.mysteamgames.objects.OwnedGame;
import com.joffreylagut.mysteamgames.mysteamgames.objects.User;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.AnimatedTabHostListener;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.GameListSorter;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.RetrieveDataFromSteamIntentService;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SteamAPICalls;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static android.support.design.widget.Snackbar.make;

public class MainActivity extends AppCompatActivity implements GameListAdapter.ListItemClickListener, NavigationView.OnNavigationItemSelectedListener {

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

        // We start the service that retrieve steam information
        Intent dataRetrieverService = new Intent(this, RetrieveDataFromSteamIntentService.class);
        startService(dataRetrieverService);

        IntentFilter filter = new IntentFilter(SteamDataReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        SteamDataReceiver receiver = new SteamDataReceiver();
        registerReceiver(receiver, filter);

        // We set the view by calling this private method
        setViews();
        pbLoading.setVisibility(View.INVISIBLE);

        // We are declaring a new UserDbHelper to access to the db.
        userDbHelper = UserDbHelper.getInstance(this);
        mDb = userDbHelper.getWritableDatabase();

        // We display the Asc menu item
        currentSortAsc = false;
        currentSort = "time_played";

        // We load the SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // We refresh the user information from DB
        refreshUserProfileInformationFromDb();

        // We call the method that will sort and display the new lists
        sortAndShowGameItemList(rvAllGames);
        sortAndShowGameItemList(rvFavoriteGames);
        sortAndShowGameItemList(rvRecentGames);

    }

    private void setViews() {

        // First, we have to set the DrawerLayout
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);


        drawer.addDrawerListener(toggle);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;

        } else if (id == R.id.nav_logout) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().remove("etp_steamID").apply();
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
                sortAndShowGameItemList(rvAllGames);
                sortAndShowGameItemList(rvFavoriteGames);
                sortAndShowGameItemList(rvRecentGames);
                break;
            case R.id.menu_sort_time_played:
                // We change the current sort of the list
                currentSort = "time_played";
                // We call the method that will sort and display the new list
                sortAndShowGameItemList(rvAllGames);
                sortAndShowGameItemList(rvFavoriteGames);
                sortAndShowGameItemList(rvRecentGames);
                break;

            case R.id.menu_sort_total_price:
                // We change the current sort of the list
                currentSort = "price";
                // We call the method that will sort and display the new list
                sortAndShowGameItemList(rvAllGames);
                sortAndShowGameItemList(rvFavoriteGames);
                sortAndShowGameItemList(rvRecentGames);
                break;
            case R.id.menu_sort_price_per_hour:
                // We change the current sort of the list
                currentSort = "price_per_hour";
                // We call the method that will sort and display the new list
                sortAndShowGameItemList(rvAllGames);
                sortAndShowGameItemList(rvFavoriteGames);
                sortAndShowGameItemList(rvRecentGames);
                break;

            case R.id.menu_ascendant:
                currentSortAsc = false;
                invalidateOptionsMenu();
                // We call the method that will sort and display the new list
                sortAndShowGameItemList(rvAllGames);
                sortAndShowGameItemList(rvFavoriteGames);
                sortAndShowGameItemList(rvRecentGames);
                break;

            case R.id.menu_descendant:
                currentSortAsc = true;
                invalidateOptionsMenu();
                // We call the method that will sort and display the new list
                sortAndShowGameItemList(rvAllGames);
                sortAndShowGameItemList(rvFavoriteGames);
                sortAndShowGameItemList(rvRecentGames);
                break;
        }
        return true;
    }

    private void sortAndShowGameItemList(RecyclerView rvToSort) {

        // We have to get the adapters
        GameListAdapter allGamesListAdapter = (GameListAdapter) rvToSort.getAdapter();

        // To then get the lists
        List<GameListItem> allGamesSortedList = allGamesListAdapter.getGameList();

        // Now we have to sort the lists
        switch (currentSort) {
            case "alphabetical":
                allGamesSortedList = GameListSorter.sortByName(allGamesSortedList, currentSortAsc);
                break;
            case "time_played":
                allGamesSortedList = GameListSorter.sortByTimePlayed(allGamesSortedList, currentSortAsc);
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
                break;
            case "price":
                allGamesSortedList = GameListSorter.sortByPrice(allGamesSortedList, currentSortAsc);
                break;
        }

        // Now that we have the sortedlists, we put the new adapters in the recyclerviews
        allGamesListAdapter.setGameList(allGamesSortedList);
        rvToSort.setAdapter(allGamesListAdapter);
    }

    private void refreshUserProfileInformationFromDb() {

        // If the user have already used the app, the saved information will be displayed.
        Long steamID = Long.valueOf(sharedPreferences.getString("etp_steamID", "0"));

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
            while (!result.isAfterLast()) {
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
        Double nbHoursTotal = (double) currentUser.getNbMinutesPlayed() / 60;
        Double totalPricePerHour = totalMoneySpent / nbHoursTotal;
        DecimalFormat df = new DecimalFormat("#.##");
        tvTotalPricePerHour.setText(String.valueOf(df.format(totalPricePerHour))
                + sharedPreferences.getString("lp_currency", "$") + "/h");
        String moneySpentText = String.valueOf(df.format(totalMoneySpent))
                + sharedPreferences.getString("lp_currency", "$");
        tvMoneySpent.setText(moneySpentText);

        // We insert the data in our RecyclerViews

        List<GameListItem> recentGamesList = createGameListItemList(
                currentUser.getRecentlyPlayedGames());
        GameListAdapter recentGamesListAdapter =
                new GameListAdapter(recentGamesList, this, "rvRecentGames");
        rvRecentGames.setAdapter(recentGamesListAdapter);
        rvRecentGames.setLayoutManager(new GridLayoutManager(this, 2));

        List<GameListItem> favoriteGamesList = createGameListItemList(
                currentUser.getFavoriteGames());
        GameListAdapter favoriteGamesListAdapter =
                new GameListAdapter(favoriteGamesList, this, "rvFavoriteGames");
        rvFavoriteGames.setAdapter(favoriteGamesListAdapter);
        rvFavoriteGames.setLayoutManager(new GridLayoutManager(this, 2));

        List<GameListItem> allGamesList = createGameListItemList(currentUser.getOwnedGames());
        GameListAdapter allGamesListAdapter =
                new GameListAdapter(allGamesList, this, "rvAllGames");
        rvAllGames.setAdapter(allGamesListAdapter);
        rvAllGames.setLayoutManager(new GridLayoutManager(this, 2));

        // We change the current sort of the lists
        currentSort = "time_played";
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            // We check if the user have updated the game
            if (resultCode == Activity.RESULT_OK) {
                // We retrieve the information from the intent
                String newPrice = data.getStringExtra("newPrice");
                int adapterPosition = data.getIntExtra("adapterPosition", 0);
                String recyclerName = data.getStringExtra("recyclerName");

                // We refresh the user information from DB to update currentUser
                refreshUserProfileInformationFromDb();

                // We make a new list to put in the recyclerViews
                List<GameListItem> newOwnedGamesList =
                        createGameListItemList(currentUser.getOwnedGames());

                // Now, we have to identify the recyclerView that have opened GameDetailActivity
                // We prepare a variable to get the adapter
                GameListAdapter gameListAdapter;
                if (recyclerName.equals("rvAllGames")) {
                    gameListAdapter = (GameListAdapter) rvAllGames.getAdapter();

                    // We have to update the other RecyclerView
                    GameListAdapter newGameListAdapter =
                            new GameListAdapter(newOwnedGamesList, this, "rvRecentGames");
                    rvRecentGames.setAdapter(newGameListAdapter);
                    rvFavoriteGames.setAdapter(newGameListAdapter);

                    sortAndShowGameItemList(rvFavoriteGames);
                    sortAndShowGameItemList(rvRecentGames);

                } else if (recyclerName.equals("rvFavoriteGames")) {

                    gameListAdapter = (GameListAdapter) rvFavoriteGames.getAdapter();

                    // We have to update the other RecyclerView
                    GameListAdapter newGameListAdapter =
                            new GameListAdapter(newOwnedGamesList, this, "rvRecentGames");
                    rvRecentGames.setAdapter(newGameListAdapter);
                    rvAllGames.setAdapter(newGameListAdapter);
                    sortAndShowGameItemList(rvAllGames);
                    sortAndShowGameItemList(rvRecentGames);

                } else {
                    gameListAdapter = (GameListAdapter) rvRecentGames.getAdapter();

                    // We have to update the other RecyclerView
                    GameListAdapter newGameListAdapter =
                            new GameListAdapter(newOwnedGamesList, this, "rvRecentGames");
                    rvAllGames.setAdapter(newGameListAdapter);
                    rvFavoriteGames.setAdapter(newGameListAdapter);
                    sortAndShowGameItemList(rvAllGames);
                    sortAndShowGameItemList(rvFavoriteGames);
                }

                // We update only the item updated in the RecyclerView used to open the activity
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

    public class SteamDataReceiver extends BroadcastReceiver {

        public static final String PROCESS_RESPONSE = "com.joffreylagut.mysteamgames.mysteamgames.intent.action.PROCESS_RESPONSE";

        @Override
        public void onReceive(Context context, Intent intent) {
            pbLoading.setVisibility(View.INVISIBLE);

            if (currentUser == null) {
                refreshUserProfileInformationFromDb();
            } else {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.new_steam_data_loaded, Snackbar.LENGTH_LONG)
                    .setAction(R.string.snackbar_action_refresh, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            refreshUserProfileInformationFromDb();
                        }
                    });
            snackbar.show();
            }
        }
    }
}
