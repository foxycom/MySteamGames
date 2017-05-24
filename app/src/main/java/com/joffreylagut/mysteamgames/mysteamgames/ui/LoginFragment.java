package com.joffreylagut.mysteamgames.mysteamgames.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joffreylagut.mysteamgames.mysteamgames.R;

/**
 * LoginFragment.java
 * Purpose: Inflate the fragment_login layout.
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-05-25
 */

public class LoginFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }
}
