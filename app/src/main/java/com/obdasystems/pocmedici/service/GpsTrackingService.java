package com.obdasystems.pocmedici.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.obdasystems.pocmedici.service.broadcast.GpsTrackingRestarterBroadcastReceiver;

import java.util.Timer;
import java.util.TimerTask;

public class GpsTrackingService extends Service {

    private FusedLocationProviderClient fusedLocationClient;

    // getting GPS status
    boolean isGPSEnabled;

    // getting network status
    boolean isNetworkEnabled;


    public int counter=0;
    public GpsTrackingService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // getting GPS status
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        /*Log.i("appMedici", "["+this.getClass().getSimpleName()+"] STARTING SERVICE");

        LocationRequest locationRequest = new LocationRequest();

        if(isGPSEnabled || isNetworkEnabled) {
            checkPermission(LocationManager.GPS_PROVIDER);
            fusedLocationClient.getLastLocation().addOnSuccessListener( new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                }
            });
        }*/
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
