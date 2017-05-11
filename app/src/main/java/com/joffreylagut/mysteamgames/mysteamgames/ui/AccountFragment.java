package com.joffreylagut.mysteamgames.mysteamgames.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.joffreylagut.mysteamgames.mysteamgames.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * AccountFragment.java
 * Purpose: Inflate and manage fragment_account layout.
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-05-03
 */

public class AccountFragment extends Fragment {

    @BindView(R.id.action_preference)
    Button mBtnPreferences;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View viewRoot = inflater.inflate(R.layout.fragment_account, container, false);
        ButterKnife.bind(this, viewRoot);

        return viewRoot;
    }

    @OnClick(R.id.action_preference)
    public void showPreferenceActivity() {
        Intent intent = new Intent(getContext(), SettingsActivity.class);
        startActivity(intent);
    }
}
