package com.obdasystems.pocmedici.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GpsTrackingStarterBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = GpsTrackingStarterBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Broadcast received");
        context.startService(new Intent(context, GpsTrackingService.class));
    }

}
