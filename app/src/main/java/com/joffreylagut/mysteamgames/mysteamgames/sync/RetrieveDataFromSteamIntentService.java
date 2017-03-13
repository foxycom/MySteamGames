package com.joffreylagut.mysteamgames.mysteamgames.sync;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.joffreylagut.mysteamgames.mysteamgames.MainActivity;
import com.joffreylagut.mysteamgames.mysteamgames.data.UserContract;
import com.joffreylagut.mysteamgames.mysteamgames.data.UserDbHelper;
import com.joffreylagut.mysteamgames.mysteamgames.objects.User;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SteamAPICalls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.joffreylagut.mysteamgames.mysteamgames.utilities.SteamAPICalls.createGameImageURL;

/**
 * Created by Joffrey on 09/03/2017.
 */

public class RetrieveDataFromSteamIntentService extends IntentService {

    public static final String RESPONSE_STRING = "myResponse";
    public static final String RESPONSE_MESSAGE = "myResponseMessage";
    private SQLiteDatabase mDb;
    private UserDbHelper userDbHelper;
    private SharedPreferences sharedPreferences;
    private boolean newGameDetected;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public RetrieveDataFromSteamIntentService() {
        super("RetrieveDataFromSteamIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        // We set newGameDetected to false
        newGameDetected = false;

        // We are declaring a new UserDbHelper to access to the db.
        userDbHelper = UserDbHelper.getInstance(this);
        mDb = userDbHelper.getWritableDatabase();
        // We load the SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        URL[] listURLAPIToCall = {
                SteamAPICalls.getURLPlayerProfileInformation(sharedPreferences.getString("etp_steamID", "")),
                SteamAPICalls.getURLPlayerOwnedGames(sharedPreferences.getString("etp_steamID", ""))
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
        if (!responses[0].equals("Error with userJSON")) ParseJSONProfile(responses[0]);
        if (!responses[1].equals("Error with gamesJSON")) ParseJSONGames(responses[1]);

        Intent broadcastIntent = new Intent();
        broadcastIntent.putExtra("newGameDetected", newGameDetected);
        broadcastIntent.setAction(MainActivity.SteamDataReceiver.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        sendBroadcast(broadcastIntent);
    }

    private void ParseJSONProfile(String JSONProfil) {

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
            } else {
                // The user doesn't exist, we have to insert him in DB.
                userDbHelper.addNewUser(mDb, String.valueOf(steamID)
                        , accountName, accountPicture);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void ParseJSONGames(String JSONGames) {
        try {
            JSONArray jsonArrayGames = new JSONObject(JSONGames).getJSONObject("response").getJSONArray("games");
            User user;
            int jsonArrayLenght = jsonArrayGames.length();
            for (int i = 0; i < jsonArrayGames.length(); i++) {

                // We retrieve and store all of the information in variables.
                JSONObject jsonGame = jsonArrayGames.getJSONObject(i);

                String appID = jsonGame.getString("appid");
                String name = jsonGame.getString("name");
                String playtime_2weeks = "";
                if (jsonGame.has("playtime_2weeks"))
                    playtime_2weeks = jsonGame.getString("playtime_2weeks");
                String playtime_forever = jsonGame.getString("playtime_forever");
                String img_icon_url = jsonGame.getString("img_icon_url");
                img_icon_url = createGameImageURL(img_icon_url, appID).toString();
                String img_logo_url = jsonGame.getString("img_logo_url");
                img_logo_url = createGameImageURL(img_logo_url, appID).toString();

                UserDbHelper userDbHelper = new UserDbHelper(this);

                // We retrieve the User in DB.
                user = userDbHelper.getUserBySteamID(mDb, Long.valueOf(sharedPreferences.getString("etp_steamID", "")));

                // First, we check if the game already exist in db.
                Cursor cursor = userDbHelper.getGameBySteamID(mDb, appID);
                if (cursor.getCount() != 0) {
                    // The game already exist, we have to update it in db.
                    userDbHelper.updateGameBySteamID(mDb, appID, name, img_logo_url,
                            img_icon_url, "Steam");
                } else {
                    // The game doesn't exist, we have to add it in db.
                    userDbHelper.addNewGame(mDb, appID, name, img_logo_url,
                            img_icon_url, "Steam");
                }

                // Now that the game is in DB, we have to check if the game is already owned by
                // the user in DB.
                // We retrieve the gameUD in DB.
                cursor = userDbHelper.getGameBySteamID(mDb, appID);
                String gameID = "0";
                if (cursor.getCount() != 0) {
                    gameID = cursor.getString(cursor.getColumnIndex(UserContract.GameEntry._ID));
                }

                cursor = userDbHelper.getOwnedGame(mDb, String.valueOf(user.getUserID()), gameID);
                if (cursor.getCount() != 0) {
                    // The game is already owned by the user, we have to update it.
                    userDbHelper.updateOwnedGame(mDb, String.valueOf(user.getUserID()), gameID,
                            playtime_forever, playtime_2weeks, null, null, null);
                } else {
                    // The game isn't already owned in db, we add it.
                    userDbHelper.addNewOwnedGame(mDb, String.valueOf(user.getUserID()), gameID,
                            playtime_forever, playtime_2weeks, null, null);
                    newGameDetected = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
