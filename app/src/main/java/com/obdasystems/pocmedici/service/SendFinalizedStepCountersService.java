package com.obdasystems.pocmedici.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.obdasystems.pocmedici.asyncresponse.StepCountersToSendAsyncResponse;
import com.obdasystems.pocmedici.network.ApiClient;
import com.obdasystems.pocmedici.network.ItcoService;
import com.obdasystems.pocmedici.network.RestStepCounter;
import com.obdasystems.pocmedici.network.interceptors.AuthenticationInterceptor;
import com.obdasystems.pocmedici.persistence.entities.StepCounter;
import com.obdasystems.pocmedici.persistence.repository.StepCounterRepository;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendFinalizedStepCountersService extends Service
        implements StepCountersToSendAsyncResponse {
    private static final String TAG = SendFinalizedStepCountersService.class.getSimpleName();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "SendFinalizedStepCountersService started");
        GetStepCounterToSendQueryAsyncTask task =
                new GetStepCounterToSendQueryAsyncTask(this, this);
        task.execute();
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
    public void getStepCounterToSendQueryAsyncTaskFinished(List<StepCounter> stepCounters) {
        Log.i(TAG, "Sending step counters ");

        ItcoService apiService = ApiClient
                .forService(ItcoService.class)
                .baseURL(ApiClient.BASE_URL)
                .logging(HttpLoggingInterceptor.Level.BODY)
                .addInterceptor(new AuthenticationInterceptor(this))
                .build();

        for (StepCounter sp : stepCounters) {
            Calendar cal = Calendar.getInstance();
            cal.set(sp.getYear(), sp.getMonth(), sp.getDay());
            long timestamp = cal.getTimeInMillis();
            RestStepCounter rsc = new RestStepCounter(sp.getStepCount(), timestamp);
            Context context = this;

            apiService.sendStepCount(rsc)
                    .enqueue(new Callback<JSONObject>() {
                        @Override
                        public void onResponse(Call<JSONObject> call,
                                               Response<JSONObject> response) {
                            if (response.isSuccessful()) {
                                Log.i(TAG, "Step count sent to server."
                                        + response.body().toString());
                                FinalizeStepCounterQueryAsyncTask task =
                                        new FinalizeStepCounterQueryAsyncTask(context, sp);
                                task.execute();
                            } else {
                                Log.e(TAG, "Unable to send step count");
                            }
                        }

                        @Override
                        public void onFailure(Call<JSONObject> call, Throwable t) {
                            Log.e(TAG, "Unable to send Step count: ", t);
                        }
                    });
        }
    }

    private static class FinalizeStepCounterQueryAsyncTask
            extends AsyncTask<Void, Void, Void> {
        private Context ctx;
        private StepCounterRepository repository;
        private StepCounter sp;

        FinalizeStepCounterQueryAsyncTask(Context context, StepCounter sp) {
            ctx = context;
            this.sp = sp;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            repository = new StepCounterRepository(ctx);
            repository.finalizeStepCounter(sp);
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
        }
    }

    private static class GetStepCounterToSendQueryAsyncTask
            extends AsyncTask<Void, Void, List<StepCounter>> {
        private Context ctx;
        private StepCounterRepository repository;
        private StepCountersToSendAsyncResponse delegate;

        GetStepCounterToSendQueryAsyncTask(Context context, StepCountersToSendAsyncResponse delegate) {
            ctx = context;
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<StepCounter> doInBackground(Void... voids) {
            repository = new StepCounterRepository(ctx);
            long timestamp = System.currentTimeMillis();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp);

            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);

            return repository.getNotSentStepCountersMinusCurrent(year, month, day);
        }

        @Override
        protected void onPostExecute(List<StepCounter> stepCounters) {
            super.onPostExecute(stepCounters);
            delegate.getStepCounterToSendQueryAsyncTaskFinished(stepCounters);
        }
    }

}
