package com.obdasystems.pocmedici.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.obdasystems.pocmedici.network.ApiClient;
import com.obdasystems.pocmedici.network.ItcoService;
import com.obdasystems.pocmedici.network.RestForm;
import com.obdasystems.pocmedici.network.RestFormPage;
import com.obdasystems.pocmedici.network.RestFormQuestion;
import com.obdasystems.pocmedici.network.RestPossibleAnswer;
import com.obdasystems.pocmedici.persistence.entities.CtcaeForm;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormPage;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormQuestion;
import com.obdasystems.pocmedici.persistence.entities.CtcaePossibleAnswer;
import com.obdasystems.pocmedici.persistence.repository.CtcaeFormRepository;
import com.obdasystems.pocmedici.utils.AppPreferences;

import java.util.LinkedList;
import java.util.List;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadAssignedFormsService extends Service {
    private static final String TAG = DownloadAssignedFormsService.class.getSimpleName();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "DownloadAssignedFormsService started");
        downloadAssignedForms();
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

    private void downloadAssignedForms() {
        if (AppPreferences.with(this).contains("authorization_token")) {
            Log.w(TAG,
                    "No authorization token found, forms not downloaded");
            return;
        }

        ItcoService apiService = (ItcoService) ApiClient
                .forService(ItcoService.class)
                .baseURL(ApiClient.BASE_URL)
                .logging(HttpLoggingInterceptor.Level.BODY)
                .build();

        apiService.getQuestionnaires()
                .enqueue(new Callback<List<RestForm>>() {
                    @Override
                    public void onResponse(Call<List<RestForm>> call,
                                           Response<List<RestForm>> response) {
                        if (response.isSuccessful()) {
                            Log.i(TAG,
                                    "Forms downloaded!! (" + response.body().size() + ")");
                            insertInLocalDatabase(response.body());
                        } else {
                            Log.e(TAG,
                                    "Unable to download forms (" + response.code() + ")");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RestForm>> call, Throwable t) {
                        Log.e(TAG, "Unable to download forms: ", t);
                    }
                });
    }

    // FIXME: fix persistence of forms
    private void insertInLocalDatabase(List<RestForm> restForms) {
        Log.i(TAG, "insertInLocalDatabase " + restForms.size());
        if (!restForms.isEmpty()) {
            List<CtcaeForm> forms = new LinkedList<>();
            List<CtcaeFormPage> pages = new LinkedList<>();
            List<CtcaeFormQuestion> questions = new LinkedList<>();
            List<CtcaePossibleAnswer> answers = new LinkedList<>();
            for (RestForm rf : restForms) {
                CtcaeForm form = new CtcaeForm(rf);
                forms.add(form);
                List<RestFormPage> restFormPages = rf.getPages();
                for (RestFormPage rfp : restFormPages) {
                    CtcaeFormPage currPage = new CtcaeFormPage(rfp, form.getId());
                    pages.add(currPage);
                    List<RestFormQuestion> restFormQuestions = rfp.getQuestions();
                    for (RestFormQuestion rfq : restFormQuestions) {
                        CtcaeFormQuestion currQuestion =
                                new CtcaeFormQuestion(rfq, form.getId(), currPage.getId());
                        questions.add(currQuestion);
                        List<RestPossibleAnswer> restPossibleAnswers = rfq.getAnswers();
                        for (RestPossibleAnswer rpa : restPossibleAnswers) {
                            CtcaePossibleAnswer currAnswer =
                                    new CtcaePossibleAnswer(rpa, form.getId(),
                                            currPage.getId(), currQuestion.getId());
                            answers.add(currAnswer);
                        }
                    }
                }
            }

            InsertQuestionnairesQueryAsyncTask task =
                    new InsertQuestionnairesQueryAsyncTask(
                            this, forms, pages, questions, answers);
            task.execute();
        }
    }

    private static class InsertQuestionnairesQueryAsyncTask extends AsyncTask<Void, Void, Void> {
        private Context ctx;
        private CtcaeFormRepository repository;

        private List<CtcaeForm> forms;
        private List<CtcaeFormPage> pages;
        private List<CtcaeFormQuestion> questions;
        private List<CtcaePossibleAnswer> answers;

        InsertQuestionnairesQueryAsyncTask(@NonNull Context context,
                                           @NonNull List<CtcaeForm> forms,
                                           @NonNull List<CtcaeFormPage> pages,
                                           @NonNull List<CtcaeFormQuestion> questions,
                                           @NonNull List<CtcaePossibleAnswer> answers) {
            ctx = context;
            this.forms = forms;
            this.pages = pages;
            this.questions = questions;
            this.answers = answers;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            repository = new CtcaeFormRepository(ctx);
            for (CtcaeForm form : forms) {
                repository.insertForm(form);
            }
            for (CtcaeFormPage page : pages) {
                repository.insertFormPage(page);
            }
            for (CtcaeFormQuestion quest : questions) {
                repository.insertFormQuestion(quest);
            }
            for (CtcaePossibleAnswer answ : answers) {
                repository.insertFormAnswer(answ);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
        }
    }

}

