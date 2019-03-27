package com.obdasystems.pocmedici.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.obdasystems.pocmedici.activity.MainActivity;

public class DeviceBootReceiver extends BroadcastReceiver {

    public final static int GPS_TRACKING_PENDING_INTENT_ALRM_ID = 10000;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Toast.makeText(context, "BOOT EVENT CATCHED!!", Toast.LENGTH_SHORT).show();

            // Register your reporting alarms here.
            Log.i("appMedici", "["+this.getClass().getSimpleName()+"] registering alarm to start gps tracking service");
            Intent gpsIntent = new Intent(context, GpsTrackingService.class);
            PendingIntent gpsPendingIntent = PendingIntent.getService(context, GPS_TRACKING_PENDING_INTENT_ALRM_ID, gpsIntent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            long triggerAtMillis = 100;
            long intervalMillis = (5 * 60 * 1000);
            intervalMillis = 10000;
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtMillis , intervalMillis, gpsPendingIntent);




            StepCounterForegroundService forService = new StepCounterForegroundService(context);
            Intent mServiceIntent = new Intent(context, forService.getClass());
            mServiceIntent.setAction(MainActivity.ACTION_START_SERVICE);
            if (!isMyServiceRunning(forService.getClass(), context)) {
                context.startService(mServiceIntent);
                Log.i("appMedici", "["+this.getClass()+"]Step counter service launched");
            }
            else {
                Log.i("appMedici", "["+this.getClass()+"]Found step counter service already running");
            }
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
