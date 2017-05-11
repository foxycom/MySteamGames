package com.joffreylagut.mysteamgames.mysteamgames.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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

import com.facebook.stetho.Stetho;
import com.joffreylagut.mysteamgames.mysteamgames.R;
import com.joffreylagut.mysteamgames.mysteamgames.data.GameListAdapter;
import com.joffreylagut.mysteamgames.mysteamgames.data.UserDbHelper;
import com.joffreylagut.mysteamgames.mysteamgames.models.GameListItem;
import com.joffreylagut.mysteamgames.mysteamgames.models.OwnedGame;
import com.joffreylagut.mysteamgames.mysteamgames.models.User;
import com.joffreylagut.mysteamgames.mysteamgames.sync.NotificationUtils;
import com.joffreylagut.mysteamgames.mysteamgames.sync.ReminderUtilities;
import com.joffreylagut.mysteamgames.mysteamgames.sync.RetrieveDataFromSteamIntentService;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.AnimatedTabHostListener;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.GameListSorter;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SharedPreferencesHelper;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SteamAPICalls;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.support.design.widget.Snackbar.make;

/**
 * GameListActivity.java
 * Purpose: Display the games of the user.
 *
 * @author Joffrey LAGUT
 * @version 1.5 2017-04-08
 */

public class GameListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Declaration of final values used in this activity
    public static final String rvAllGamesName = "rvAllGames";
    public static final String rvFavoriteGamesName = "rvFavoriteGames";
    public static final String rvRecentGamesName = "rvRecentGames";

    // Declaration of the global values of this activity.
    private User currentUser = new User();
    private SQLiteDatabase mDb;
    private UserDbHelper userDbHelper;
    private SharedPreferences sharedPreferences;
    private CoordinatorLayout coordinatorLayout;

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

    // Variable used to store the datetime of the last steam data refresh
    private Date lastRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);
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

        ReminderUtilities.schedulesRetrieveDataFromSteam(this);

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
        refreshUserProfileInformationFromDb(true);

        // We call the method that will sort and display the new lists
        sortAndShowGameItemList(rvAllGames);
        sortAndShowGameItemList(rvFavoriteGames);
        sortAndShowGameItemList(rvRecentGames);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (lastRefresh != null) {
            Date currentDate = new Date();
            Long timeDifference = currentDate.getTime() - lastRefresh.getTime();
            if (timeDifference > 3600000) {
                // We start the service that retrieve steam information
                Intent dataRetrieverService = new Intent(this, RetrieveDataFromSteamIntentService.class);
                startService(dataRetrieverService);
            }
        }
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;

        } else if (id == R.id.nav_logout) {
            PreferenceManager.getDefaultSharedPreferences(this).edit().remove(SharedPreferencesHelper.STEAM_ID).apply();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_send) {
            make(coordinatorLayout, "Test notification", Snackbar.LENGTH_LONG).show();
            NotificationUtils.newGameDetected(this);
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

    /**
     * Method that sort the items of a RecyclerView and then refresh it.
     *
     * @param recyclerViewToSort RecyclerView that we want to sort and refresh.
     */
    private void sortAndShowGameItemList(RecyclerView recyclerViewToSort) {

        // We have to get the adapters
        GameListAdapter allGamesListAdapter = (GameListAdapter) recyclerViewToSort.getAdapter();
        if (allGamesListAdapter == null) return;
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
        recyclerViewToSort.setAdapter(allGamesListAdapter);
    }

    private void refreshUserProfileInformationFromDb(boolean refreshRecyclerViews) {

        // If the user have already used the app, the saved information will be displayed.
        Long steamID = sharedPreferences.getLong(SharedPreferencesHelper.STEAM_ID, 0);

        currentUser = userDbHelper.getUserBySteamId(mDb, steamID, true);

        if (currentUser.getUserID() == 0) return;

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

        // We have to calculate the total money that the user have spent
        Double totalMoneySpent = 0.00;
        for (OwnedGame currentOwnedGame:currentUser.getOwnedGames()) {
            if(currentOwnedGame.getGamePrice() > 0){
                totalMoneySpent = totalMoneySpent + currentOwnedGame.getGamePrice();
            }
        }

        Double nbHoursTotal = (double) currentUser.getNbMinutesPlayed() / 60;
        Double totalPricePerHour = totalMoneySpent / nbHoursTotal;
        DecimalFormat df = new DecimalFormat("#.##");
        tvTotalPricePerHour.setText(String.valueOf(df.format(totalPricePerHour))
                + sharedPreferences.getString(SharedPreferencesHelper.CURRENCY, "$") + "/h");
        String moneySpentText = String.valueOf(df.format(totalMoneySpent))
                + sharedPreferences.getString(SharedPreferencesHelper.CURRENCY, "$");
        tvMoneySpent.setText(moneySpentText);

        if (refreshRecyclerViews) {
            // We insert the data in our RecyclerViews

            List<GameListItem> recentGamesList = createGameListItemList(
                    currentUser.getRecentlyPlayedGames());
            //GameListAdapter recentGamesListAdapter =
            //new GameListAdapter(recentGamesList, this, rvRecentGamesName);
            //rvRecentGames.setAdapter(recentGamesListAdapter);
            rvRecentGames.setLayoutManager(new GridLayoutManager(this, 2));

            List<GameListItem> favoriteGamesList = createGameListItemList(
                    currentUser.getFavoriteGames());
            //GameListAdapter favoriteGamesListAdapter =
            //new GameListAdapter(favoriteGamesList, this, rvFavoriteGamesName);
            //rvFavoriteGames.setAdapter(favoriteGamesListAdapter);
            rvFavoriteGames.setLayoutManager(new GridLayoutManager(this, 2));

            List<GameListItem> allGamesList = createGameListItemList(currentUser.getOwnedGames());
            //GameListAdapter allGamesListAdapter =
            //        new GameListAdapter(allGamesList, this, rvAllGamesName);
            //rvAllGames.setAdapter(allGamesListAdapter);
            rvAllGames.setLayoutManager(new GridLayoutManager(this, 2));

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // If we haven't a requestCode and a resultCode, we return directly
        if (requestCode != 1 || resultCode != Activity.RESULT_OK) return;

        // We retrieve the information from the intent
        String newPrice = data.getStringExtra("newPrice");
        int adapterPosition = data.getIntExtra("adapterPosition", 0);
        String recyclerName = data.getStringExtra("recyclerName");
        Boolean edited = data.getBooleanExtra("edited", false);

        // We refresh the user information from DB to update currentUser
        refreshUserProfileInformationFromDb(false);

        // We make a new list to put in the recyclerViews
        List<GameListItem> newOwnedGamesList =
                createGameListItemList(currentUser.getOwnedGames());
        List<GameListItem> newFavoriteGamesList = createGameListItemList(
                currentUser.getFavoriteGames());
        List<GameListItem> newRecentGamesList = createGameListItemList(
                currentUser.getRecentlyPlayedGames());

        // Now, we have to identify the recyclerView that have opened GameDetailActivity
        // We prepare a variable to get the adapter
        GameListAdapter gameListAdapter;
        switch (recyclerName) {
            case rvAllGamesName: {
                gameListAdapter = (GameListAdapter) rvAllGames.getAdapter();

                // We have to update the other RecyclerView
                //GameListAdapter newGameListAdapter =
                //        new GameListAdapter(newOwnedGamesList, this, rvRecentGamesName);
                //rvRecentGames.setAdapter(newGameListAdapter);
                //GameListAdapter newFavoriteGameListAdapter =
                //        new GameListAdapter(newFavoriteGamesList, this, rvFavoriteGamesName);
                //rvFavoriteGames.setAdapter(newFavoriteGameListAdapter);

                sortAndShowGameItemList(rvFavoriteGames);
                sortAndShowGameItemList(rvRecentGames);

                break;
            }
            case rvFavoriteGamesName: {

                gameListAdapter = (GameListAdapter) rvFavoriteGames.getAdapter();

                // We have to update the other RecyclerView
                /*GameListAdapter newGameListAdapter =
                        new GameListAdapter(newOwnedGamesList, this, rvRecentGamesName);
                rvAllGames.setAdapter(newGameListAdapter);
                GameListAdapter newRecentGameListAdapter =
                        new GameListAdapter(newRecentGamesList, this, rvFavoriteGamesName);
                rvRecentGames.setAdapter(newRecentGameListAdapter);*/

                sortAndShowGameItemList(rvAllGames);
                sortAndShowGameItemList(rvRecentGames);

                break;
            }
            default: {
                gameListAdapter = (GameListAdapter) rvRecentGames.getAdapter();

                // We have to update the other RecyclerView
/*                GameListAdapter newGameListAdapter =
                        new GameListAdapter(newOwnedGamesList, this, rvAllGamesName);
                rvAllGames.setAdapter(newGameListAdapter);
                GameListAdapter newFavoriteGameListAdapter =
                        new GameListAdapter(newFavoriteGamesList, this, rvFavoriteGamesName);
                rvFavoriteGames.setAdapter(newFavoriteGameListAdapter);*/
                sortAndShowGameItemList(rvAllGames);
                sortAndShowGameItemList(rvFavoriteGames);
                break;
            }
        }
        if (recyclerName.equals(rvFavoriteGamesName) && edited) {
//            GameListAdapter newFavoriteGameListAdapter =
//                    new GameListAdapter(newFavoriteGamesList, this, rvFavoriteGamesName);
//            rvFavoriteGames.setAdapter(newFavoriteGameListAdapter);
            sortAndShowGameItemList(rvFavoriteGames);
        } else {
            if (newPrice != null &&
                    !newPrice.equals(getResources().getString(R.string.free))) {
                // We update only the item updated in the RecyclerView used to open the activity
                GameListItem itemToEdit = gameListAdapter.getGameList().get(adapterPosition);
                itemToEdit.setGamePrice(Double.valueOf(newPrice));
                gameListAdapter.getGameList().set(adapterPosition, itemToEdit);
                gameListAdapter.notifyItemChanged(adapterPosition);
            }
        }
    }

    /**
     * This function transforms a List<OwnedGame> into a List<GameListItem> usable by the
     * GameListAdapters.
     *
     * @param ownedGames List<OwnedGame> that you want to convert.
     * @return List<GameListItem> that can be used by GameListAdapters.
     */
    public static List<GameListItem> createGameListItemList(List<OwnedGame> ownedGames){
        // TODO Unit tests
        List<GameListItem> gameListItems = new ArrayList<>();
        GameListItem item;

        for (OwnedGame ownedGame : ownedGames){
            item = new GameListItem();
            item.setGameTimePlayed(ownedGame.getTimePlayedForever());
            item.setGameImage(ownedGame.getGame().getGameLogo());
            item.setGameName(ownedGame.getGame().getGameName());
            item.setGamePrice(ownedGame.getGamePrice());
            item.setGameID(ownedGame.getGame().getGameID());
            item.setUserID(ownedGame.getUserId());
            gameListItems.add(item);
        }

        return gameListItems;
    }

    /**
     * This class is created to receive the response from the service responsible of Steam data
     * loading.
     */
    public class SteamDataReceiver extends BroadcastReceiver {

        public static final String PROCESS_RESPONSE =
                "com.joffreylagut.mysteamgames.mysteamgames.intent.action.PROCESS_RESPONSE";

        @Override
        public void onReceive(Context context, Intent intent) {
            pbLoading.setVisibility(View.INVISIBLE);
            lastRefresh = new Date();

            if (currentUser == null) {
                refreshUserProfileInformationFromDb(true);
            } else {
                Snackbar snackbar = Snackbar.make(coordinatorLayout,
                        R.string.new_steam_data_loaded, Snackbar.LENGTH_LONG)
                        .setAction(R.string.snackbar_action_refresh, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                refreshUserProfileInformationFromDb(true);
                            }
                        });
                snackbar.show();
            }
        }
    }
}
