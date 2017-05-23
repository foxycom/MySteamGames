package com.joffreylagut.mysteamgames.mysteamgames.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.joffreylagut.mysteamgames.mysteamgames.R;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SharedPreferencesHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * SettingsActivity.java
 * Purpose: Display the settings fragment and the logout button.
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-05-23
 */

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.action_logout)
    Button mBtnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(getResources().getString(R.string.settings_activity_title));
        ButterKnife.bind(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // We want to display an animation to go back on the previous activity
        overridePendingTransition(R.transition.left_to_right_incoming,
                R.transition.left_to_right_outgoing);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @OnClick(R.id.action_logout)
    public void logout(View v) {
        PreferenceManager.getDefaultSharedPreferences(this).edit().remove(SharedPreferencesHelper.STEAM_ID).apply();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}