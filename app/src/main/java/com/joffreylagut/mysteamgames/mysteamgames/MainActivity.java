package com.joffreylagut.mysteamgames.mysteamgames;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.joffreylagut.mysteamgames.mysteamgames.customclass.GameListItem;
import com.joffreylagut.mysteamgames.mysteamgames.customclass.User;
import com.joffreylagut.mysteamgames.mysteamgames.data.AppPreferences;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements GameListAdapter.ListItemClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        // We are declaring a new UserDbHelper to access to the db.
        UserDbHelper userDbHelper = new UserDbHelper(this);
        mDb = userDbHelper.getWritableDatabase();

        // If the user have already used the app, the saved informations will be displayed.
        refreshUserProfileInformationFromDb(AppPreferences.getUserSteamID());

        // Then, we are putting values inside our current user
        currentUser.setSteamID(AppPreferences.getUserSteamID());
        currentUser.setGameList(insertGameData());

        // We are generating the URL and then ask the Steam API
        SteamAPICalls.getURLPlayerProfileInformation(AppPreferences.getUserSteamID());
        RetrieveProfileInformation myProfileInformation = new RetrieveProfileInformation();
        URL[] listURLAPIToCall = {
                SteamAPICalls.getURLPlayerProfileInformation(currentUser.getSteamID()),
                SteamAPICalls.getURLPlayerOwnedGames(currentUser.getSteamID())};
        myProfileInformation.execute(listURLAPIToCall);

    }

    private void refreshUserProfileInformationFromDb(String steamID){

        UserDbHelper userDbHelper = new UserDbHelper(this);

        Cursor result = userDbHelper.getUserBySteamID(mDb, steamID);
        if(result.getCount() != 0){
            currentUser.setAccountName(result.getString(
                    result.getColumnIndex(UserContract.UserEntry.COLUMN_ACCOUNT_NAME)));
            try {
                currentUser.setAccountPicture(new URL(result.getString(
                        result.getColumnIndex(UserContract.UserEntry.COLUMN_ACCOUNT_PICTURE))));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

    }
    /**
     * Insert data sample in the gamelist.
     */
    private List<GameListItem> insertGameData() {
        List<GameListItem> gameList = new ArrayList<>();
        return gameList;
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
            UserDbHelper userDbHelper = new UserDbHelper(this);
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
            int totalPlayedTime = 0;
            List<GameListItem> gamesList = new ArrayList<>();
            GameListItem gameToAdd = new GameListItem();

            for (int i=0; i < jsonArrayGames.length(); i++){

                JSONObject jsonGame = jsonArrayGames.getJSONObject(i);
                gameToAdd = new GameListItem();
                gameToAdd.setGameSteamID(Integer.parseInt(jsonGame.getString("appid")));
                gameToAdd.setGameName(jsonGame.getString("name"));
                gameToAdd.setGameTimePlayed(Integer.parseInt(jsonGame.getString("playtime_forever")));
                URL urlImage = new URL("http://media.steampowered.com/steamcommunity/public/images/apps/" + gameToAdd.getGameSteamID() + "/" + jsonGame.getString("img_logo_url") + ".jpg");
                gameToAdd.setGameImage(urlImage);
                gamesList.add(gameToAdd);

                totalPlayedTime = totalPlayedTime + gameToAdd.getGameTimePlayed();
            }
            currentUser.setGameList(gamesList);
            currentUser.setNbMinutesPlayed(totalPlayedTime);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    private void displayInformation(){
        refreshUserProfileInformationFromDb(currentUser.getSteamID());

        // Now we can display the user's information.
        tvAccountName.setText(currentUser.getAccountName());
        tvTotalTimePlayed.setText(String.valueOf(currentUser.getNbMinutesPlayed()));
        tvNumberOfGames.setText(String.valueOf(currentUser.getGameList().size()) + " " + getResources().getString(R.string.games));

        String convertedTime = Long.toString(TimeUnit.HOURS.convert(currentUser.getNbMinutesPlayed(), TimeUnit.MINUTES));
        tvTotalTimePlayed.setText(convertedTime + " " + getResources().getString(R.string.hoursplayed));

        String urlImageToLoad = currentUser.getAccountPicture().toString();
        Picasso.with(this).load(urlImageToLoad).into(ivProfile);

        //currentUser.setGameList(insertGameData());

        // We insert the data in our RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        GameListAdapter gameListAdapter = new GameListAdapter(currentUser.getGameList(), this);
        recyclerView.setAdapter(gameListAdapter);

    }
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
            return responses;
        }

        protected void onPostExecute(String[] responses) {

            // TODO : Generate errors to be sure that the application can run even without a JSON
            pbLoading.setVisibility(View.GONE);
            Log.i("INFO", responses[0]);
            Log.i("INFO", responses[1]);
            if(!responses[0].equals("Error")) ParseJSONProfile(responses[0]);
            if(!responses[1].equals("Error")) ParseJSONGames(responses[1]);
            displayInformation();
        }
    }
}
