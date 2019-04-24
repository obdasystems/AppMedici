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

@Deprecated
public class StepCounterService extends Service
        implements SensorEventListener, CustomStepListener {
    private static final String TAG = StepCounterService.class.getSimpleName();
    private boolean hwStepDetectorEnabled;
    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    private Sensor accelerometerSensor;
    private CustomStepDetector customDetector;
    private StepCounterRepository repository;

    public StepCounterService() {
        super();
        //repository = new StepCounterRepository(app);
        //sensorManager = (SensorManager) app.getSystemService(Context.SENSOR_SERVICE);;
        //if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
        //    stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        //}
    }

    public StepCounterService(Application app) {
        super();
        repository = new StepCounterRepository(app);
        sensorManager = (SensorManager) app.getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            sensorManager.registerListener(this,
                    stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
            hwStepDetectorEnabled = true;
            Log.i(TAG, "HW Step counter service created");
        } else {
            hwStepDetectorEnabled = false;
            customDetector = new CustomStepDetector();
            customDetector.registerListener(this);
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this,
                    accelerometerSensor, SensorManager.SENSOR_DELAY_FASTEST);
            Log.i(TAG, "SW Step counter service created");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Step counter service started");
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
                if (detStep == 1) {
                    Log.i(TAG, "Step  detected HW");
                    QueryAsyncTask task =
                            new QueryAsyncTask(year, month, day, (int) detStep, repository);
                    task.execute();
                }
                break;
            case Sensor.TYPE_ACCELEROMETER:
                customDetector.updateAccel(event.timestamp,
                        event.values[0], event.values[1], event.values[2]);
                break;
            default:
                Log.i(TAG, "Unhandled sensor event: " + event.sensor.getType());
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void step(long timeNs) {
        Log.i(TAG, "Step  detected SW");
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        QueryAsyncTask task = new QueryAsyncTask(year, month, day, 1, repository);
        task.execute();
    }

    private static class QueryAsyncTask extends AsyncTask<Void, Void, Void> {
        private Context ctx;
        private StepCounterRepository innerRepository;
        private int year, month, day, numSteps;

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


