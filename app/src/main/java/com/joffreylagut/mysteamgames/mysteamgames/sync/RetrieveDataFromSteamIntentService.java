package com.joffreylagut.mysteamgames.mysteamgames.sync;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.joffreylagut.mysteamgames.mysteamgames.data.UserDbHelper;
import com.joffreylagut.mysteamgames.mysteamgames.models.Game;
import com.joffreylagut.mysteamgames.mysteamgames.models.OwnedGame;
import com.joffreylagut.mysteamgames.mysteamgames.models.User;
import com.joffreylagut.mysteamgames.mysteamgames.ui.GameListActivity;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SharedPreferencesHelper;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SteamAPICalls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.joffreylagut.mysteamgames.mysteamgames.utilities.SteamAPICalls.createGameImageURL;

/**
 * RetrieveDataFromSteamIntentService.java
 * Purpose: Handle all the instrumental tests for RetrieveDataFromSteamIntentService.java
 *
 * @author Joffrey LAGUT
 * @version 1.1 2017-04-07
 */

public class RetrieveDataFromSteamIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public RetrieveDataFromSteamIntentService() {
        super("RetrieveDataFromSteamIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        // We are declaring a new UserDbHelper to access to the db.
        UserDbHelper userDbHelper = UserDbHelper.getInstance(this);
        SQLiteDatabase mDb = userDbHelper.getWritableDatabase();
        // We load the SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        URL[] listURLAPIToCall = {
                SteamAPICalls.getURLPlayerProfileInformation(sharedPreferences.getLong(SharedPreferencesHelper.STEAM_ID, 0)),
                SteamAPICalls.getURLPlayerOwnedGames(sharedPreferences.getLong(SharedPreferencesHelper.STEAM_ID, 0))
        };
        // Do some validation here
        String[] responses = new String[listURLAPIToCall.length];
        int i = 0;
        for (URL currentURL : listURLAPIToCall) {
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
        User user = new User();
        if (!responses[0].equals("Error with userJSON")){
            user = InsertSteamJsonUserInformationInDb(this, responses[0]);
        }
        if (!responses[1].equals("Error with gamesJSON")) {
            InsertSteamJsonUserGamesInDb(this, responses[1], user.getUserID());
        }

        Intent broadcastIntent = new Intent();
        broadcastIntent.putExtra("newGameDetected", false);
        broadcastIntent.setAction(GameListActivity.SteamDataReceiver.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(broadcastIntent);
    }

    /**
     * This method is parsing the String in parameter to insert the user in database.
     * The user is updated if already in db.
     *
     * @param context Context
     * @param JSONUserProfile json converted into a string.
     */
    static User InsertSteamJsonUserInformationInDb(Context context, String JSONUserProfile) {

        try {
            // We are directly going to the object containing our player information.
            JSONObject player = new JSONObject(JSONUserProfile)
                    .getJSONObject("response")
                    .getJSONArray("players").getJSONObject(0);

            // We create a new user with the information from the JSON
            User currentUser = new User();
            currentUser.setSteamID(Long.valueOf(player.getString("steamid")));
            currentUser.setAccountName(player.getString("personaname"));
            URL accountPictureUrl = null;
            try {
                accountPictureUrl = new URL(player.getString("avatarfull"));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            currentUser.setAccountPicture(accountPictureUrl);

            // We check if the user already exist in db
            UserDbHelper userDbHelper = UserDbHelper.getInstance(context);
            SQLiteDatabase mDb = userDbHelper.getWritableDatabase();
            User userInDB = userDbHelper.getUserBySteamId(mDb, currentUser.getSteamID(), false);

            if (userInDB.getUserID() != 0) {
                // The user already exist, we have to update his information.
                userDbHelper.updateUserBySteamID(mDb, currentUser);
                currentUser.setUserID(userInDB.getUserID());
            } else {
                // The user doesn't exist, we have to insert him in DB.
                currentUser = userDbHelper.addNewUser(mDb, currentUser);
            }

            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putInt("userId", currentUser.getUserID());
            editor.apply();

            return currentUser;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new User();
    }

    /**
     * This method is parsing the String in parameter to insert the games owned by the user in database.
     * The games are updated if already in db.
     *
     * @param context Context
     * @param JSONUserGames  json converted into a string.
     */
    public static void InsertSteamJsonUserGamesInDb(Context context, String JSONUserGames, int userId) {
        // TODO Fix the error bellow
        UserDbHelper userDbHelper = UserDbHelper.getInstance(context);
        SQLiteDatabase db = userDbHelper.getWritableDatabase();

        try {

            JSONArray jsonArrayGames = new JSONObject(JSONUserGames).getJSONObject("response").getJSONArray("games");
            for (int i = 0; i < jsonArrayGames.length(); i++) {

                // We retrieve and store all of the information in variables.
                JSONObject jsonGame = jsonArrayGames.getJSONObject(i);

                Game game = new Game(jsonGame.getString("name"));

                // Now we create a new OwnedGame object.
                OwnedGame ownedGame = new OwnedGame(userId, game);

                ownedGame.setUserId(userId);
                ownedGame.getGame().setSteamID(jsonGame.getLong("appid"));
                ownedGame.getGame().setGameName(jsonGame.getString("name"));
                if (jsonGame.has("playtime_2weeks")) {
                    ownedGame.setTimePlayed2Weeks(jsonGame.getInt("playtime_2weeks"));
                }
                ownedGame.setTimePlayedForever(jsonGame.getInt("playtime_forever"));
                ownedGame.getGame().setGameIcon(createGameImageURL(jsonGame.getString("img_icon_url"), ownedGame.getGame().getSteamID()));
                ownedGame.getGame().setGameLogo(createGameImageURL(jsonGame.getString("img_logo_url"), ownedGame.getGame().getSteamID()));
                ownedGame.getGame().setMarketplace("Steam");


                // First, we check if the game already exist in db.
                Game gameFromDb = userDbHelper.getGameBySteamID(db, ownedGame.getGame().getSteamID());
                if (gameFromDb.getGameID() != 0) {
                    ownedGame.getGame().setGameID(gameFromDb.getGameID());
                    // The game already exist, we have to update it in db.
                    userDbHelper.updateGameById(db, ownedGame.getGame());
                } else {
                    // The game doesn't exist, we have to add it in db.
                    ownedGame.setGame(userDbHelper.addNewGame(db, ownedGame.getGame()));
                }

                // Now that the game is in DB, we have to check if the game is already owned by the user.
                OwnedGame ownedGameFromDb = userDbHelper.getOwnedGame(db, userId, ownedGame.getGame().getGameID());
                if(ownedGameFromDb == null){
                    // The user don't own the game yet, we have to add it in db
                    userDbHelper.addNewOwnedGame(db,ownedGame);
                }else{
                    // We update the game
                    userDbHelper.updateOwnedGame(db, ownedGame);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
