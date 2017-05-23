package com.joffreylagut.mysteamgames.mysteamgames.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import com.joffreylagut.mysteamgames.mysteamgames.R;
import com.joffreylagut.mysteamgames.mysteamgames.utilities.SharedPreferencesHelper;

/**
 * SettingsFragment.java
 * Purpose: Display the settings.
 *
 * @author Joffrey LAGUT
 * @version 1.0 2017-05-23
 */

public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);

        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();

        // Go through all of the preferences, and set up their preference summary.
        for (int i = 0; i < count; i++) {
            Preference preference = prefScreen.getPreference(i);
            updateSummary(preference);
        }

    }

    /**
     * Update the summary of a preference to put value + \n + summary
     *
     * @param preference that we want to update.
     */
    private void updateSummary(Preference preference) {

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        String extraValue = sharedPreferences.getString(SharedPreferencesHelper.CURRENCY, "$") + "/h";
        if (null != preference) {
            String summary = (String) preference.getSummary();
            String summarySplit[] = summary.split("\n");
            String value = sharedPreferences.getString(preference.getKey(), "");
            if (preference.getKey().equals(SharedPreferencesHelper.PROFITABLE_LIMIT)) {
                value += extraValue;
            }
            if (summarySplit.length == 1) {
                preference.setSummary(value + "\n" + summarySplit[0]);
            } else {
                preference.setSummary(value + "\n" + summarySplit[1]);
            }
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        updateSummary(preference);
    }

    /**
     * Called when a Preference has been changed by the user. This is
     * called before the state of the Preference is about to be updated and
     * before the state is persisted.
     *
     * @param preference The changed Preference.
     * @param newValue   The new value of the Preference.
     * @return True to update the state of the Preference with the new value.
     */
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

}