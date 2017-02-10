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


}
