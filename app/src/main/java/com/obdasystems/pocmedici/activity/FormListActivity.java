package com.obdasystems.pocmedici.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.adapter.FormListAdapter;
import com.obdasystems.pocmedici.asyncresponse.InsertQuestionnairesAsyncResponse;
import com.obdasystems.pocmedici.message.helper.DividerItemDecoration;
import com.obdasystems.pocmedici.network.ApiClient;
import com.obdasystems.pocmedici.network.ItcoService;
import com.obdasystems.pocmedici.network.RestForm;
import com.obdasystems.pocmedici.network.RestFormPage;
import com.obdasystems.pocmedici.network.RestFormQuestion;
import com.obdasystems.pocmedici.network.RestPossibleAnswer;
import com.obdasystems.pocmedici.network.interceptors.AuthenticationInterceptor;
import com.obdasystems.pocmedici.persistence.entities.CtcaeForm;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormPage;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormQuestion;
import com.obdasystems.pocmedici.persistence.entities.CtcaePossibleAnswer;
import com.obdasystems.pocmedici.persistence.entities.JoinFormWithMaxPageNumberData;
import com.obdasystems.pocmedici.persistence.repository.CtcaeFormRepository;
import com.obdasystems.pocmedici.persistence.viewmodel.CtcaeFormListViewModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormListActivity extends AppActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        FormListAdapter.FormAdapterListener,
        InsertQuestionnairesAsyncResponse {
    private CtcaeFormListViewModel formListViewModel;
    private List<JoinFormWithMaxPageNumberData> forms = new ArrayList<>();
    private RecyclerView recyclerView;
    private FormListAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Context ctx;
    private int restCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ctx = this;
        setContentView(R.layout.activity_form_list_new);

        Toolbar toolbar = (Toolbar) findViewById(R.id.new_form_list_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
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

        mAdapter = new FormListAdapter(this, forms, this);
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

    /* **********************
     * SWIPE REFRESH METHODS
     ************************/

    @Override
    public void onRefresh() {
        downloadAssignedForms();
    }

    /* ************************
     * ADAPTER LISTENER METHODS
     **************************/

    @Override
    public void onIconClicked(int position) {

    }

    @Override
    public void onFormRowClicked(int position) {
        JoinFormWithMaxPageNumberData clickedForm = mAdapter.getFormAtPosition(position);
        if (clickedForm != null) {
            Intent intent = new Intent(this, CtcaeFormActivity.class);
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

        ItcoService apiService = ApiClient
                .forService(ItcoService.class)
                .baseURL(ApiClient.BASE_URL)
                .logging(HttpLoggingInterceptor.Level.BODY)
                .addInterceptor(new AuthenticationInterceptor(this))
                .build();

        apiService.getQuestionnaires()
                .enqueue(new Callback<List<RestForm>>() {
                    @Override
                    public void onResponse(Call<List<RestForm>> call,
                                           Response<List<RestForm>> response) {
                        if (response.isSuccessful()) {
                            Log.w(tag(), "Forms downloaded!!");
                            insertInLocalDatabase(response.body());
                        } else {
                            Log.e(tag(), "Unable to download forms");
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RestForm>> call, Throwable t) {
                        Log.e(tag(), "Unable to download forms: ", t);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    private void insertInLocalDatabase(List<RestForm> restForms) {
        Log.i(tag(), "insertInLocalDatabase " + restForms.size());
        if (!restForms.isEmpty()) {
            List<CtcaeForm> forms = new LinkedList<>();
            List<CtcaeFormPage> pages = new LinkedList<>();
            List<CtcaeFormQuestion> questions = new LinkedList<>();
            List<CtcaePossibleAnswer> answers = new LinkedList<>();
            for (RestForm rf : restForms) {
                CtcaeForm form = new CtcaeForm(rf);
                form.setFormInstructions(t(R.string.form_instructions));
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
            FormListActivity.InsertQuestionnairesQueryAsyncTask task = new FormListActivity.InsertQuestionnairesQueryAsyncTask(this, forms, pages, questions, answers, this);
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

        InsertQuestionnairesQueryAsyncTask(@NonNull Context context,
                                           @NonNull List<CtcaeForm> forms,
                                           @NonNull List<CtcaeFormPage> pages,
                                           @NonNull List<CtcaeFormQuestion> questions,
                                           @NonNull List<CtcaePossibleAnswer> answers,
                                           @NonNull InsertQuestionnairesAsyncResponse delegate) {
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
            delegate.insertQuestionnairesQueryAsyncTaskFinished();
        }
    }
}


