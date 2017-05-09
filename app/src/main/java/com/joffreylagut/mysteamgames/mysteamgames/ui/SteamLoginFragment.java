package com.joffreylagut.mysteamgames.mysteamgames.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
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
 * Created by Joffrey on 02/03/2017.
 */

public class SteamLoginFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    SharedPreferences sharedPreferences;
    View v;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String REALM_PARAM = "Steam Test Login";
    private String mUrl;


    private OnFragmentInteractionListener mListener;

    public SteamLoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SteamLoginFragment newInstance(String param1, String param2) {
        SteamLoginFragment fragment = new SteamLoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

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


        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                String mainString = url;

                String substr = "openid%2Fid%";
                String before = mainString.substring(0, mainString.indexOf(substr));
                String after = mainString.substring(mainString.indexOf(substr) + substr.length());

                Long steamId = Long.valueOf(after.substring(2, 19));

                //Prefs.putString("steamid", steamId);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(SharedPreferencesHelper.STEAM_ID, steamId);
                editor.apply();
                Log.d(TAG, "Steam ID: " + steamId);

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                return false;

            }
        });
        webView.loadUrl(mUrl);


        return webView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
