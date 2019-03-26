package com.obdasystems.pocmedici.service.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.obdasystems.pocmedici.service.GpsTrackingService;


public class GpsTrackingRestarterBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("appMedici", "["+this.getClass().getSimpleName()+"] Receiving Message to start gps tracking service");
        context.startService(new Intent(context, GpsTrackingService.class));
    }
}
