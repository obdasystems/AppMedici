package com.obdasystems.pocmedici.service;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.obdasystems.pocmedici.asyncresponse.PageQuestionsAsyncResponse;
import com.obdasystems.pocmedici.persistence.repository.StepCounterRepository;
import com.obdasystems.pocmedici.stepdetector.CustomStepDetector;
import com.obdasystems.pocmedici.stepdetector.CustomStepListener;

import java.util.Calendar;

public class StepCounterService extends Service implements SensorEventListener, CustomStepListener {

    private boolean hwStepDetectorEnabled;

    private SensorManager mSensorManager;
    private Sensor mStepDetectorSensor;

    private Sensor mAccelerometerSensor;
    private CustomStepDetector mCustomDetector;

    private StepCounterRepository repository;

    public StepCounterService() {
        super();
        /*repository = new StepCounterRepository(app);
        mSensorManager = (SensorManager) app.getSystemService(Context.SENSOR_SERVICE);;
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        }*/
    }

    public StepCounterService(Application app) {
        super();
        repository = new StepCounterRepository(app);
        mSensorManager = (SensorManager) app.getSystemService(Context.SENSOR_SERVICE);;
        if(mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            mStepDetectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            mSensorManager.registerListener(this, mStepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            hwStepDetectorEnabled = true;
            Log.i("appMedici", "["+this.getClass()+"] HW Step counter service created");
        }
        else {
            hwStepDetectorEnabled = false;
            mCustomDetector = new CustomStepDetector();
            mCustomDetector.registerListener(this);
            mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);
            Log.i("appMedici", "["+this.getClass()+"] SW Step counter service created");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("appMedici", "["+this.getClass()+"] Step counter service started");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
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
                    Log.i("appMedici", "["+this.getClass()+"]Step  detected HW");
                    QueryAsyncTask task = new QueryAsyncTask(year, month, day, (int)detStep, repository);
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
        Log.i("appMedici", "["+this.getClass()+"]Step  detected SW");
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        QueryAsyncTask task = new QueryAsyncTask(year, month, day, 1, repository);
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


