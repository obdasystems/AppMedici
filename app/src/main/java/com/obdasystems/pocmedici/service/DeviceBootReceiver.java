package com.obdasystems.pocmedici.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.obdasystems.pocmedici.service.broadcast.GpsTrackingRestarterBroadcastReceiver;

public class DeviceBootReceiver extends BroadcastReceiver {

    public final static int GPS_TRACKING_PENDING_INTENT_ALRM_ID = 10000;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // Register your reporting alarms here.
            /*Log.i("appMedici", "["+this.getClass().getSimpleName()+"] registering alarm to start gps tracking service");
            Intent gpsIntent = new Intent(context, GpsTrackingRestarterBroadcastReceiver.class);
            PendingIntent gpsPendingIntent = PendingIntent.getService(context, GPS_TRACKING_PENDING_INTENT_ALRM_ID, gpsIntent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            long triggerAtMillis = 100;
            long intervalMillis = (5 * 60 * 1000);
            intervalMillis = 10000;
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtMillis , intervalMillis, gpsPendingIntent);*/


            //start step counter
            /*StepCounterService mSensorService = new StepCounterService();
            Intent mServiceIntent = new Intent(context, mSensorService.getClass());
            if (!isMyServiceRunning(mSensorService.getClass(), context)) {
                context.startService(mServiceIntent);
            }*/
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }
}
