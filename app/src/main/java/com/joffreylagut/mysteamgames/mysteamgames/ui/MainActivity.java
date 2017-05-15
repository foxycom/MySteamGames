package com.joffreylagut.mysteamgames.mysteamgames.ui;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.SparseArray;
import android.view.MenuItem;

import com.facebook.stetho.Stetho;
import com.joffreylagut.mysteamgames.mysteamgames.R;
import com.joffreylagut.mysteamgames.mysteamgames.data.MainPagerAdapter;
import com.joffreylagut.mysteamgames.mysteamgames.sync.RetrieveDataFromSteamIntentService;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SharedPreferencesHelper;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.UiUtilities;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * MainActivity.java
 * Purpose: Main activity of the application. Allow the user to access to the main functionality.
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-05-03
 */

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener, HomeFragment.OnGameSelectedListener, GamesFragment.OnGameSelectedListener, GoalsFragment.OnGameSelectedListener {

    private final Integer HOME_FRAGMENT_CODE = 0;
    private final Integer GOALS_FRAGMENT_CODE = 1;
    private final Integer FINISHED_FRAGMENT_CODE = 2;
    private final Integer GAMES_FRAGMENT_CODE = 3;
    private final Integer ACCOUNT_FRAGMENT_CODE = 4;

    @BindView(R.id.bottom_navigation_view)
    BottomNavigationView mBottomNavigationView;

    @BindView(R.id.home_view_pager)
    ViewPager mPager;
    MainPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Stetho.initializeWithDefaults(this);

        // We start the service that retrieve steam information
        Intent dataRetrieverService = new Intent(this, RetrieveDataFromSteamIntentService.class);
        startService(dataRetrieverService);

        // We disable the shift mode of the bottom navigation view
        UiUtilities.removeBottomNavigationShiftMode(mBottomNavigationView);
        // And set the listener
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);

        // We create a new Hashmap and put our fragments inside
        SparseArray<Fragment> activityFragments = new SparseArray<>();
        activityFragments.put(HOME_FRAGMENT_CODE, new HomeFragment());
        activityFragments.put(GOALS_FRAGMENT_CODE, new GoalsFragment());
        activityFragments.put(FINISHED_FRAGMENT_CODE, new FinishedFragment());
        activityFragments.put(GAMES_FRAGMENT_CODE, new GamesFragment());
        activityFragments.put(ACCOUNT_FRAGMENT_CODE, new AccountFragment());

        mAdapter = new MainPagerAdapter(super.getSupportFragmentManager(), activityFragments);
        mPager.setAdapter(mAdapter);
        mPager.addOnPageChangeListener(this);

    }

    /**
     * Called when there is a click on an item from the bottom navigation view
     *
     * @param item clicked.
     * @return true
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_goals:
                mPager.setCurrentItem(GOALS_FRAGMENT_CODE);
                break;
            case R.id.action_finished:
                mPager.setCurrentItem(FINISHED_FRAGMENT_CODE);
                break;
            case R.id.action_account:
                mPager.setCurrentItem(ACCOUNT_FRAGMENT_CODE);
                break;
            case R.id.action_games:
                mPager.setCurrentItem(GAMES_FRAGMENT_CODE);
                break;
            default:
                mPager.setCurrentItem(HOME_FRAGMENT_CODE);

        }
        return true;
    }

    /**
     * Called when the user have swiped to show a new page of the view pager.
     *
     * @param position of the viewpager.
     */
    @Override
    public void onPageSelected(int position) {
        if (position == GOALS_FRAGMENT_CODE) {
            mBottomNavigationView.setSelectedItemId(R.id.action_goals);
        } else if (position == FINISHED_FRAGMENT_CODE) {
            mBottomNavigationView.setSelectedItemId(R.id.action_finished);
        } else if (position == GAMES_FRAGMENT_CODE) {
            mBottomNavigationView.setSelectedItemId(R.id.action_games);
        } else if (position == ACCOUNT_FRAGMENT_CODE) {
            mBottomNavigationView.setSelectedItemId(R.id.action_account);
        } else {
            mBottomNavigationView.setSelectedItemId(R.id.action_home);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /**
     * Called when the user click on a GameTongue, that function is starting a GameDetailsActivity.
     *
     * @param gameId of the game that we want to show.
     */
    @Override
    public void OnGameSelected(int gameId) {
        // We retrieve the userId via the sharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int userId = sharedPreferences.getInt(SharedPreferencesHelper.USER_ID, 0);

        // We create a new intent to start the GameDetailActivity
        Intent intentGameDetails = new Intent(this, GameDetailsActivity.class);
        intentGameDetails.putExtra(GameDetailsActivity.ARG_USER_ID, userId);
        intentGameDetails.putExtra(GameDetailsActivity.ARG_GAME_ID, gameId
        );

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            Bundle bndlAnimation = ActivityOptions.makeCustomAnimation(this,
                    R.transition.right_to_left_incoming, R.transition.right_to_left_outgoing)
                    .toBundle();
            startActivity(intentGameDetails, bndlAnimation);
        } else {
            startActivity(intentGameDetails);
        }
    }
}
