package com.obdasystems.pocmedici.service;

import android.Manifest;
import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.asyncresponse.PageQuestionsAsyncResponse;
import com.obdasystems.pocmedici.persistence.repository.PositionRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class GpsTrackingService extends Service {

    private static final String TAG = GpsTrackingService.class.getSimpleName();
    static final int NOTIFICATION_ID = 195;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildNotification();
        loginToFirebase();
    }

    //Create the persistent notification
    private void buildNotification() {
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);
        // Create the persistent notification//
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.tracking_enabled_notif))
                //Make this notification ongoing so it can’t be dismissed by the user//
                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.icons8_caduceus_48);
        startForeground(NOTIFICATION_ID, builder.build());
    }

    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Unregister the BroadcastReceiver when the notification is tapped//
            unregisterReceiver(stopReceiver);
            //Stop the Service//
            stopSelf();
        }
    };

    private void loginToFirebase() {
        //Authenticate with Firebase, using the email and password we created earlier//
        String email = getString(R.string.test_email);
        String password = getString(R.string.test_password);

        //Call OnCompleteListener if the user is signed in successfully//
        Log.i("appMedici", "["+this.getClass().getSimpleName()+"] Signing in to firebase usr="+email + " pwd="+password+"  ...");
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                //If the user has been authenticated...//
                if (task.isSuccessful()) {
                    //...then call requestLocationUpdates
                    Log.i("appMedici", "["+this.getClass().getSimpleName()+"] Firebase authentication granted");
                    requestLocationUpdates();
                } else {
                    //If sign in fails, then log the error
                    Log.i("appMedici", "["+this.getClass().getSimpleName()+"] Firebase authentication failed");
                }
            }
        });
    }

    //Initiate the request to track the device's location//
    private void requestLocationUpdates() {
        String email = getString(R.string.test_email);

        Log.i("appMedici", "["+this.getClass().getSimpleName()+"] Requesting location updates...");
        LocationRequest request = new LocationRequest();

        //Specify how often your app should request the device’s location//
        request.setInterval(10000 * 60);

        //Get the most accurate location data available//
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        final String path = getString(R.string.firebase_path);
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        //If the app currently has access to the location permission...//
        if (permission == PackageManager.PERMISSION_GRANTED) {
            Log.i("appMedici", "["+this.getClass().getSimpleName()+"] Access to fine location granted");
            //...then request location updates//
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Log.i("appMedici", "["+this.getClass().getSimpleName()+"] Getting location result");
                    //Get a reference to the database, so your app can perform read and write operations//
                    //DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                    /*
                    LISTENER CAPTURING EVENT ON DATABASE
                     */
                    /*ValueEventListener insertListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get Post object and use the values to update the UI
                            Object inserted = dataSnapshot.getValue();
                            Log.i("appMedici", "["+this.getClass().getSimpleName()+"] Listener got datasnapshot= "+inserted.toString());
                            // ...
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                            // ...
                        }
                    };
                    ref.addValueEventListener(insertListener);*/

                    Log.i("appMedici", "["+this.getClass().getSimpleName()+"] Got reference to database " + ref.toString());
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        //Save the location data to the database//
                        long timestamp = System.currentTimeMillis();
                        int day;
                        int month;
                        int year;

                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
                            LocalDate now = LocalDate.now();
                            day = now.getDayOfMonth();
                            month = now.getMonthValue();
                            year = now.getYear();
                        }
                        else {
                            Calendar gc = new GregorianCalendar();
                            gc.setTimeInMillis(timestamp);
                            day = gc.get(Calendar.DAY_OF_MONTH);
                            month = gc.get(Calendar.MONTH);
                            year = gc.get(Calendar.YEAR);
                        }

                        ref.child("positions")
                                .child("james_bush")
                                .child(""+year)
                                .child(""+month)
                                .child(""+day)
                                .child(""+timestamp)
                                .setValue(location, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                        Log.i("appMedici", "["+this.getClass().getSimpleName()+"] Value was set??. Error = "+databaseError);
                                    }
                                });

                        /*Log.i("appMedici", "["+this.getClass().getSimpleName()+"] Saving the location" +location + " data to the remote database....");
                        ref.child("positions").child("james").setValue(location, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                                Log.i("appMedici", "["+this.getClass().getSimpleName()+"] Value was set??. Error = "+databaseError);
                            }
                        });*/
                    }
                    else {
                        Log.i("appMedici", "["+this.getClass().getSimpleName()+"] Got null location data");
                    }
                }
            }, null);
        }
        else {
            Log.i("appMedici", "["+this.getClass().getSimpleName()+"] Access to fine location failed");
        }
    }


    //Insert location in local database
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
            innerRepository.insertPosition(gc, innLat, innLong);
            Log.i("appMedici", "[" + this.getClass().getSimpleName() + "] POSITION INSERTED");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
