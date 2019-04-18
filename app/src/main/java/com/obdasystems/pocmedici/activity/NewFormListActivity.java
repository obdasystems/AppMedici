package com.obdasystems.pocmedici.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.adapter.NewFormListAdapter;
import com.obdasystems.pocmedici.asyncresponse.InsertQuestionnairesAsyncResponse;
import com.obdasystems.pocmedici.message.helper.DividerItemDecoration;
import com.obdasystems.pocmedici.network.MediciApi;
import com.obdasystems.pocmedici.network.MediciApiClient;
import com.obdasystems.pocmedici.network.NetworkUtils;
import com.obdasystems.pocmedici.network.RestForm;
import com.obdasystems.pocmedici.network.RestFormPage;
import com.obdasystems.pocmedici.network.RestFormQuestion;
import com.obdasystems.pocmedici.network.RestPossibleAnswer;
import com.obdasystems.pocmedici.persistence.entities.CtcaeForm;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormPage;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormQuestion;
import com.obdasystems.pocmedici.persistence.entities.CtcaePossibleAnswer;
import com.obdasystems.pocmedici.persistence.entities.JoinFormWithMaxPageNumberData;
import com.obdasystems.pocmedici.persistence.repository.CtcaeFormRepository;
import com.obdasystems.pocmedici.persistence.viewmodel.CtcaeFormListViewModel;
import com.obdasystems.pocmedici.utils.SaveSharedPreference;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewFormListActivity extends AppActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        NewFormListAdapter.FormAdapterListener,
        InsertQuestionnairesAsyncResponse {
    private CtcaeFormListViewModel formListViewModel;
    private List<JoinFormWithMaxPageNumberData> forms= new ArrayList<>();
    private RecyclerView recyclerView;
    private NewFormListAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Context ctx;
    private int restCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ctx = this;
        setContentView(R.layout.activity_form_list_new);

        Toolbar toolbar = (Toolbar) findViewById(R.id.new_form_list_toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_black_24dp);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMain();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.form_list_recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.form_list_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new NewFormListAdapter(this, forms, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        formListViewModel = ViewModelProviders.of(this).get(CtcaeFormListViewModel.class);
        formListViewModel.getAllForms().observe(this, new Observer<List<JoinFormWithMaxPageNumberData>>() {
            @Override
            public void onChanged(@Nullable List<JoinFormWithMaxPageNumberData> ctcaeForms) {
                mAdapter.setForms(ctcaeForms);
                mAdapter.notifyDataSetChanged();
            }
        });

        // show loader and fetch forms
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        downloadAssignedForms();
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        backToMain();
    }

    private void backToMain() {
        Intent mainIntent = new Intent(ctx, MainActivity.class);
        startActivity(mainIntent);
    }

    /************************
     * SWIPE REFRESH METHODS
     *
     ************************/
    @Override
    public void onRefresh() {
        downloadAssignedForms();
    }

    /**************************
     * ADAPTER LISTENER METHODS
     *
     **************************/
    @Override
    public void onIconClicked(int position) {

    }

    @Override
    public void onFormRowClicked(int position) {
        JoinFormWithMaxPageNumberData clickedForm = mAdapter.getFormAtPosition(position);
        if(clickedForm!=null) {
            Intent intent = new Intent(this,CtcaeFormActivity.class);
            intent.putExtra("clickedForm", clickedForm);
            //startActivityForResult(intent, CTCAE_FORM_SUBMITTED_CODE);
            startActivity(intent);
        }
    }

    /* **********************
     * REFRESH FORM LIST
     ************************/

    private void downloadAssignedForms() {
        swipeRefreshLayout.setRefreshing(true);
        Context ctx = this;
        if(restCounter<25) {
            restCounter++;
            String usr = "james";
            String pwd = "bush";

            String authorizationToken = SaveSharedPreference.getAuthorizationToken(this);
            if (authorizationToken == null) {
                NetworkUtils.requestNewAuthorizationToken(pwd, usr, this);
            }
            authorizationToken = SaveSharedPreference.getAuthorizationToken(this);

            MediciApi apiService = MediciApiClient.createService(MediciApi.class, authorizationToken);

            Call<List<RestForm>> call = apiService.getQuestionnaires();
            call.enqueue(new Callback<List<RestForm>>() {
                @Override
                public void onResponse(Call<List<RestForm>> call, Response<List<RestForm>> response) {
                    if (response.isSuccessful()) {
                        Log.i("appMedici", "[" + this.getClass().getSimpleName() + "] Forms downloaded!! ("+response.body().size()+")");
                        insertInLocalDatabase(response.body());
                        restCounter = 0;
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
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                                break;
                            case 404:
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to download forms  (404)");
                                Toast.makeText(getApplicationContext(), "Unable to download forms  (404)", Toast.LENGTH_LONG).show();
                                swipeRefreshLayout.setRefreshing(false);
                                break;
                            case 500:
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to download forms  (500)");
                                Toast.makeText(getApplicationContext(), "Unable to download forms  (500)", Toast.LENGTH_LONG).show();
                                swipeRefreshLayout.setRefreshing(false);
                                break;
                            default:
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to download forms  (UNKNOWN)");
                                Toast.makeText(getApplicationContext(), "Unable to download forms (UNKNOWN)", Toast.LENGTH_LONG).show();
                                swipeRefreshLayout.setRefreshing(false);
                                break;
                        }
                    }
                }

                @Override
                public void onFailure(Call<List<RestForm>> call, Throwable t) {
                    Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to download forms : " + t.getMessage());
                    Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to download forms : " + t.getStackTrace());
                    Toast.makeText(getApplicationContext(), "Unable to download forms ..", Toast.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
        else {
            swipeRefreshLayout.setRefreshing(false);
            Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Max number of calls to downloadAssignedForms() reached!!");
            Toast.makeText(getApplicationContext(), "Max number of calls to downloadAssignedForms() reached!!", Toast.LENGTH_LONG).show();
            restCounter = 0;
        }
    }

    private void insertInLocalDatabase(List<RestForm> restForms) {
        Log.i("appMedici", "insertInLocalDatabase "+restForms.size());
        if(!restForms.isEmpty()) {
            List<CtcaeForm> forms = new LinkedList<>();
            List<CtcaeFormPage> pages = new LinkedList<>();
            List<CtcaeFormQuestion> questions = new LinkedList<>();
            List<CtcaePossibleAnswer> answers = new LinkedList<>();
            for(RestForm rf:restForms) {
                CtcaeForm form = new CtcaeForm(rf);
                form.setFormInstructions(t(R.string.form_instructions).toString());
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
            NewFormListActivity.InsertQuestionnairesQueryAsyncTask task = new NewFormListActivity.InsertQuestionnairesQueryAsyncTask(this,forms, pages, questions, answers,this);
            task.execute();
        }
    }

    @Override
    public void insertQuestionnairesQueryAsyncTaskFinished() {
        swipeRefreshLayout.setRefreshing(false);
    }

    private static class InsertQuestionnairesQueryAsyncTask extends AsyncTask<Void, Void, Void> {
        private Context ctx;
        private CtcaeFormRepository repository;

        private List<CtcaeForm> forms;
        private List<CtcaeFormPage> pages;
        private List<CtcaeFormQuestion> questions;
        private List<CtcaePossibleAnswer> answers;

        private InsertQuestionnairesAsyncResponse delegate;

        InsertQuestionnairesQueryAsyncTask(Context context, List<CtcaeForm> forms, List<CtcaeFormPage> pages,
                                           List<CtcaeFormQuestion> questions, List<CtcaePossibleAnswer> answers,
                                           InsertQuestionnairesAsyncResponse delegate) {
            ctx = context;
            this.forms = forms;
            this.pages = pages;
            this.questions = questions;
            this.answers = answers;
            this.delegate = delegate;
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
        protected void onPostExecute(Void v){
                super.onPostExecute(v);
                delegate.insertQuestionnairesQueryAsyncTaskFinished();
        }
    }
}


