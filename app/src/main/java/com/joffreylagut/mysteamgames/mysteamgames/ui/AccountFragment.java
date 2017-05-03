package com.joffreylagut.mysteamgames.mysteamgames.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joffreylagut.mysteamgames.mysteamgames.R;

/**
 * AccountFragment.java
 * Purpose: Inflate and manage fragment_account layout.
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-05-03
 */

public class AccountFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_account, container, false);
    }
}
