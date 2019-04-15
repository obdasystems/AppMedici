package com.obdasystems.pocmedici.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.jaredrummler.android.device.DeviceName;
import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.network.MediciApi;
import com.obdasystems.pocmedici.network.MediciApiClient;
import com.obdasystems.pocmedici.network.NetworkUtils;
import com.obdasystems.pocmedici.network.request.UserDeviceRegistrationRequest;
import com.obdasystems.pocmedici.service.DownloadAssignedFormsService;
import com.obdasystems.pocmedici.service.GpsTrackingService;
import com.obdasystems.pocmedici.service.SendFinalizedStepCountersService;
import com.obdasystems.pocmedici.service.StepCounterForegroundService;
import com.obdasystems.pocmedici.service.StepCounterService;
import com.obdasystems.pocmedici.utils.SaveSharedPreference;
import com.obdasystems.pocmedici.utils.TimeUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public final static String ACTION_MAIN = "action MAIN";
    public final static String ACTION_START_SERVICE = "action start service";

    private final int MAIN_LOGIN_CODE = 10000;

    private static final int LOCATION_PERMISSIONS_REQUEST = 100;

    private Context ctx;

    private int authCounter = 0;

    StepCounterService mSensorService;

    private String authorizationToken;

    /*private GpsTrackingService gpsService;
    private Intent gpsServiceIntent;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx = this;

        ImageView formImageView = findViewById(R.id.image_ctcae_form);
        Picasso.with(ctx).load(R.drawable.pulsante_questionnaire_rect_slim3).resize(1000, 250).into(formImageView);

        ImageView sensorImageView = findViewById(R.id.image_sensors);
        Picasso.with(ctx).load(R.drawable.pulsante_sensori_rect_slim).resize(1000, 250).into(sensorImageView);

        ImageView messagesImageView = findViewById(R.id.image_messages);
        Picasso.with(ctx).load(R.drawable.pulsante_messaggi_rect_slim).resize(1000, 250).into(messagesImageView);

        ImageView calendarImageView = findViewById(R.id.image_calendar);
        Picasso.with(ctx).load(R.drawable.pulsante_calendario_rect_slim).resize(1000, 250).into(calendarImageView);

        ImageView prescrView = findViewById(R.id.image_prescriptions);
        Picasso.with(ctx).load(R.drawable.pulsante_prescrizioni_rect_slim).resize(1000, 250).into(prescrView);

        ImageView drugView = findViewById(R.id.image_drugs);
        Picasso.with(ctx).load(R.drawable.pulsante_info_farmaci_rect_slim).resize(1000, 250).into(prescrView);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        Intent mServiceIntent = new Intent(this, StepCounterForegroundService.class);
        mServiceIntent.setAction(MainActivity.ACTION_START_SERVICE);
        if (!isMyServiceRunning(StepCounterForegroundService.class)) {
            startService(mServiceIntent);
            Log.i("appMedici", "["+this.getClass().getSimpleName()+"]Step counter service started");
        }
        else {
            Log.i("appMedici", "["+this.getClass().getSimpleName()+"]Found step counter service already running");
        }


        //LOCATION TRACKING
        //Check whether GPS tracking is enabled//
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.i("appMedici", "["+this.getClass().getSimpleName()+"] GPS provider is off!!");
            //finish();
        }

        //Check whether this app has access to the location permission
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        //If the location permission has been granted, then start the TrackerService
        if (permission == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        } else {

        //If the app doesn’t currently have access to the user’s location, then request access//
        ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSIONS_REQUEST);
        }

        checkAuthorizationToken();


        Calendar cal = Calendar.getInstance();
        String todayRepr = TimeUtils.getSimpleDateStringRepresentation(cal);
        String lastDateStepCountersSent = SaveSharedPreference.getLastTimeStepcountersSent(this);
        if(lastDateStepCountersSent!=null) {
            Log.i("appMedici", "[" + this.getClass().getSimpleName() + "] lastDateStepCountersSent=" + lastDateStepCountersSent);
        }
        else {
            Log.i("appMedici", "[" + this.getClass().getSimpleName() + "] lastDateStepCountersSent= null" );
        }

        if(lastDateStepCountersSent== null || !todayRepr.equals(lastDateStepCountersSent)) {
            Log.i("appMedici", "[" + this.getClass().getSimpleName() + "] creating intent SendFinalizedStepCountersService");
            Intent sendStepCountersIntent = new Intent(this, SendFinalizedStepCountersService.class);
            startService(sendStepCountersIntent);
            SaveSharedPreference.setLastTimeStepcountersSent(this,todayRepr);
        }

        String lastDateFormsRequested = SaveSharedPreference.getLastTimeQuestionnairesRequested(this);
        if(lastDateStepCountersSent!=null) {
            Log.i("appMedici", "[" + this.getClass().getSimpleName() + "] lastDateFormsRequested=" + lastDateFormsRequested);
        }
        else {
            Log.i("appMedici", "[" + this.getClass().getSimpleName() + "] lastDateFormsRequested= null" );
        }
        if(lastDateFormsRequested== null || !todayRepr.equals(lastDateFormsRequested)) {
            Log.i("appMedici", "[" + this.getClass().getSimpleName() + "] creating intent DownloadAssignedFormsService");
            Intent downloadFormsIntent = new Intent(this, DownloadAssignedFormsService.class);
            startService(downloadFormsIntent);
            SaveSharedPreference.setLastTimeQuestionnairesRequested(this,todayRepr);
        }

        authOnFirebase();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //...then start the GPS tracking service//
            startTrackerService();
        } else {
            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }
    }

    private void startTrackerService() {
        startService(new Intent(this, GpsTrackingService.class));
        Toast.makeText(this, "GPS tracking enabled", Toast.LENGTH_SHORT).show();
        //finish();
    }

    /*****************************
     * TOOLBAR METHODS
     *****************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.form_page_action_profile) {
            //TODO launch profile intent
            return true;
        }
        if (id == R.id.form_page_action_settings) {
            //TODO launch SETTINGS intent
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==MAIN_LOGIN_CODE){
            Toast.makeText(this,"Benvenuto!!! Bravo!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void cardViewSelected(View view) {
        Class activityClass;

        switch (view.getId()) {
            case R.id.card_ctcae_form:
                activityClass = NewFormListActivity.class;
                break;
            case R.id.card_sensors:
                activityClass = PieChartStepCounterActivity.class;
                break;
            case R.id.card_messages:
                activityClass = MessageListActivity.class;
                break;
            case R.id.card_calendar:
                activityClass = CalendarMaterialActivity.class;
                break;
            case R.id.card_prescriptions:
                activityClass = PrescriptionListActivity.class;
                break;
            case R.id.card_drugs:
                activityClass = DrugListActivity.class;
                break;
            default:
                activityClass = NewFormListActivity.class;
                break;
        }

        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    /*****************************
     *
     *      * SERVICES MANAGEMENT METHODS
     *****************************/
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }


    @Override
    protected void onDestroy() {
        //stopService(gpsServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

    }

    /*****************************
     *      * AUTHENTICATION
     *****************************/
    private void authOnFirebase(){

        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("appMedici", "getInstanceId failed", task.getException());
                        return;
                    }
                    // Get new Instance ID token
                    String token = task.getResult().getToken();
                    sendFirebaseTokenToServer(token);
                });



    }

    private void sendFirebaseTokenToServer(String token) {
        String usr = "james";
        String pwd = "bush";

        if(authCounter<30) {
            authCounter++;
            String deviceDescription = DeviceName.getDeviceName();
            String authToken = SaveSharedPreference.getAuthorizationToken(this);
            MediciApi apiInterface = MediciApiClient.createService(MediciApi.class, authToken);
            UserDeviceRegistrationRequest registrationRequest = new UserDeviceRegistrationRequest();
            registrationRequest.setDeviceDescription(deviceDescription);
            registrationRequest.setRegistrationToken(token);
            apiInterface.registerInstanceId(registrationRequest)
                    .enqueue(new Callback<JSONObject>() {
                        @Override
                        public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                            if (response.isSuccessful()) {
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Firebase token sent!!");
                                authCounter = 0;
                            } else {
                                switch (response.code()) {
                                    case 401:
                                        NetworkUtils.requestNewAuthorizationToken(pwd, usr, ctx);
                                        Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to send firebase token to server (401)");
                                        if (!SaveSharedPreference.getAuthorizationIssue(ctx)) {
                                            sendFirebaseTokenToServer(token);
                                        } else {
                                            String issueDescription = SaveSharedPreference.getAuthorizationIssueDescription(ctx);
                                            Toast.makeText(getApplicationContext(), "Unable to send firebase token to server (401) [" + issueDescription + "]", Toast.LENGTH_LONG).show();
                                            Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to send firebase token to server (401) [" + issueDescription + "]");
                                        }
                                        break;
                                    case 404:
                                        Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to send firebase token to server (404)");
                                        Toast.makeText(getApplicationContext(), "Unable to send firebase token to server (404)", Toast.LENGTH_LONG).show();

                                        break;
                                    case 500:
                                        Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to send firebase token to server (500)");
                                        Toast.makeText(getApplicationContext(), "Unable to send firebase token to server (500)", Toast.LENGTH_LONG).show();

                                        break;
                                    default:
                                        Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to send firebase token to server (UNKNOWN)");
                                        Toast.makeText(getApplicationContext(), "Unable to send firebase token to server (UNKNOWN)", Toast.LENGTH_LONG).show();

                                        break;
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JSONObject> call, Throwable t) {

                        }
                    });
        }
    }


    private void checkAuthorizationToken() {
        String usr = "james";
        String pwd = "bush";
        String authorizationToken = SaveSharedPreference.getAuthorizationToken(this);
        if(authorizationToken == null) {
            NetworkUtils.requestNewAuthorizationToken(pwd, usr, this);
        }
    }


}
