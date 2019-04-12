package com.obdasystems.pocmedici.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.obdasystems.pocmedici.network.MediciApiClient;
import com.obdasystems.pocmedici.network.MediciApiInterface;
import com.obdasystems.pocmedici.network.NetworkUtils;
import com.obdasystems.pocmedici.network.RestForm;
import com.obdasystems.pocmedici.network.RestFormPage;
import com.obdasystems.pocmedici.network.RestFormQuestion;
import com.obdasystems.pocmedici.network.RestPossibleAnswer;
import com.obdasystems.pocmedici.persistence.entities.CtcaeForm;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormPage;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormQuestion;
import com.obdasystems.pocmedici.persistence.entities.CtcaePossibleAnswer;
import com.obdasystems.pocmedici.persistence.repository.CtcaeFormRepository;
import com.obdasystems.pocmedici.utils.SaveSharedPreference;

import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadAssignedFormsService extends Service {

    private int counter;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("appMedici", "["+this.getClass()+"] Step counter service started");
        counter = 0;
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
        Context ctx = this;
        if(counter<25) {
            counter++;
            String usr = "james";
            String pwd = "bush";

            String authorizationToken = SaveSharedPreference.getAuthorizationToken(this);
            if (authorizationToken == null) {
                NetworkUtils.requestNewAuthorizationToken(pwd, usr, this);
            }
            authorizationToken = SaveSharedPreference.getAuthorizationToken(this);

            MediciApiInterface apiService = MediciApiClient.createService(MediciApiInterface.class, authorizationToken);

            Call<List<RestForm>> call = apiService.getQuestionnaires();
            call.enqueue(new Callback<List<RestForm>>() {
                @Override
                public void onResponse(Call<List<RestForm>> call, Response<List<RestForm>> response) {
                    if (response.isSuccessful()) {
                        insertInLocalDatabase(response.body());
                    } else {
                        switch (response.code()) {
                            case 401:
                                NetworkUtils.requestNewAuthorizationToken(pwd, usr, ctx);
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to download forms (401)");
                                if (!SaveSharedPreference.getAuthorizationIssue(ctx)) {
                                    downloadAssignedForms();
                                } else {
                                    String issueDescription = SaveSharedPreference.getAuthorizationIssueDescription(ctx);
                                    Toast.makeText(getApplicationContext(), "Unable to download forms  (401) [" + issueDescription + "]", Toast.LENGTH_LONG).show();
                                    Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to download forms  (401) [" + issueDescription + "]");

                                }
                                break;
                            case 404:
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to download forms  (404)");
                                Toast.makeText(getApplicationContext(), "Unable to download forms  (404)", Toast.LENGTH_LONG).show();
                                break;
                            case 500:
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to download forms  (500)");
                                Toast.makeText(getApplicationContext(), "Unable to download forms  (500)", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to download forms  (UNKNOWN)");
                                Toast.makeText(getApplicationContext(), "Unable to download forms (UNKNOWN)", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<RestForm>> call, Throwable t) {
                    Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to download forms : " + t.getMessage());
                    Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to download forms : " + t.getStackTrace());
                    Toast.makeText(getApplicationContext(), "Unable to download forms ..", Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Max number of calls to getInbox() reached!!");
            Toast.makeText(getApplicationContext(), "Max number of calls to getInbox() reached!!", Toast.LENGTH_LONG).show();
        }
    }

    private void insertInLocalDatabase(List<RestForm> restForms) {
        if(!restForms.isEmpty()) {
            List<CtcaeForm> forms = new LinkedList<>();
            List<CtcaeFormPage> pages = new LinkedList<>();
            List<CtcaeFormQuestion> questions = new LinkedList<>();
            List<CtcaePossibleAnswer> answers = new LinkedList<>();
            for(RestForm rf:restForms) {
                CtcaeForm form = new CtcaeForm(rf);
                forms.add(form);
                List<RestFormPage> restFormPages = rf.getPages();
                for (RestFormPage rfp : restFormPages) {
                    CtcaeFormPage currPage = new CtcaeFormPage(rfp, form.getId());
                    pages.add(currPage);
                    List<RestFormQuestion> restFormQuestions = rfp.getQuestions();
                    for (RestFormQuestion rfq : restFormQuestions) {
                        CtcaeFormQuestion currQuestion = new CtcaeFormQuestion(rfq, form.getId(), currPage.getId());
                        questions.add(currQuestion);
                        List<RestPossibleAnswer> restPossibleAnswers = rfq.getAnswers();
                        for (RestPossibleAnswer rpa : restPossibleAnswers) {
                            CtcaePossibleAnswer currAnswer = new CtcaePossibleAnswer(rpa, form.getId(), currPage.getId(), currQuestion.getId());
                            answers.add(currAnswer);
                        }
                    }
                }
            }
            InsertQuestionnairesQueryAsyncTask task = new InsertQuestionnairesQueryAsyncTask(this,forms, pages, questions, answers);
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


        InsertQuestionnairesQueryAsyncTask(Context context, List<CtcaeForm> forms, List<CtcaeFormPage> pages,
                                          List<CtcaeFormQuestion> questions, List<CtcaePossibleAnswer> answers) {
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
            for(CtcaeForm form:forms) {
                repository.insertForm(form);
            }
            for(CtcaeFormPage page:pages) {
                repository.insertFormPage(page);
            }
            for(CtcaeFormQuestion quest:questions) {
                repository.insertFormQuestion(quest);
            }
            for(CtcaePossibleAnswer answ:answers) {
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

