package com.joffreylagut.mysteamgames.mysteamgames.data;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

/**
 * MainPagerAdapter.java
 * Purpose: Adapter used by the ViewPager in MainActivity.
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-05-03
 */

public class MainPagerAdapter extends FragmentPagerAdapter {

    private final SparseArray<Fragment> fragments;

    public MainPagerAdapter(FragmentManager fm, SparseArray<Fragment> fragments) {
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
