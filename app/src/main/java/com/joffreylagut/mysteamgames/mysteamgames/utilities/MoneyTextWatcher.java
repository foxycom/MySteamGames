package com.joffreylagut.mysteamgames.mysteamgames.utilities;

import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;

/**
 * Created by Joffrey on 21/02/2017.
 */

public class MoneyTextWatcher implements TextWatcher {
    private final WeakReference<EditText> editTextWeakReference;

    public MoneyTextWatcher(EditText editText) {
        editTextWeakReference = new WeakReference<EditText>(editText);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        EditText editText = editTextWeakReference.get();
        if (editText == null) return;
        String s = editable.toString();
        editText.removeTextChangedListener(this);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(editText.getContext());
        String cleanString;
        switch (sharedPreferences.getString("lp_currency", "$")) {
            case "€":
                cleanString = s.toString().replaceAll("[€]", "");
                break;
            case "£":
                cleanString = s.toString().replaceAll("[£]", "");
                break;
            default:
                cleanString = s.toString().replaceAll("[$]", "");
                break;
        }
        editText.setText(sharedPreferences.getString("lp_currency", "$") + cleanString);
        editText.setSelection(cleanString.length() + 1);
        editText.addTextChangedListener(this);
    }
}