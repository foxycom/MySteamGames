package com.joffreylagut.mysteamgames.mysteamgames.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joffreylagut.mysteamgames.mysteamgames.R;

/**
 * HomeFragment.java
 * Purpose: Inflate and manage fragment_home layout.
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-05-03
 */

public class HomeFragment extends android.support.v4.app.Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View viewRoot = inflater.inflate(R.layout.fragment_home, container, false);
        return viewRoot;
    }
}
