package com.joffreylagut.mysteamgames.mysteamgames.data;

/**
 * Created by Joffrey on 10/02/2017.
 */

/**
 * This class contains the app preference variables.
 *
 */
public class AppPreferences {

    // API Key to use the steam Web API.
    private static String apiKey = "B1F028BA4B3F02C594462737E055DB44";

    // Steam ID of the user who is using the app. By default, Slayde's steam id.
    private static String userSteamID = "76561198052789807";
    //private static String userSteamID = "76561198090115246";

    static public String getApiKey() {
        return apiKey;
    }

    static public void setApiKey(String apiKey) {
        AppPreferences.apiKey = apiKey;
    }

    public static String getUserSteamID() {
        return userSteamID;
    }

    public static void setUserSteamID(String userSteamID) {
        AppPreferences.userSteamID = userSteamID;
    }
}
