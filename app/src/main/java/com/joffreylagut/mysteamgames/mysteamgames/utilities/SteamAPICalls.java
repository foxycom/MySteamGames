package com.joffreylagut.mysteamgames.mysteamgames.utilities;

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * SteamAPICalls.java
 * Purpose: Contains all the methods relatives to the Steam API.
 *
 * @author Joffrey LAGUT
 * @version 1.6 2017-05-15
 */

public class SteamAPICalls {

    private static final String TAG = SteamAPICalls.class.getSimpleName();

    // API Key to use the steam Web API.
    static final String API_KEY = "B1F028BA4B3F02C594462737E055DB44";

    final static String STEAM_API_BASE_URL = "http://api.steampowered.com";
    final static String STEAM_CALL_USER_PROFILE_URL = "/ISteamUser/GetPlayerSummaries/v0002/";
    final static String STEAM_CALL_USER_OWNED_GAMES_URL = "/IPlayerService/GetOwnedGames/v0001/";

    /**
     * This function return the URL of the User profile JSON from the Steam API.
     * @param steamPlayerId Id of the player that we want to fetch the information.
     * @return the URL of the JSON User profile.
     */
    public static URL getURLPlayerProfileInformation(Long steamPlayerId){
        URL finalUrl = null;
        Uri urlBuilt = Uri.parse(STEAM_API_BASE_URL + STEAM_CALL_USER_PROFILE_URL)
                .buildUpon()
                .appendQueryParameter("key", API_KEY)
                .appendQueryParameter("steamids",String.valueOf(steamPlayerId))
                .build();

        try {
            finalUrl = new URL(urlBuilt.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "getURLPlayerProfileInformation: Unable to create the URL. ", e);
        }

        return finalUrl;
    }

    /**
     * This function return the URL of the User games JSON from the Steam API.
     * @param steamPlayerId Id of the player that we want to fetch the information.
     * @return the URL of the JSON User games.
     */
    public static URL getURLPlayerOwnedGames(long steamPlayerId){
        URL finalUrl = null;
        Uri urlBuilt = Uri.parse(STEAM_API_BASE_URL + STEAM_CALL_USER_OWNED_GAMES_URL)
                .buildUpon()
                .appendQueryParameter("key", API_KEY)
                .appendQueryParameter("include_appinfo","1")
                .appendQueryParameter("steamid",String.valueOf(steamPlayerId))
                .build();

        try {
            finalUrl = new URL(urlBuilt.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "getURLPlayerOwnedGames: Unable to create the URL. ", e);
        }
        return finalUrl;
    }

    /**
     * This function create the URL to the games pictures on the Steam server.
     * @param imageID ID found in the JSON user games.
     * @param appID Game id from the JSON user games.
     * @return an URL to the image.
     */
    public static URL createGameImageURL(String imageID, long appID){
        String composedURL = "http://media.steampowered.com/steamcommunity/public/images/apps/" +
                appID + "/" + imageID + ".jpg";
        URL finalURL = null;
        try {
            finalURL = new URL(composedURL);
        } catch (MalformedURLException e) {
            Log.e(TAG, "getURLPlayerProfileInformation: Unable to create the URL. ", e);
        }
        return finalURL;
    }

}
