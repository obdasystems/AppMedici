package com.obdasystems.pocmedici.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.adapter.MainAdapter;
import com.obdasystems.pocmedici.listener.OnMainRecyclerViewItemClickListener;
import com.obdasystems.pocmedici.message.helper.DividerItemDecoration;
import com.obdasystems.pocmedici.network.NetworkUtils;
import com.obdasystems.pocmedici.service.DownloadAssignedFormsService;
import com.obdasystems.pocmedici.service.GpsTrackingService;
import com.obdasystems.pocmedici.service.SendFinalizedStepCountersService;
import com.obdasystems.pocmedici.service.StepCounterForegroundService;
import com.obdasystems.pocmedici.utils.SaveSharedPreference;
import com.obdasystems.pocmedici.utils.TimeUtils;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

@Deprecated
public class MainRecyclerActivity extends AppActivity
        implements OnMainRecyclerViewItemClickListener {
    public static final String ACTION_MAIN = "action MAIN";
    public static final String ACTION_START_SERVICE = "action start service";
    private static final int LOCATION_PERMISSIONS_REQUEST = 100;
    private static final int MAIN_LOGIN_CODE = 10000;

    private static final String QUESTIONNAIRE_IMAGE_NAME = "pulsante_questionnaire_rect_slim3";
    private static final String SENSORS_IMAGE_NAME = "pulsante_sensori_rect_slim";
    private static final String MESSAGES_IMAGE_NAME = "pulsante_messaggi_rect_slim";
    private static final String CALENDAR_IMAGE_NAME = "pulsante_calendario_rect_slim";
    private static final String PRESCRIPTION_IMAGE_NAME = "pulsante_prescrizioni_rect_slim";

    // Instance variables
    private List<String> imageNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recycler);
        initializeImageNames();

        MainAdapter mAdapter = new MainAdapter(this);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setImages(imageNames);

        // Instance variables
        RecyclerView recyclerView = find(R.id.main_recycler_view);
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        Toolbar toolbar = find(R.id.main_recycler_toolbar);
        setSupportActionBar(toolbar);

        Intent mServiceIntent =
                new Intent(this, StepCounterForegroundService.class);
        mServiceIntent.setAction(MainActivity.ACTION_START_SERVICE);
        if (!isServiceRunning(StepCounterForegroundService.class)) {
            startService(mServiceIntent);
            Log.i(tag(), "Step counter service started");
        } else {
            Log.i(tag(), "Found step counter service already running");
        }

        // LOCATION TRACKING
        // Check whether GPS tracking is enabled
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.i(tag(), "GPS provider is off!!");
        }

        // Check whether this app has access to the location permission
        if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            // If the location permission has been granted, then start the TrackerService
            startTrackerService();
        } else {
            // If the app doesn’t currently have access to
            // the user’s location, then request access
            requestPermission(LOCATION_PERMISSIONS_REQUEST,
                    Manifest.permission.ACCESS_FINE_LOCATION);
        }

        checkAuthorizationToken();

        Calendar cal = Calendar.getInstance();
        String todayRepr = TimeUtils.getSimpleDateStringRepresentation(cal);
        String lastDateStepCountersSent =
                SaveSharedPreference.getLastTimeStepcountersSent(this);
        if (lastDateStepCountersSent != null) {
            Log.i(tag(), "lastDateStepCountersSent=" + lastDateStepCountersSent);
        } else {
            Log.i(tag(), "lastDateStepCountersSent= null");
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
            Log.i(tag(), "lastDateFormsRequested=" + lastDateFormsRequested);
        } else {
            Log.i(tag(), "lastDateFormsRequested= null");
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
    protected void onResume() {
        super.onResume();

        SharedPreferences sp = getSharedPreferences("app_medici_login", MODE_PRIVATE);
        boolean logged = sp.getBoolean("logged", false);
        if (!logged) {
            Intent logIntent = new Intent(this, LoginActivity.class);
            startActivityForResult(logIntent, MAIN_LOGIN_CODE);
        }
    }

    private void initializeImageNames() {
        imageNames = new LinkedList<>();
        imageNames.add(QUESTIONNAIRE_IMAGE_NAME);
        imageNames.add(SENSORS_IMAGE_NAME);
        imageNames.add(MESSAGES_IMAGE_NAME);
        imageNames.add(CALENDAR_IMAGE_NAME);
        imageNames.add(PRESCRIPTION_IMAGE_NAME);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSIONS_REQUEST
                && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // ...then start the GPS tracking service
            startTrackerService();
        } else {
            toast(R.string.permission_location_denied);
        }
    }

    private void startTrackerService() {
        startService(new Intent(this, GpsTrackingService.class));
    }

    @Override
    public void onItemClick(View view, int position) {
        Class activityClass;

        switch (position) {
            case 0:
                activityClass = FormListActivity.class;
                break;
            case 1:
                activityClass = PieChartStepCounterActivity.class;
                break;
            case 2:
                activityClass = MessageListActivity.class;
                break;
            case 3:
                activityClass = CalendarActivity.class;
                break;
            case 4:
                activityClass = MessageListActivity.class;
                break;
            default:
                activityClass = FormListActivity.class;
                break;
        }

        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
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
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    /* ***************************
     * SERVICES MANAGEMENT METHODS
     *****************************/

    @Override
    protected void onDestroy() {
        //stopService(gpsServiceIntent);
        Log.i("MAINACT", "onDestroy!");
        super.onDestroy();

    }

    /* ***************************
     * AUTHENTICATION
     *****************************/
    private void checkAuthorizationToken() {
        String usr = "james";
        String pwd = "bush";
        String authorizationToken = SaveSharedPreference.getAuthorizationToken(this);
        if (authorizationToken == null) {
            NetworkUtils.requestNewAuthorizationToken(pwd, usr, this);
        }
    }

}
