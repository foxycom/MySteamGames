package com.joffreylagut.mysteamgames.mysteamgames;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.joffreylagut.mysteamgames.mysteamgames.customclass.Game;
import com.joffreylagut.mysteamgames.mysteamgames.customclass.GameListItem;
import com.joffreylagut.mysteamgames.mysteamgames.customclass.OwnedGame;
import com.joffreylagut.mysteamgames.mysteamgames.customclass.User;
import com.joffreylagut.mysteamgames.mysteamgames.data.UserContract;
import com.joffreylagut.mysteamgames.mysteamgames.data.UserDbHelper;
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
import java.util.Collections;
import java.util.List;

import static com.joffreylagut.mysteamgames.mysteamgames.utilities.SteamAPICalls.createGameImageURL;

public class MainActivity extends AppCompatActivity implements GameListAdapter.ListItemClickListener {

    private static final String TAG = "MainActivity";
    Double totalMoneySpent = 0.00;
    UserDbHelper userDbHelper;
    SharedPreferences sharedPreferences;
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
    private RecyclerView recyclerView;
    private TextView tvTotalPricePerHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Games list");

        Log.d(TAG, "onCreate");

        // Stepho is providing us an access to the mobile database with Chrome.
        // We initialize it here.
        Stetho.initializeWithDefaults(this);

        // First, we are linking our views with the layout
        pbLoading = (ProgressBar)findViewById(R.id.pb_loading);
        tvAccountName = (TextView)findViewById(R.id.tv_account_name);
        recyclerView = (RecyclerView) findViewById(R.id.rv_games);
        ivProfile = (ImageView)findViewById(R.id.iv_profile);
        tvNumberOfGames = (TextView)findViewById(R.id.tv_nb_games);
        tvTotalTimePlayed = (TextView)findViewById(R.id.tv_total_time_played);
        tvTotalPricePerHour = (TextView) findViewById(R.id.tv_total_price_per_hour);

        // We are declaring a new UserDbHelper to access to the db.
        userDbHelper = UserDbHelper.getInstance(this);
        mDb = userDbHelper.getWritableDatabase();

        // We load the SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // If the user have already used the app, the saved informations will be displayed.
        String newSteamID = sharedPreferences.getString("etp_steamID", "");
        refreshUserProfileInformationFromDb(newSteamID);

        //refreshUserProfileInformationFromDb(AppPreferences.getUserSteamID());

        // We are generating the URL and then ask the Steam API

        SteamAPICalls.getURLPlayerProfileInformation(sharedPreferences.getString("etp_steamID", ""));
        RetrieveProfileInformation myProfileInformation = new RetrieveProfileInformation();
        URL[] listURLAPIToCall = {
                SteamAPICalls.getURLPlayerProfileInformation(currentUser.getSteamID()),
                SteamAPICalls.getURLPlayerOwnedGames(currentUser.getSteamID())};
        myProfileInformation.execute(listURLAPIToCall);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_list_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_button_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshUserProfileInformationFromDb(String steamID){

        currentUser.setSteamID(steamID);

        // We start by getting the user information in user Table.
        Cursor result = userDbHelper.getUserBySteamID(mDb, steamID);
        if(result.getCount() != 0){
            currentUser.setUserID(Integer.parseInt(result.getString(
                    result.getColumnIndex(UserContract.UserEntry._ID))
            ));
            currentUser.setAccountName(result.getString(
                    result.getColumnIndex(UserContract.UserEntry.COLUMN_ACCOUNT_NAME)));
            try {
                currentUser.setAccountPicture(new URL(result.getString(
                        result.getColumnIndex(UserContract.UserEntry.COLUMN_ACCOUNT_PICTURE))));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }else{
            currentUser.setUserID(0);
            currentUser.setAccountName(null);
            currentUser.setAccountPicture(null);
        }

        // Then, we get the list of owned games from OwnedGames table.
        List<OwnedGame> userOwnedGames = new ArrayList<>();
        OwnedGame currentOwnedGame;
        Game currentGame;
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
        tvNumberOfGames.setText(String.valueOf(currentUser.getOwnedGames().size()) + " " + getResources().getString(R.string.games));

        tvTotalTimePlayed.setText(SteamAPICalls.convertTimePlayed(currentUser.getNbMinutesPlayed()) + " " + getResources().getString(R.string.played));

        if(currentUser.getAccountPicture() != null){
            String urlImageToLoad = currentUser.getAccountPicture().toString();
            Picasso.with(this).load(urlImageToLoad).into(ivProfile);
        }
        Double nbHoursTotal = Double.valueOf(currentUser.getNbMinutesPlayed()) / 60;
        Double totalPricePerHour = totalMoneySpent / nbHoursTotal;
        DecimalFormat df = new DecimalFormat("#.##");
        tvTotalPricePerHour.setText(String.valueOf(df.format(totalPricePerHour)) + " " + sharedPreferences.getString("lp_currency", "€") + "/h");

        // We insert the data in our RecyclerView
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        List<GameListItem> sortedList = createGameListItemList(currentUser.getOwnedGames());

        Collections.sort(sortedList);

        GameListAdapter gameListAdapter = new GameListAdapter(sortedList, this, this);
        recyclerView.setAdapter(gameListAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String newPrice = data.getStringExtra("newPrice");
                int adapterPosition = data.getIntExtra("adapterPosition", 0);
                GameListAdapter gameListAdapter = (GameListAdapter) recyclerView.getAdapter();
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
            String steamID = player.getString("steamid");
            String accountName = player.getString("personaname");
            String accountPicture = player.getString("avatarfull");

            // We first check if the user already exist in db
            userDbHelper = UserDbHelper.getInstance(this);
            Cursor userInDB = userDbHelper.getUserBySteamID(mDb, steamID);
            int nbRow = userInDB.getCount();
            if(userInDB.getCount() != 0){
                // The user already exist, we have to update his informations.
                userDbHelper.updateUserBySteamID(mDb, steamID,accountName, accountPicture);
            }else{
                // The user doesn't exist, we have to insert him in DB.
                userDbHelper.addNewUser(mDb, steamID, accountName, accountPicture);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void ParseJSONGames(String JSONGames){
        try {
            JSONArray jsonArrayGames = new JSONObject(JSONGames).getJSONObject("response").getJSONArray("games");
            Cursor user;
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

                // We retrieve the UserID in DB.
                user = userDbHelper.getUserBySteamID(mDb, currentUser.getSteamID());
                int idUser = 0;
                if(user.getCount() != 0){
                    idUser = user.getInt(user.getColumnIndex(UserContract.UserEntry._ID));
                }

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

                cursor = userDbHelper.getOwnedGame(mDb, String.valueOf(idUser), gameID);
                if(cursor.getCount() != 0){
                    // The game is already owned by the user, we have to update it.
                    userDbHelper.updateOwnedGame(mDb, String.valueOf(idUser), gameID,
                            playtime_forever, playtime_2weeks, null, null);
                }else{
                    // The game isn't already owned in db, we add it.
                    userDbHelper.addNewOwnedGame(mDb, String.valueOf(idUser), gameID,
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
            refreshUserProfileInformationFromDb(currentUser.getSteamID());

        }
    }
}
