package com.obdasystems.pocmedici.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.obdasystems.pocmedici.asyncresponse.StepCountersToSendAsyncResponse;
import com.obdasystems.pocmedici.network.MediciApi;
import com.obdasystems.pocmedici.network.MediciApiClient;
import com.obdasystems.pocmedici.network.NetworkUtils;
import com.obdasystems.pocmedici.network.RestStepCounter;
import com.obdasystems.pocmedici.persistence.entities.StepCounter;
import com.obdasystems.pocmedici.persistence.repository.StepCounterRepository;
import com.obdasystems.pocmedici.utils.SaveSharedPreference;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendFinalizedStepCountersService extends Service
        implements StepCountersToSendAsyncResponse {
    private static final String TAG = SendFinalizedStepCountersService.class.getSimpleName();
    private int counter;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "SendFinalizedStepCountersService started");
        counter = 0;
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
        if(counter<25) {
            Log.i(TAG, "Sending step counters ");
            String usr = "james";
            String pwd = "bush";
            counter++;
            Context ctx = this;
            String authorizationToken = SaveSharedPreference.getAuthorizationToken(this);
            if (authorizationToken == null) {
                NetworkUtils.requestNewAuthorizationToken(pwd, usr, this);
            }
            authorizationToken = SaveSharedPreference.getAuthorizationToken(this);

            MediciApi apiService = MediciApiClient.createService(MediciApi.class, authorizationToken);

            for(StepCounter sp: stepCounters) {
                Calendar cal = Calendar.getInstance();
                cal.set(sp.getYear(), sp.getMonth(), sp.getDay());
                long timestamp = cal.getTimeInMillis();

                RestStepCounter rsc = new RestStepCounter(sp.getStepCount(), timestamp);

                apiService.sendStepCount(rsc).enqueue(new Callback<JSONObject>() {
                    @Override
                    public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
                        if (response.isSuccessful()) {
                            Log.i(TAG, "Step count sent to server." + response.body().toString());
                            counter=0;
                            FinalizeStepCounterQueryAsyncTask task = new FinalizeStepCounterQueryAsyncTask(ctx,sp);
                            task.execute();
                        } else {
                            switch (response.code()) {
                                case 401:
                                    NetworkUtils.requestNewAuthorizationToken(pwd, usr, ctx);
                                    Log.e(TAG, "Unable to send Step count (401)");
                                    if (!SaveSharedPreference.getAuthorizationIssue(ctx)) {
                                        getStepCounterToSendQueryAsyncTaskFinished(stepCounters);
                                    } else {
                                        String issueDescription = SaveSharedPreference.getAuthorizationIssueDescription(ctx);
                                        Toast.makeText(getApplicationContext(), "Unable to send Step count (401) [" + issueDescription + "]", Toast.LENGTH_LONG).show();
                                        Log.e(TAG, "Unable to send Step count (401) [" + issueDescription + "]");
                                    }
                                    break;
                                case 404:
                                    Log.e(TAG, "Unable to send Step count (404)");
                                    Toast.makeText(getApplicationContext(), "Unable to send position (404)", Toast.LENGTH_LONG).show();
                                    break;
                                case 500:
                                    Log.e(TAG, "Unable to send Step count (500)");
                                    Toast.makeText(getApplicationContext(), "Unable to send position (500)", Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    Log.e(TAG, "Unable to send Step count (UNKNOWN)");
                                    Toast.makeText(getApplicationContext(), "Unable to send Step count (UNKNOWN)", Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JSONObject> call, Throwable t) {
                        Log.e(TAG, "Unable to send Step count : " + t.getMessage());
                        Log.e(TAG, "Unable to send Step count : " + t.getStackTrace());
                        Toast.makeText(ctx, "Unable to send Step count ..", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        else {
            Log.e(TAG, "Max number of calls to sendPositionToServer() reached!!");
            Toast.makeText(getApplicationContext(), "Max number of calls to sendPositionToServer() reached!!", Toast.LENGTH_LONG).show();
            counter=0;
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

        GetStepCounterToSendQueryAsyncTask(Context context,StepCountersToSendAsyncResponse delegate) {
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
