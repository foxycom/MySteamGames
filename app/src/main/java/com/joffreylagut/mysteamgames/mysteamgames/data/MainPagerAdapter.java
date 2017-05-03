package com.joffreylagut.mysteamgames.mysteamgames.data;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;

/**
 * MainPagerAdapter.java
 * Purpose: Adapter used by the ViewPager in MainActivity.
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-05-03
 */

public class MainPagerAdapter extends FragmentPagerAdapter {

    private final HashMap<Integer, Fragment> fragments;

    public MainPagerAdapter(FragmentManager fm, HashMap<Integer, Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }
}
