package com.joffreylagut.mysteamgames.mysteamgames.utilities;

import android.net.Uri;

import com.joffreylagut.mysteamgames.mysteamgames.data.AppPreferences;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Joffrey on 10/02/2017.
 */

public class SteamAPICalls {

    final static String steamAPIBaseUrl = "http://api.steampowered.com";
    final static String steamCallProfileURL = "/ISteamUser/GetPlayerSummaries/v0002/";
    final static String steamCallOwnedGamesURL = "/IPlayerService/GetOwnedGames/v0001/";

    public static URL getURLPlayerProfileInformation(String steamPlayerId){
        URL finalUrl = null;
        Uri urlBuilt = Uri.parse(steamAPIBaseUrl + steamCallProfileURL)
                .buildUpon()
                .appendQueryParameter("key", AppPreferences.getApiKey())
                .appendQueryParameter("steamids",steamPlayerId)
                .build();

        try {
            finalUrl = new URL(urlBuilt.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return finalUrl;
    }

    public static URL getURLPlayerOwnedGames(String steamPlayerId){
        URL finalUrl = null;
        Uri urlBuilt = Uri.parse(steamAPIBaseUrl + steamCallOwnedGamesURL)
                .buildUpon()
                .appendQueryParameter("key", AppPreferences.getApiKey())
                .appendQueryParameter("include_appinfo","1")
                .appendQueryParameter("steamid",steamPlayerId)
                .build();

        try {
            finalUrl = new URL(urlBuilt.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return finalUrl;
    }

    public static String convertTimePlayed(int totalMinutes, boolean showBoth) {
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;
        if (!showBoth) {
            if (hours == 0) {
                return String.valueOf(minutes) + "mn";
            } else {
                return String.valueOf(hours) + "h";
            }
        }else{
            return String.valueOf(hours) + "h" + String.valueOf(minutes) + "mn";
        }

    }

    public static String convertTimePlayed(int totalMinutes) {
        return convertTimePlayed(totalMinutes, false);
    }

    public static URL createGameImageURL(String imageID, String appID){
        String composedURL = "http://media.steampowered.com/steamcommunity/public/images/apps/" +
                appID + "/" + imageID + ".jpg";
        URL finalURL = null;
        try {
            finalURL = new URL(composedURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return finalURL;
    }

}
