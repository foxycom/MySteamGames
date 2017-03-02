package com.joffreylagut.mysteamgames.mysteamgames;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;

/**
 * Created by Joffrey on 01/03/2017.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // There is no Layout in this activity. The background is defined in a theme.

        // ------------------------------------------------------------------
        // CODE FOR TESTING
        //PreferenceManager.getDefaultSharedPreferences(this).edit().remove("etp_steamID").commit();
        // ------------------------------------------------------------------

        // We need to check in the preferences if the SteamID is defined.
        // We load the SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Long steamID = Long.valueOf(sharedPreferences.getString("etp_steamID", "0"));
        if (steamID != 0) {
            // The user have already used the app. We can launch the main activity.
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            // This is the first launch. We have to ask the user to enter his SteamID.
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        // We finish this activity.
        finish();
    }
}