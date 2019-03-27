package com.obdasystems.pocmedici.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.service.StepCounterForegroundService;
import com.obdasystems.pocmedici.service.StepCounterService;

public class MainActivity extends AppCompatActivity {

    public final static String ACTION_MAIN = "action MAIN";
    public final static String ACTION_START_SERVICE = "action start service";

    private final int MAIN_LOGIN_CODE = 10000;

    private Context ctx;

    StepCounterService mSensorService;

    /*private GpsTrackingService gpsService;
    private Intent gpsServiceIntent;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ctx = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        //toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_black_24dp);
        setSupportActionBar(toolbar);

        /*Log.i("appMedici", "["+this.getClass().getSimpleName()+"] registering alarm to start gps tracking service");
        //Intent gpsIntent = new Intent(this, GpsTrackingRestarterBroadcastReceiver.class);
        Intent gpsIntent = new Intent(this, GpsTrackingService.class);

        PendingIntent gpsPendingIntent = PendingIntent.getService(this, DeviceBootReceiver.GPS_TRACKING_PENDING_INTENT_ALRM_ID, gpsIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long triggerAtMillis = 100;
        long intervalMillis = (5 * 60 * 1000);
        intervalMillis = 10000;


        //

        Calendar cal = Calendar.getInstance();
        // add 30 seconds to the calendar object
        cal.add(Calendar.SECOND, 10);
        //alarmManager.set(AlarmManager.RTC_WAKEUP,  cal.getTimeInMillis(), gpsPendingIntent);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis() , intervalMillis, gpsPendingIntent);*/

        //start step counter
        /*mSensorService = new StepCounterService(getApplication());
        Intent mServiceIntent = new Intent(this, mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            startService(mServiceIntent);
            Log.i("appMedici", "["+this.getClass()+"]Step counter service started");
        }
        else {
            Log.i("appMedici", "["+this.getClass()+"]Found step counter service already running");
        }*/

        StepCounterForegroundService forService = new StepCounterForegroundService(getApplication());
        Intent mServiceIntent = new Intent(this, forService.getClass());
        mServiceIntent.setAction(MainActivity.ACTION_START_SERVICE);
        if (!isMyServiceRunning(forService.getClass())) {
            startService(mServiceIntent);
            Log.i("appMedici", "["+this.getClass()+"]Step counter service started");
        }
        else {
            Log.i("appMedici", "["+this.getClass()+"]Found step counter service already running");
        }

        /*Intent startIntent = new Intent(getApplicationContext(), StepCounterForegroundService.class);
        startIntent.setAction(MainActivity.ACTION_START_SERVICE);
        startService(startIntent);*/

    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sp = getSharedPreferences("app_medici_login", MODE_PRIVATE);
        boolean logged = sp.getBoolean("logged",false);
        if(!logged) {
            Intent logIntent = new Intent(this,LoginActivity.class);
            startActivityForResult(logIntent, MAIN_LOGIN_CODE);
        }
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
                //activityClass = FormListActivity.class;
                activityClass = NewFormListActivity.class;
                break;
            case R.id.card_sensors:
                activityClass = StepCounterActivity.class;
                break;
            case R.id.card_messages:
                activityClass = MessageListActivity.class;
                break;
            case R.id.card_calendar:
                //activityClass = CalendarActivity.class;
                //activityClass = CustomCalendarActivity.class;
                activityClass = CalendarMaterialActivity.class;
                break;
            //case R.id.card_negative_event:
            case R.id.card_prescriptions:
                Log.i("appMedici", "["+this.getClass()+"]Step counter service destroying");
                Intent stopServiceIntent = new Intent(this, StepCounterService.class);
                stopService(stopServiceIntent);
                return;
            //case R.id.card_user_profile:
            //case R.id.card_settings:
            default:
                activityClass = NewFormListActivity.class;
                break;
        }

        Intent intent = new Intent(this, activityClass);
        intent.putExtra("name", "Bravo!!!");
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
     * SERVICES MANAGEMENT METHODS
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

}
