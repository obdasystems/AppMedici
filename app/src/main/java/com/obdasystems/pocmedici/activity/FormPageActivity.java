package com.obdasystems.pocmedici.activity;

import android.app.Application;
import android.app.ProgressDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.adapter.FormQuestionListAdapter;
import com.obdasystems.pocmedici.asyncresponse.PageQuestionsAsyncResponse;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormPage;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormQuestion;
import com.obdasystems.pocmedici.persistence.entities.JoinFormPageQuestionsWithPossibleAnswerData;
import com.obdasystems.pocmedici.persistence.repository.CtcaeFormQuestionsRepository;
import com.obdasystems.pocmedici.persistence.viewmodel.CtcaeFormPageViewModel;
import com.obdasystems.pocmedici.persistence.viewmodel.factory.FormPageViewModelFactory;

import java.util.LinkedList;
import java.util.List;

public class FormPageActivity extends AppCompatActivity implements PageQuestionsAsyncResponse {

    private int currentPageIndex = 1;
    private int totalPagecount = 0;
    private List<CtcaeFormPage> formPages;
    private CtcaeFormPageViewModel viewModel;
    private Button nextPageButton;
    private Button submitFormButton;
    private RecyclerView recyclerView;
    private FormQuestionListAdapter adapter;
    private CtcaeFormPage currPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctcae_formpage);
        formPages = new LinkedList<>();

        Intent intent = getIntent();
        totalPagecount = intent.getIntExtra("pageCount", 0);
        for(int i=0;i<totalPagecount;i++) {
            int actualPageNumber = i+1;
            String extraName = "page_"+actualPageNumber;
            CtcaeFormPage page = intent.getParcelableExtra(extraName);
            formPages.add(page);
        }


        nextPageButton = findViewById(R.id.nextPageButton);
        submitFormButton = findViewById(R.id.submitFormButton);

        recyclerView = findViewById(R.id.formPageRecyclerView);
        adapter = new FormQuestionListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(totalPagecount>0) {
            currentPageIndex = 1;

            if(currentPageIndex<totalPagecount) {
                submitFormButton.setVisibility(View.INVISIBLE);
            }
            else {
                nextPageButton.setVisibility(View.INVISIBLE);
            }

            currPage = formPages.get(currentPageIndex-1);

            String pageDescr = currPage.getPageTitle() + "\nPage nr:" +currPage.getPageNumber() +"\n" +currPage.getPageInstructions();

            TextView pageDescrTextView = findViewById(R.id.PageDescriptionTextView);
            pageDescrTextView.setText(pageDescr);

            QueryAsyncTask task = new QueryAsyncTask(currPage.getId(),this,this.getApplication(),this);
            task.execute();

            /*viewModel = ViewModelProviders.of(this,new FormPageViewModelFactory(this.getApplication(),getCurrentPageId())).get(CtcaeFormPageViewModel.class);
            viewModel.getPageQuestionsWithAnswers().observe(this, new Observer<List<JoinFormPageQuestionsWithPossibleAnswerData>>() {
                @Override
                public void onChanged(@Nullable List<JoinFormPageQuestionsWithPossibleAnswerData> questionsWithAnswers) {
                    adapter.setQuestionsWithAnswers(questionsWithAnswers);
                    adapter.notifyDataSetChanged();
                }
            });
            viewModel.getPageQuestions().observe(this, new Observer<List<CtcaeFormQuestion>>() {
                @Override
                public void onChanged(@Nullable List<CtcaeFormQuestion> questions) {
                    adapter.setQuestions(questions);
                    adapter.notifyDataSetChanged();
                }
            });*/
        }
        else {
            //view model vuoto
        }
    }

    @Override
    public void taskFinished(FormQuestionsContainer container) {
        adapter.setQuestions(container.questions);
        adapter.setQuestionsWithAnswers(container.questionsWithAnswers);
        adapter.notifyDataSetChanged();
    }

    private int getCurrentPageId() {
        return formPages.get(currentPageIndex-1).getId();
    }

    public void onClickNextPage(View view) {
        currentPageIndex++;

        if(currentPageIndex<totalPagecount) {
            submitFormButton.setVisibility(View.INVISIBLE);
            nextPageButton.setVisibility(View.VISIBLE);
        }
        else {
            nextPageButton.setVisibility(View.INVISIBLE);
            submitFormButton.setVisibility(View.VISIBLE);
        }

        currPage = formPages.get(currentPageIndex-1);

        String pageDescr = currPage.getPageTitle() + "\nPage nr:" +currPage.getPageNumber() +"\n" +currPage.getPageInstructions();

        TextView pageDescrTextView = findViewById(R.id.PageDescriptionTextView);
        pageDescrTextView.setText(pageDescr);

        int currId = getCurrentPageId();

        QueryAsyncTask task = new QueryAsyncTask(currPage.getId(),this,this.getApplication(),this);
        task.execute();
    }


    private static class QueryAsyncTask extends AsyncTask<Void, Void, FormQuestionsContainer> {
        private Context ctx;
        private ProgressDialog progDial;
        private int pageId;
        private CtcaeFormQuestionsRepository repository;
        private Application app;
        private PageQuestionsAsyncResponse delegate;

        QueryAsyncTask(int pageId, Context context, Application app, PageQuestionsAsyncResponse delegate) {
            ctx = context;
            this.pageId = pageId;
            progDial = new ProgressDialog(ctx);
            this.app = app;
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDial.setMessage("Retrieving questions...");
            progDial.setIndeterminate(false);
            progDial.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDial.setCancelable(false);
            progDial.show();
        }

        @Override
        protected FormQuestionsContainer doInBackground(Void... voids) {
            repository = new CtcaeFormQuestionsRepository(app, pageId);
            List<CtcaeFormQuestion> questions = repository.getAllQuestions();
            List<JoinFormPageQuestionsWithPossibleAnswerData> answers = repository.getAllQuestionsWithAnswers();
            FormQuestionsContainer cont = new FormQuestionsContainer(answers,questions);
            return cont;
        }

        @Override
        protected void onPostExecute(FormQuestionsContainer formQuestionsContainer) {
            super.onPostExecute(formQuestionsContainer);
            progDial.dismiss();
            delegate.taskFinished(formQuestionsContainer);
        }
    }


    public static class FormQuestionsContainer {
        List<JoinFormPageQuestionsWithPossibleAnswerData> questionsWithAnswers;
        List<CtcaeFormQuestion> questions;

        FormQuestionsContainer(List<JoinFormPageQuestionsWithPossibleAnswerData> answers, List<CtcaeFormQuestion> quest) {
            questionsWithAnswers = answers;
            questions = quest;
        }
    }
}
