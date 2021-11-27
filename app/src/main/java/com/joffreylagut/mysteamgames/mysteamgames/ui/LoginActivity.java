package com.joffreylagut.mysteamgames.mysteamgames.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.joffreylagut.mysteamgames.mysteamgames.R;
import com.joffreylagut.mysteamgames.mysteamgames.sync.RetrieveDataFromSteamIntentService;
import com.joffreylagut.mysteamgames.mysteamgames.sync.SteamDataReceiver;

/**
 * LoginActivity.java
 * Purpose: Handle the fragment management for the login.
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-05-24
 */

public class LoginActivity extends AppCompatActivity implements SteamLoginFragment.OnLoginFinishedListener, SteamDataReceiver.OnReceivedFinishedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.activity_login_title);

        LoginFragment fragment = new LoginFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    public void onClickLogin(View v) {
        SteamLoginFragment fragment = new SteamLoginFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Display the loading message and wait until the IntentService have done his job to start
     * a new activity.
     */
    @Override
    public void onLoginFinished() {

        findViewById(R.id.fragment_container).setVisibility(View.GONE);
        findViewById(R.id.loading_linear_layout).setVisibility(View.VISIBLE);

        createAndRegisterSteamDataReceiver();

        Intent dataRetrieverService = new Intent(this, RetrieveDataFromSteamIntentService.class);
        startService(dataRetrieverService);

    }

    /**
     * Create an IntentFilter and register a SteamDataReceiver.
     */
    private void createAndRegisterSteamDataReceiver() {
        IntentFilter filter = new IntentFilter(SteamDataReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        SteamDataReceiver receiver = new SteamDataReceiver();
        registerReceiver(receiver, filter);
    }

    /**
     * Called when SteamDataReceiver have received an intent from RetrieveDataFromSteamIntentService.
     */
    @Override
    public void onReceiveFinished() {
        // The user have already used the app. We can launch the main activity.
        Intent intentMainActivity = new Intent(this, MainActivity.class);
        intentMainActivity.putExtra(MainActivity.ARG_FIRST_LAUNCH, true);
        startActivity(intentMainActivity);
        finish();
    }
}
