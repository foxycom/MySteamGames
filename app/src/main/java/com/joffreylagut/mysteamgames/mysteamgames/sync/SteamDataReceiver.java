package com.joffreylagut.mysteamgames.mysteamgames.sync;

/**
 * SteamDataReceiver.java
 * Purpose: Receive a response and call back the class that implement it.
 *
 * @author Joffrey LAGUT
 * @version 1.1 2017-05-24
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SteamDataReceiver extends BroadcastReceiver {

    public static final String PROCESS_RESPONSE =
            "com.joffreylagut.mysteamgames.mysteamgames.intent.action.PROCESS_RESPONSE";

    public interface OnReceivedFinishedListener {
        void onReceiveFinished();
    }

    private OnReceivedFinishedListener mCallback;

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            mCallback = (OnReceivedFinishedListener) context;
            mCallback.onReceiveFinished();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnGameSelectedListener");
        }
    }
}