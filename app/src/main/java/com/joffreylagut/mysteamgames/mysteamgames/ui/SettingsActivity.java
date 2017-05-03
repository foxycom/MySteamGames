package com.joffreylagut.mysteamgames.mysteamgames.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.joffreylagut.mysteamgames.mysteamgames.R;

/**
 * Created by Joffrey on 27/02/2017.
 */

public class SettingsActivity extends AppCompatActivity {
    private static String mSteamID;
    private static String mCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(getResources().getString(R.string.activity_settings_title));
    }


}