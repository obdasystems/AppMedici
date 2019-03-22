package com.obdasystems.pocmedici.activity;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.adapter.FormQuestionListAdapter;
import com.obdasystems.pocmedici.adapter.NewFormQuestionListAdapter;
import com.obdasystems.pocmedici.asyncresponse.PageQuestionsAsyncResponse;
import com.obdasystems.pocmedici.message.helper.DividerItemDecoration;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormPage;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormQuestion;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormQuestionAnswered;
import com.obdasystems.pocmedici.persistence.entities.JoinFormPageQuestionsWithPossibleAnswerData;
import com.obdasystems.pocmedici.persistence.repository.CtcaeFillingProcessAnsweredQuestionRepository;
import com.obdasystems.pocmedici.persistence.repository.CtcaeFinalizeFillingProcessRepository;
import com.obdasystems.pocmedici.persistence.repository.CtcaeFormQuestionsRepository;
import com.obdasystems.pocmedici.persistence.repository.CtcaeIncompleteFillingProcessRepository;

import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

public class NewFormPageActivity extends AppCompatActivity implements PageQuestionsAsyncResponse {

    private int currentPageIndex = 1;
    private int totalPagecount = 0;
    private List<CtcaeFormPage> formPages;
    private List<CtcaeFormQuestion> questions = new LinkedList<>();
    private List<JoinFormPageQuestionsWithPossibleAnswerData> questionsWithAnswers= new LinkedList<>();
    private List<CtcaeFormQuestionAnswered> answeredQuestions= new LinkedList<>();

    private CtcaeFormPage currPage;
    private int fillingProcessId;
    private int formId;

    private RecyclerView recyclerView;
    private NewFormQuestionListAdapter adapter;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_form_page);
        formPages = new LinkedList<>();

        setToolbarTitle();

        Intent intent = getIntent();
        fillingProcessId = intent.getIntExtra("fillingProcessId",-1);
        formId = intent.getIntExtra("formId",-1);
        totalPagecount = intent.getIntExtra("pageCount", 0);
        for(int i=0;i<totalPagecount;i++) {
            int actualPageNumber = i+1;
            String extraName = "page_"+actualPageNumber;
            CtcaeFormPage page = intent.getParcelableExtra(extraName);
            formPages.add(page);
        }

        toolbar = (Toolbar) findViewById(R.id.new_page_form_toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_black_24dp);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPreviousPage();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        fillingProcessId = intent.getIntExtra("fillingProcessId",-1);
        formId = intent.getIntExtra("formId",-1);
        totalPagecount = intent.getIntExtra("pageCount", 0);
        for(int i=0;i<totalPagecount;i++) {
            int actualPageNumber = i+1;
            String extraName = "page_"+actualPageNumber;
            CtcaeFormPage page = intent.getParcelableExtra(extraName);
            formPages.add(page);
        }


        recyclerView =  (RecyclerView) findViewById(R.id.question_list_recycler_view);
        adapter = new NewFormQuestionListAdapter(questions, questionsWithAnswers, answeredQuestions, fillingProcessId, formId, getApplication(), this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(adapter);

        if(totalPagecount>0) {
            Log.i("appMedici","["+this.getClass()+"] onResume() currentPageIndex="+currentPageIndex);
            if(currentPageIndex<=0) {
                currentPageIndex = 1;
            }
            currPage = formPages.get(currentPageIndex-1);
            NewFormPageActivity.GetQuestionsQueryAsyncTask task = new NewFormPageActivity.GetQuestionsQueryAsyncTask(currPage.getId(), fillingProcessId, formId,this,this.getApplication(),this);
            task.execute();
        }

    }

    @Override
    public void onBackPressed() {
        goToPreviousPage();
    }

    /*****************************
     * TOOLBAR AND BUTTONS METHODS
     *****************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(currentPageIndex!=totalPagecount) {
            getMenuInflater().inflate(R.menu.form_page_menu, menu);
        }
        else {
            getMenuInflater().inflate(R.menu.form_page_last_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if(currentPageIndex!=totalPagecount) {
            getMenuInflater().inflate(R.menu.form_page_menu, menu);
        }
        else {
            getMenuInflater().inflate(R.menu.form_page_last_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.form_page_action_forward) {
            goToNextPage();
            return true;
        }
        if (id == R.id.form_page_action_submit) {
            submitForm();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setToolbarTitle() {
        NewFormPageActivity.this.setTitle("Page nr "+currentPageIndex);
    }

    private void goToNextPage() {
        currentPageIndex++;
        currPage = formPages.get(currentPageIndex-1);
        invalidateOptionsMenu();
        setToolbarTitle();
        GetQuestionsQueryAsyncTask task = new GetQuestionsQueryAsyncTask(currPage.getId(), fillingProcessId, formId,this,this.getApplication(),this);
        task.execute();
    }

    private void goToPreviousPage() {
        if(currentPageIndex == 1) {
            Intent formlistIntent = new Intent(this, NewFormListActivity.class);
            startActivity(formlistIntent);
        }
        else {
            currentPageIndex--;
            currPage = formPages.get(currentPageIndex-1);
            invalidateOptionsMenu();
            setToolbarTitle();
            GetQuestionsQueryAsyncTask task = new GetQuestionsQueryAsyncTask(currPage.getId(), fillingProcessId, formId,this,this.getApplication(),this);
            task.execute();
        }
    }

    private void submitForm() {
        GetUnansweredQuestionsQueryAsyncTask task = new GetUnansweredQuestionsQueryAsyncTask(fillingProcessId, formId,this,this.getApplication(),this);
        task.execute();
    }

    /*****************************
     * ASYNC TASKS CALLBACK
     *****************************/

    @Override
    public void getQuestionsTaskFinished(FormPageActivity.FormQuestionsContainer container) {
        adapter.setQuestions(container.questions);
        adapter.setQuestionsWithAnswers(container.questionsWithAnswers);
        adapter.setAlreadyAnsweredQuestions(container.answeredQuestions);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void getUnansweredQuestionsTaskFinished(FormPageActivity.IncompleteContainer container) {
        List<CtcaeFormQuestion> unansweredQuestions = container.unansweredQuestions;
        List<Integer> incompletePages = container.incompletePages;
        if(incompletePages.isEmpty()) {
            //finalize filling process
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTimeInMillis(System.currentTimeMillis());
            NewFormPageActivity.FinalizeFillingProcessQueryAsyncTask task = new NewFormPageActivity.FinalizeFillingProcessQueryAsyncTask(fillingProcessId, gc, this, this.getApplication(),this);
            task.execute();
        }
        else {
            //warning message
            String msg = "You have unanswered questions in pages ";
            for(int i=0;i<incompletePages.size();i++) {
                if (i > 0) {
                    if (i < (incompletePages.size() - 1)) {
                        msg += ",";
                    } else {
                        msg += " and ";
                    }
                }
                msg += incompletePages.get(i);
            }
            AlertDialog dialog = new AlertDialog.Builder(NewFormPageActivity.this).create();
            dialog.setTitle("Incomplete form alert");
            dialog.setMessage(msg);
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog.show();
        }
    }

    @Override
    public void finalizeFillingProcessTaskFinished(int result) {
        Log.i("appMedici","["+this.getClass()+"]finalized fillingProcessId="+fillingProcessId+" " +
                "update return value="+result);
        if(result>0){
            Intent formlistIntent = new Intent(this, NewFormListActivity.class);
            formlistIntent.putExtra("filledForm", formId);
            formlistIntent.putExtra("fillingProcess", fillingProcessId);
            startActivity(formlistIntent);
        }
        else {
            //warning message
            String msg = "Problems encountered while submitting filled form. Please try again";
            AlertDialog dialog = new AlertDialog.Builder(NewFormPageActivity.this).create();
            dialog.setTitle("Submit filled form alert");
            dialog.setMessage(msg);
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            dialog.show();
        }
    }

    /*****************************
     * ASYNC TASKS
     *****************************/

    //set end timestamp for current filling process
    private static class FinalizeFillingProcessQueryAsyncTask extends AsyncTask<Void, Void, Integer> {
        private Context ctx;
        private ProgressDialog progDial;
        private int fpId;
        private GregorianCalendar calendar;
        private CtcaeFinalizeFillingProcessRepository repository;


        private Application app;
        private PageQuestionsAsyncResponse delegate;

        FinalizeFillingProcessQueryAsyncTask( int fillingProcId, GregorianCalendar cal, Context context, Application app, PageQuestionsAsyncResponse delegate) {
            ctx = context;
            this.fpId = fillingProcId;
            this.calendar = cal;
            progDial = new ProgressDialog(ctx);
            this.app = app;
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDial.setMessage("Submitting filled form...");
            progDial.setIndeterminate(false);
            progDial.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDial.setCancelable(false);
            progDial.show();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            repository = new CtcaeFinalizeFillingProcessRepository(app, fpId, calendar);
            int res = repository.getUpdateReturnValue();
            return res;
        }

        @Override
        protected void onPostExecute(Integer res) {
            super.onPostExecute(res);
            progDial.dismiss();
            delegate.finalizeFillingProcessTaskFinished(res);
        }
    }

    //get all questions yet to be answered current filling process
    private static class GetUnansweredQuestionsQueryAsyncTask extends AsyncTask<Void, Void, FormPageActivity.IncompleteContainer> {
        private Context ctx;
        private ProgressDialog progDial;
        private int fpId;
        private int fId;
        private CtcaeIncompleteFillingProcessRepository repository;


        private Application app;
        private PageQuestionsAsyncResponse delegate;

        GetUnansweredQuestionsQueryAsyncTask( int fillingProcId, int formId, Context context, Application app, PageQuestionsAsyncResponse delegate) {
            ctx = context;
            this.fpId = fillingProcId;
            this.fId = formId;
            progDial = new ProgressDialog(ctx);
            this.app = app;
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDial.setMessage("Retrieving unanswered questions...");
            progDial.setIndeterminate(false);
            progDial.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDial.setCancelable(false);
            progDial.show();
        }

        @Override
        protected FormPageActivity.IncompleteContainer doInBackground(Void... voids) {
            repository = new CtcaeIncompleteFillingProcessRepository(app,fpId,fId);
            List<CtcaeFormQuestion> questions = repository.getIncompleteQuestions();
            List<Integer> pageNumbers = repository.getIncompletePages();
            FormPageActivity.IncompleteContainer container = new FormPageActivity.IncompleteContainer(questions,pageNumbers);
            return container;
        }

        @Override
        protected void onPostExecute(FormPageActivity.IncompleteContainer container) {
            super.onPostExecute(container);
            progDial.dismiss();
            delegate.getUnansweredQuestionsTaskFinished(container);
        }
    }

    public static class IncompleteContainer {
        List<CtcaeFormQuestion> unansweredQuestions;
        List<Integer> incompletePages;

        IncompleteContainer( List<CtcaeFormQuestion> unansweredQuest, List<Integer> incPages) {
            unansweredQuestions = unansweredQuest;
            incompletePages = incPages;
        }
    }

    //get all questions in page along with questions already answered in cyurrent filling process
    private static class GetQuestionsQueryAsyncTask extends AsyncTask<Void, Void, FormPageActivity.FormQuestionsContainer> {
        private Context ctx;
        private ProgressDialog progDial;
        private int pageId;
        private int fpId;
        private int fId;
        private CtcaeFormQuestionsRepository repository;
        private CtcaeFillingProcessAnsweredQuestionRepository answeredRepository;


        private Application app;
        private PageQuestionsAsyncResponse delegate;

        GetQuestionsQueryAsyncTask(int pageId, int fillingProcId, int formId, Context context, Application app, PageQuestionsAsyncResponse delegate) {
            ctx = context;
            this.fpId = fillingProcId;
            this.fId = formId;
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
        protected FormPageActivity.FormQuestionsContainer doInBackground(Void... voids) {
            repository = new CtcaeFormQuestionsRepository(app, pageId);
            List<CtcaeFormQuestion> questions = repository.getAllQuestions();
            List<JoinFormPageQuestionsWithPossibleAnswerData> answers = repository.getAllQuestionsWithAnswers();

            answeredRepository = new CtcaeFillingProcessAnsweredQuestionRepository(app,fpId,fId);
            List<CtcaeFormQuestionAnswered> answeredQuestions = answeredRepository.getAnsweredQuestions();

            FormPageActivity.FormQuestionsContainer cont = new FormPageActivity.FormQuestionsContainer(answers,questions, answeredQuestions);
            return cont;
        }

        @Override
        protected void onPostExecute(FormPageActivity.FormQuestionsContainer formQuestionsContainer) {
            super.onPostExecute(formQuestionsContainer);
            progDial.dismiss();
            delegate.getQuestionsTaskFinished(formQuestionsContainer);
        }
    }

    public static class FormQuestionsContainer {
        List<JoinFormPageQuestionsWithPossibleAnswerData> questionsWithAnswers;
        List<CtcaeFormQuestion> questions;
        List<CtcaeFormQuestionAnswered> answeredQuestions;

        FormQuestionsContainer(List<JoinFormPageQuestionsWithPossibleAnswerData> answers, List<CtcaeFormQuestion> quest,
                               List<CtcaeFormQuestionAnswered> answeredQuest) {
            questionsWithAnswers = answers;
            questions = quest;
            answeredQuestions = answeredQuest;
        }
    }
}
