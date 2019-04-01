package com.obdasystems.pocmedici.service;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.obdasystems.pocmedici.asyncresponse.PageQuestionsAsyncResponse;
import com.obdasystems.pocmedici.persistence.repository.PositionRepository;

import java.util.GregorianCalendar;

public class GpsTrackingService extends Service {

    private FusedLocationProviderClient fusedLocationClient;

    // getting GPS status
    boolean isGPSEnabled;

    // getting network status
    boolean isNetworkEnabled;


    private PositionRepository repository;

    public int counter=0;
    public GpsTrackingService(Context applicationContext) {
        super();
        Log.i("appMedici", "["+this.getClass().getSimpleName()+"]NON-Empty constructor!!");

        repository = new PositionRepository(applicationContext);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // getting GPS status
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    }

    public GpsTrackingService() {
        super();
        Log.i("appMedici", "["+this.getClass().getSimpleName()+"]Empty constructor!!");

        repository = new PositionRepository(this);

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
        Log.i("appMedici", "["+this.getClass().getSimpleName()+"] STARTING GEOLOCATION SERVICE ");

        this.getApplicationContext();

        //LocationRequest locationRequest = new LocationRequest();

        if(isGPSEnabled || isNetworkEnabled) {
            checkPermission(LocationManager.GPS_PROVIDER,0,0);
            fusedLocationClient.getLastLocation().addOnSuccessListener( new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Log.i("appMedici", "["+this.getClass().getSimpleName()+"] Position acquired !!");
                    QueryAsyncTask task = new QueryAsyncTask(longitude, latitude, repository);
                }
            });
        }
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("appMedici", "["+this.getClass().getSimpleName()+"] destroying ");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static class QueryAsyncTask extends AsyncTask<Void, Void, Void> {
        private Context ctx;
        double innLat, innLong;
        private PositionRepository innerRepository;

        QueryAsyncTask(double longitude, double latitude, PositionRepository rep) {
            this.innLat = latitude;
            this.innLong = longitude;
            this.innerRepository = rep;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTimeInMillis(System.currentTimeMillis());
            innerRepository.insertPosition(gc,innLat,innLong);
            Log.i("appMedici", "["+this.getClass().getSimpleName()+"] POSITION INSERTED");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
