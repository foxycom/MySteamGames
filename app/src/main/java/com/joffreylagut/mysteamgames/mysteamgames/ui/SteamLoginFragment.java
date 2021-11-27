package com.joffreylagut.mysteamgames.mysteamgames.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.joffreylagut.mysteamgames.mysteamgames.R;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SharedPreferencesHelper;

import static android.content.ContentValues.TAG;

/**
 * SteamLoginFragment.java
 * Purpose: Create a WebView to allow the user to login on Steam.
 *
 * @author Joffrey LAGUT
 * @version 1.1 2017-05-24
 */

public class SteamLoginFragment extends Fragment {

    SharedPreferences sharedPreferences;

    private String REALM_PARAM = "Steam Login";
    private String mUrl;
    private OnLoginFinishedListener mCallBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We load the SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //View v = inflater.inflate(R.layout.fragment_login, container, false);
        REALM_PARAM = getResources().getString(R.string.app_name);

        mUrl = "https://steamcommunity.com/openid/login?" +
                "openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select&" +
                "openid.identity=http://specs.openid.net/auth/2.0/identifier_select&" +
                "openid.mode=checkid_setup&" +
                "openid.ns=http://specs.openid.net/auth/2.0&" +
                "openid.realm=https://" + REALM_PARAM + "&" +
                "openid.return_to=https://" + REALM_PARAM + "/signin/";

        final WebView webView = new WebView(getActivity());
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        // TODO Use the new method instead of the deprecated one
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                String mainString = url;

                String substr = "openid%2Fid%";
                String after = mainString.substring(mainString.indexOf(substr) + substr.length());

                Long steamId = Long.valueOf(after.substring(2, 19));

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(SharedPreferencesHelper.STEAM_ID, steamId);
                editor.apply();
                Log.d(TAG, "Steam ID: " + steamId);

                mCallBack.onLoginFinished();

                return false;

            }
        });
        webView.loadUrl(mUrl);

        return webView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginFinishedListener) {
            mCallBack = (OnLoginFinishedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLoginFinishedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBack = null;
    }

    interface OnLoginFinishedListener {
        void onLoginFinished();
    }
}
