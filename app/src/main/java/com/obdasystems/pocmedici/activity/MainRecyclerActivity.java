package com.obdasystems.pocmedici.activity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

public class MainRecyclerActivity extends AppCompatActivity implements OnMainRecyclerViewItemClickListener {

    public final static String ACTION_MAIN = "action MAIN";
    public final static String ACTION_START_SERVICE = "action start service";

    private final int MAIN_LOGIN_CODE = 10000;

    private static final int LOCATION_PERMISSIONS_REQUEST = 100;

    private Context ctx;

    private RecyclerView recyclerView;
    private MainAdapter mAdapter;

    private final String QUESTIONNAIRE_IMAGE_NAME = "pulsante_questionnaire_rect_slim3";
    private final String SENSORS_IMAGE_NAME = "pulsante_sensori_rect_slim";
    private final String MESSAGES_IMAGE_NAME = "pulsante_messaggi_rect_slim";
    private final String CALENDAR_IMAGE_NAME = "pulsante_calendario_rect_slim";
    private final String PRESCRIPTION_IMAGE_NAME = "pulsante_prescrizioni_rect_slim";

    private List<String> imageNames;

    private void initializeImageNames() {
        imageNames = new LinkedList<>();
        imageNames.add(QUESTIONNAIRE_IMAGE_NAME);
        imageNames.add(SENSORS_IMAGE_NAME);
        imageNames.add(MESSAGES_IMAGE_NAME);
        imageNames.add(CALENDAR_IMAGE_NAME);
        imageNames.add(PRESCRIPTION_IMAGE_NAME);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recycler);
        ctx = this;
        initializeImageNames();

        mAdapter = new MainAdapter(this);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setImages(imageNames);

        recyclerView = (RecyclerView) findViewById(R.id.main_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        /*ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        recyclerView.setLayoutParams(params);*/

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_recycler_toolbar);
        setSupportActionBar(toolbar);

        Intent mServiceIntent = new Intent(this, StepCounterForegroundService.class);
        mServiceIntent.setAction(MainActivity.ACTION_START_SERVICE);
        if (!isMyServiceRunning(StepCounterForegroundService.class)) {
            startService(mServiceIntent);
            Log.i("appMedici", "[" + this.getClass().getSimpleName() + "]Step counter service started");
        } else {
            Log.i("appMedici", "[" + this.getClass().getSimpleName() + "]Found step counter service already running");
        }


        //LOCATION TRACKING
        //Check whether GPS tracking is enabled//
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.i("appMedici", "[" + this.getClass().getSimpleName() + "] GPS provider is off!!");
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
        if (lastDateStepCountersSent != null) {
            Log.i("appMedici", "[" + this.getClass().getSimpleName() + "] lastDateStepCountersSent=" + lastDateStepCountersSent);
        } else {
            Log.i("appMedici", "[" + this.getClass().getSimpleName() + "] lastDateStepCountersSent= null");
        }

        if (lastDateStepCountersSent == null || !todayRepr.equals(lastDateStepCountersSent)) {
            Log.i("appMedici", "[" + this.getClass().getSimpleName() + "] creating intent SendFinalizedStepCountersService");
            Intent sendStepCountersIntent = new Intent(this, SendFinalizedStepCountersService.class);
            startService(sendStepCountersIntent);
            SaveSharedPreference.setLastTimeStepcountersSent(this, todayRepr);
        }

        String lastDateFormsRequested = SaveSharedPreference.getLastTimeQuestionnairesRequested(this);
        if (lastDateStepCountersSent != null) {
            Log.i("appMedici", "[" + this.getClass().getSimpleName() + "] lastDateFormsRequested=" + lastDateFormsRequested);
        } else {
            Log.i("appMedici", "[" + this.getClass().getSimpleName() + "] lastDateFormsRequested= null");
        }
        if (lastDateFormsRequested == null || !todayRepr.equals(lastDateFormsRequested)) {
            Log.i("appMedici", "[" + this.getClass().getSimpleName() + "] creating intent DownloadAssignedFormsService");
            Intent downloadFormsIntent = new Intent(this, DownloadAssignedFormsService.class);
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


    @Override
    public void onItemClick(View view, int position) {
        Class activityClass;

        switch (position) {
            case 0:
                activityClass = NewFormListActivity.class;
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
                activityClass = NewFormListActivity.class;
                break;
        }

        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
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
    }

    /*public void cardViewSelected(View view) {
        Class activityClass;

        switch (view.getId()) {
            case R.id.card_ctcae_form:
                //activityClass = FormListActivity.class;
                activityClass = NewFormListActivity.class;
                break;
            case R.id.card_sensors:
                //activityClass = StepCounterActivity.class;
                activityClass = PieChartStepCounterActivity.class;
                //activityClass = PieChartHelloStepCounterActivity.class;
                break;
            case R.id.card_messages:
                activityClass = MessageListActivity.class;
                break;
            case R.id.card_calendar:
                activityClass = CalendarActivity.class;
                break;
            //case R.id.card_negative_event:
            case R.id.card_prescriptions:
                activityClass = MessageListActivity.class;
                break;
            //case R.id.card_user_profile:
            //case R.id.card_settings:
            default:
                activityClass = NewFormListActivity.class;
                break;
        }

        Intent intent = new Intent(this, activityClass);
        intent.putExtra("token", authorizationToken);
        startActivity(intent);
    }*/

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
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
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
    private void checkAuthorizationToken() {
        String usr = "james";
        String pwd = "bush";
        String authorizationToken = SaveSharedPreference.getAuthorizationToken(this);
        if (authorizationToken == null) {
            NetworkUtils.requestNewAuthorizationToken(pwd, usr, this);
        }
    }


}
