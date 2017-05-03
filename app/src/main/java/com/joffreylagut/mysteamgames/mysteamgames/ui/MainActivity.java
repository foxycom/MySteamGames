package com.joffreylagut.mysteamgames.mysteamgames.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.joffreylagut.mysteamgames.mysteamgames.R;
import com.joffreylagut.mysteamgames.mysteamgames.data.MainPagerAdapter;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.BottomNavigationViewHelper;

import java.util.HashMap;

/**
 * MainActivity.java
 * Purpose: Main activity of the application. Allow the user to access to the main functionality.
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-05-03
 */

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

    private final Integer HOME_FRAGMENT_CODE = 0;
    private final Integer GOALS_FRAGMENT_CODE = 1;
    private final Integer SUCCESS_FRAGMENT_CODE = 2;
    private final Integer ACCOUNT_FRAGMENT_CODE = 3;

    BottomNavigationView mBottomNavigationView;

    ViewPager mPager;
    MainPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        BottomNavigationViewHelper.removeShiftMode(mBottomNavigationView);
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);

        mPager = (ViewPager) findViewById(R.id.view_pager);

        HashMap<Integer, Fragment> activityFragments = new HashMap<>();
        activityFragments.put(HOME_FRAGMENT_CODE, new HomeFragment());
        activityFragments.put(GOALS_FRAGMENT_CODE, new GoalsFragment());
        activityFragments.put(SUCCESS_FRAGMENT_CODE, new SuccessFragment());
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
            case R.id.action_success:
                mPager.setCurrentItem(SUCCESS_FRAGMENT_CODE);
                break;
            case R.id.action_account:
                mPager.setCurrentItem(ACCOUNT_FRAGMENT_CODE);
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
        } else if (position == SUCCESS_FRAGMENT_CODE) {
            mBottomNavigationView.setSelectedItemId(R.id.action_success);
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
}
