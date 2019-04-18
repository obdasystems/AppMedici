package com.obdasystems.pocmedici.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
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
import com.obdasystems.pocmedici.utils.SaveSharedPreference;
import com.obdasystems.pocmedici.utils.TimeUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppActivity {
    public static final String ACTION_MAIN = "action MAIN";
    public static final String ACTION_START_SERVICE = "action start service";

    // Core used to start login activity for results
    private static final int MAIN_LOGIN_CODE = 10000;

    private int authCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup toolbar
        loadImages();
        Toolbar toolbar = find(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        checkServices();
        checkAuthorizationToken();
        checkInfosToServer();
        authOnFirebase();
    }

    private void loadImages() {
        ImageView formImageView = find(R.id.image_form);
        Picasso.with(context())
                .load(R.drawable.button_forms)
                .resize(1000, 250)
                .into(formImageView);

        ImageView sensorImageView = find(R.id.image_sensors);
        Picasso.with(context())
                .load(R.drawable.button_sensors)
                .resize(1000, 250)
                .into(sensorImageView);

        ImageView messagesImageView = find(R.id.image_messages);
        Picasso.with(context())
                .load(R.drawable.button_messages)
                .resize(1000, 250)
                .into(messagesImageView);

        ImageView calendarImageView = find(R.id.image_calendar);
        Picasso.with(context())
                .load(R.drawable.button_calendar)
                .resize(1000, 250)
                .into(calendarImageView);

        ImageView prescrView = find(R.id.image_prescriptions);
        Picasso.with(context())
                .load(R.drawable.button_prescriptions)
                .resize(1000, 250)
                .into(prescrView);

        ImageView drugView = find(R.id.image_drugs);
        Picasso.with(context())
                .load(R.drawable.button_drug_details)
                .resize(1000, 250)
                .into(drugView);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQ_CODE_REQUEST_PERMISSIONS
                && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        } else {
            toast(R.string.permission_location_denied);
        }
    }

    /* ***************************
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
            // TODO: launch profile intent
            return true;
        }
        if (id == R.id.form_page_action_settings) {
            // TODO: launch SETTINGS intent
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if (requestCode == MAIN_LOGIN_CODE) {
        //
        //}
    }

    public void cardViewSelected(View view) {
        Class activityClass;

        switch (view.getId()) {
            case R.id.card_form:
                activityClass = NewFormListActivity.class;
                break;
            case R.id.card_sensors:
                activityClass = PieChartStepCounterActivity.class;
                break;
            case R.id.card_messages:
                activityClass = MessageListActivity.class;
                break;
            case R.id.card_calendar:
                activityClass = CalendarActivity.class;
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
        Intent back = new Intent(Intent.ACTION_MAIN);
        back.addCategory(Intent.CATEGORY_HOME);
        back.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(back);
    }

    /* ******************************************
     * SERVICES MANAGEMENT METHODS
     ********************************************/

    private void checkServices() {
        if (!isServiceRunning(StepCounterForegroundService.class)) {
            startStepCounterService();
        }

        // LOCATION TRACKING
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.i(tag(), "GPS provider is off");
            // TODO: CHIEDI DI ACCENDERE GPS;
        } else {
            if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (!isServiceRunning(GpsTrackingService.class)) {
                    startTrackerService();
                }
            } else {
                requestPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
    }

    private void startStepCounterService() {
        Intent mServiceIntent = new Intent(this, StepCounterForegroundService.class);
        mServiceIntent.setAction(MainActivity.ACTION_START_SERVICE);
        startService(mServiceIntent);
        Log.i(tag(), "Step counter service started");
    }

    private void startTrackerService() {
        startService(new Intent(this, GpsTrackingService.class));
        Log.i(tag(), "GPS tracking service started");
    }

    private void checkInfosToServer() {
        Calendar cal = Calendar.getInstance();
        String todayRepr = TimeUtils.getSimpleDateStringRepresentation(cal);
        String lastDateStepCountersSent =
                SaveSharedPreference.getLastTimeStepcountersSent(this);
        if (lastDateStepCountersSent != null) {
            Log.i(tag(), "lastDateStepCountersSent = " + lastDateStepCountersSent);
        } else {
            Log.i(tag(), "lastDateStepCountersSent = null");
        }

        if (!todayRepr.equals(lastDateStepCountersSent)) {
            Log.i(tag(), "creating intent SendFinalizedStepCountersService");
            Intent sendStepCountersIntent =
                    new Intent(this, SendFinalizedStepCountersService.class);
            startService(sendStepCountersIntent);
            SaveSharedPreference.setLastTimeStepcountersSent(this, todayRepr);
        }

        String lastDateFormsRequested =
                SaveSharedPreference.getLastTimeQuestionnairesRequested(this);
        if (lastDateStepCountersSent != null) {
            Log.i(tag(), "lastDateFormsRequested = " + lastDateFormsRequested);
        } else {
            Log.i(tag(), "lastDateFormsRequested = null");
        }

        if (!todayRepr.equals(lastDateFormsRequested)) {
            Log.i(tag(), "Creating intent DownloadAssignedFormsService");
            Intent downloadFormsIntent =
                    new Intent(this, DownloadAssignedFormsService.class);
            startService(downloadFormsIntent);
            SaveSharedPreference.setLastTimeQuestionnairesRequested(this, todayRepr);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    /* ******************************************
     * AUTHENTICATION
     ********************************************/

    private void authOnFirebase(){
        // Send instance id token to the server
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(tag(), "getInstanceId failed", task.getException());
                        return;
                    }
                    // Get new Instance ID token
                    String token = task.getResult().getToken();
                    sendFirebaseTokenToServer(token);
                });

        // Setup Firebase notification support
        FirebaseMessaging.getInstance()
                .subscribeToTopic("events")
                .addOnCompleteListener(task -> {
                    Log.i(tag(), "Successfully subscribed to topic: events");
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
                                Log.e(tag(), "Firebase token sent");
                                authCounter = 0;
                            } else {
                                switch (response.code()) {
                                    case 401:
                                        NetworkUtils.requestNewAuthorizationToken(pwd, usr, context());
                                        Log.e(tag(), "Unable to send firebase token to server (401)");
                                        if (!SaveSharedPreference.getAuthorizationIssue(context())) {
                                            sendFirebaseTokenToServer(token);
                                        } else {
                                            String issueDescription = SaveSharedPreference.getAuthorizationIssueDescription(context());
                                            toast("Unable to send firebase token to server (401) [" + issueDescription + "]", Toast.LENGTH_LONG);
                                            Log.e(tag(), "Unable to send firebase token to server (401) [" + issueDescription + "]");
                                        }
                                        break;
                                    case 404:
                                        Log.e(tag(), "Unable to send firebase token to server (404)");
                                        toast("Unable to send firebase token to server (404)", Toast.LENGTH_LONG);
                                        break;
                                    case 500:
                                        Log.e(tag(), "Unable to send firebase token to server (500)");
                                        toast("Unable to send firebase token to server (500)", Toast.LENGTH_LONG);
                                        break;
                                    default:
                                        Log.e(tag(), "Unable to send firebase token to server (UNKNOWN)");
                                        toast("Unable to send firebase token to server (UNKNOWN)", Toast.LENGTH_LONG);
                                        break;
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<JSONObject> call, Throwable t) {

                        }
                    });
        } else {
            authCounter = 0;
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
