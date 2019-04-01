package com.obdasystems.pocmedici.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GpsTrackingStarterBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("appMedici", "["+this.getClass().getSimpleName()+"] Signal received!! ");
        context.startService(new Intent(context, GpsTrackingService.class));
    }
}
