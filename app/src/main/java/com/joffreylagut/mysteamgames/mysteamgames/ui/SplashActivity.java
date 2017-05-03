package com.joffreylagut.mysteamgames.mysteamgames.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;

/**
 * SplashActivity.java
 * Purpose: This activity is analysing if this is the first launched or not.
 *
 * @author Joffrey LAGUT
 * @version 1.5 2017-04-08
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // There is no Layout in this activity. The background is defined in a theme.



        // We need to check in the preferences if the SteamID is defined.
        // We load the SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Long steamID = sharedPreferences.getLong("etp_steamID", 0);
        if (steamID != 0) {
            // The user have already used the app. We can launch the main activity.
            Intent intent = new Intent(this, GameListActivity.class);
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