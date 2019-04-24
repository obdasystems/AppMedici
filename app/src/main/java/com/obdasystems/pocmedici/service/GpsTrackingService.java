package com.obdasystems.pocmedici.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.activity.MainActivity;
import com.obdasystems.pocmedici.network.ApiClient;
import com.obdasystems.pocmedici.network.ItcoService;
import com.obdasystems.pocmedici.network.RestPosition;
import com.obdasystems.pocmedici.network.interceptors.AuthenticationInterceptor;
import com.obdasystems.pocmedici.persistence.repository.PositionRepository;
import com.obdasystems.pocmedici.utils.AppPreferences;

import org.json.JSONObject;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTWriter;

import java.util.GregorianCalendar;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GpsTrackingService extends Service {
    private static final String TAG = GpsTrackingService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 195;
    private final String CHANNEL_ID = "AppMedici_PosTracking";
    private final String CHANNEL_NAME = "AppMedici Tracker Service";

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Unregister the BroadcastReceiver when the notification is tapped//
            unregisterReceiver(stopReceiver);
            //Stop the Service//
            stopSelf();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildNotification();
        requestLocationUpdates();
    }

    /**
     * Create a persistent notification.
     */
    private void buildNotification() {
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
//        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
//                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setAction(MainActivity.ACTION_MAIN);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        // Create the persistent notification//
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.icons8_caduceus_48);
        String channelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel(CHANNEL_ID, CHANNEL_NAME);
        }

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.tracking_enabled_notif))
                // TODO: notification icon disabled
                //.setSmallIcon(R.drawable.icons8_caduceus_48)
                //.setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                //.setContentIntent(broadcastIntent)
                .setContentIntent(contentPendingIntent)
                .setOngoing(true)
//                .setDeleteIntent(contentPendingIntent)  // if needed
                .build();
        notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
        startForeground(NOTIFICATION_ID, notification);

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel nc = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
        nc.setLightColor(Color.BLUE);
        //nc.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
        nc.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(nc);
        return channelId;
    }

    /**
     * Initiate the request to track the device's location.
     */
    private void requestLocationUpdates() {
        String email = getString(R.string.test_email);

        Log.i(TAG, "Requesting location updates...");
        LocationRequest request = new LocationRequest();

        // Specify how often your app should request the device’s location//
        // FIXME: setup configurable tracking interval
        request.setInterval(60000);

        // Get the most accurate location data available
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        final String path = getString(R.string.firebase_path);
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        // If the app currently has access to the location permission...
        if (permission == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Access to fine location granted");
            // ...then request location updates
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Log.i(TAG, "Getting location result");
                    Location location = locationResult.getLastLocation();

                    if (location != null) {
                        // Save the location data to the database
                        long timestamp = System.currentTimeMillis();
                        Point geoPoint = getGeometryPoint(location);
                        sendPositionToServer(geoPoint, timestamp);
                    } else {
                        Log.i(TAG, "Got null location data");
                    }
                }
            }, null);
        } else {
            Log.i(TAG, "Access to fine location failed");
        }
    }

    /**
     * Transforms a {@link Location} into a {@link Point}.
     *
     * @param location the location
     * @return the point corresponding to the specified location
     */
    private Point getGeometryPoint(Location location) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        Coordinate coordinate = new Coordinate(latitude, longitude);
        GeometryFactory factory = new GeometryFactory();
        return factory.createPoint(coordinate);
    }

    private void sendPositionToServer(Point point, long timestamp) {
        if (!AppPreferences.with(this).contains("authorization_token")) {
            Log.w(TAG, "Location not sent: unauthenticated");
            return;
        }

        ItcoService apiService = (ItcoService) ApiClient
                .forService(ItcoService.class)
                .logging(HttpLoggingInterceptor.Level.BODY)
                .baseURL(ApiClient.BASE_URL)
                .addInterceptor(new AuthenticationInterceptor(this))
                .build();

        WKTWriter wktw = new WKTWriter();
        Log.i(TAG, "Sending position " + wktw.write(point));
        RestPosition rp = new RestPosition(wktw.write(point), timestamp);

        apiService.sendPosition(rp).enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Position sent to server." + response.body().toString());
                } else {
                    Log.e(TAG, "Unable to send position (" + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Log.e(TAG, "Unable to send position : " + t.getMessage());
            }
        });
    }

    /**
     * Async task used to insert location in local database.
     */
    private static class QueryAsyncTask extends AsyncTask<Void, Void, Void> {
        private double innLat, innLong;
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
            innerRepository.insertPosition(gc, innLat, innLong);
            Log.i(TAG, "Position inserted");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
