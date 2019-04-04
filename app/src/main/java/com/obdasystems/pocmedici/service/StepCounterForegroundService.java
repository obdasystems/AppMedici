package com.obdasystems.pocmedici.service;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.activity.MainActivity;
import com.obdasystems.pocmedici.asyncresponse.PageQuestionsAsyncResponse;
import com.obdasystems.pocmedici.persistence.repository.StepCounterRepository;
import com.obdasystems.pocmedici.stepdetector.CustomStepDetector;
import com.obdasystems.pocmedici.stepdetector.CustomStepListener;

import java.util.Calendar;

public class StepCounterForegroundService extends Service implements SensorEventListener, CustomStepListener {


    private final String CHANNEL_ID = "AppMedici_StepCounter";
    private final String CHANNEL_NAME = "AppMedici Step Counter Service";
    private final int NOTIFICATION_ID = 543;

    public static boolean isServiceRunning = false;

    private boolean hwStepDetectorEnabled;

    private SensorManager mSensorManager;
    private Sensor mStepDetectorSensor;

    private Sensor mAccelerometerSensor;
    private CustomStepDetector mCustomDetector;

    private StepCounterRepository repository;

    public StepCounterForegroundService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startServiceWithNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction().equals(MainActivity.ACTION_START_SERVICE)) {
            startServiceWithNotification();
        }
        startServiceWithNotification();
        //else stopMyService();
        return START_STICKY;
    }

    // In case the service is deleted or crashes some how
    @Override
    public void onDestroy() {
        isServiceRunning = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return null;
    }


    void startServiceWithNotification() {
        if (isServiceRunning) return;
        isServiceRunning = true;


        Log.i("appMedici", "["+this.getClass().getSimpleName()+"]Starting foreground service");

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setAction(MainActivity.ACTION_MAIN);  // A string containing the action name
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_pet);

        String channelId = "";
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            channelId = createNotificationChannel(CHANNEL_ID, CHANNEL_NAME);
        }

        Notification notification = new NotificationCompat.Builder(this,channelId)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setTicker(getResources().getString(R.string.app_name))
                .setContentText("CONTEXT TEXT")
                .setSmallIcon(R.drawable.ic_pet)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(contentPendingIntent)
                .setOngoing(true)
//                .setDeleteIntent(contentPendingIntent)  // if needed
                .build();
        notification.flags = notification.flags | Notification.FLAG_NO_CLEAR;     // NO_CLEAR makes the notification stay when the user performs a "delete all" command
        startForeground(NOTIFICATION_ID, notification);

        Log.i("appMedici", "["+this.getClass().getSimpleName()+"]Foreground service started");
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel nc = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
        nc.setLightColor(Color.BLUE);
        //nc.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
        nc.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(nc);
        return channelId;
    }


    void stopMyService() {
        stopForeground(true);
        stopSelf();
        isServiceRunning = false;
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        switch (event.sensor.getType()) {
            case Sensor.TYPE_STEP_DETECTOR:
                float detStep = event.values[0];
                if(detStep==1) {
                    Log.i("appMedici", "["+this.getClass().getSimpleName()+"]Step  detected HW");
                    StepCounterForegroundService.QueryAsyncTask task = new StepCounterForegroundService.QueryAsyncTask(year, month, day, (int)detStep, repository);
                    task.execute();
                }
                break;
            case  Sensor.TYPE_ACCELEROMETER:
                mCustomDetector.updateAccel(event.timestamp, event.values[0], event.values[1], event.values[2] );
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void step(long timeNs) {
        Log.i("appMedici", "["+this.getClass().getSimpleName()+"]Step  detected SW");
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) +1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        StepCounterForegroundService.QueryAsyncTask task = new StepCounterForegroundService.QueryAsyncTask(year, month, day, 1, repository);
        task.execute();
    }

    private static class QueryAsyncTask extends AsyncTask<Void, Void, Void> {
        private Context ctx;
        private int year, month , day , numSteps;

        private StepCounterRepository innerRepository;


        private Application app;
        private PageQuestionsAsyncResponse delegate;

        QueryAsyncTask(int year, int month, int day, int numSteps, StepCounterRepository rep) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.numSteps = numSteps;
            this.innerRepository = rep;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            innerRepository.addStep(numSteps, year, month, day);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
