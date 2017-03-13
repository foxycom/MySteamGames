package com.joffreylagut.mysteamgames.mysteamgames.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by Joffrey on 13/03/2017.
 */

public class RetrieveDataFromSteamJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        IntentFilter filter = new IntentFilter(SteamDataReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        SteamDataReceiver receiver = new SteamDataReceiver();
        registerReceiver(receiver, filter);

        // We start the service that retrieve steam information
        Intent dataRetrieverService = new Intent(this, RetrieveDataFromSteamIntentService.class);
        startService(dataRetrieverService);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

    /**
     * This class is created to receive the response from the service responsible of Steam data
     * loading.
     */
    private class SteamDataReceiver extends BroadcastReceiver {

        public static final String PROCESS_RESPONSE =
                "com.joffreylagut.mysteamgames.mysteamgames.intent.action.PROCESS_RESPONSE";

        @Override
        public void onReceive(Context context, Intent intent) {
            Boolean newGameDetected = intent.getBooleanExtra("newGameDetected", false);
            NotificationUtils.clearAllNotifications(RetrieveDataFromSteamJobService.this);
            if (newGameDetected) {
                NotificationUtils.newGameDetected(RetrieveDataFromSteamJobService.this);
            }
        }
    }
}
