package com.obdasystems.pocmedici.activity;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.asyncresponse.FormPagesAsyncResponse;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormPage;
import com.obdasystems.pocmedici.persistence.entities.JoinFormWithMaxPageNumberData;
import com.obdasystems.pocmedici.persistence.repository.CtcaeCreateFillingProcessRepository;
import com.obdasystems.pocmedici.persistence.repository.CtcaePagesByFormIdRepository;

import java.util.List;

public class CtcaeFormActivity extends AppActivity
        implements FormPagesAsyncResponse {
    JoinFormWithMaxPageNumberData displayedForm;
    List<CtcaeFormPage> displayedFormPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctcae_form);

        Toolbar toolbar = find(R.id.form_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            Intent formListIntent = new Intent(context(), FormListActivity.class);
            startActivity(formListIntent);
        });

        Intent intent = getIntent();
        displayedForm = intent.getParcelableExtra("clickedForm");

        TextView titleView = findViewById(R.id.FormTitleText);
        titleView.setText(displayedForm.getFormTitle());

        TextView pagesView = findViewById(R.id.PagesText);
        pagesView.setText(t(R.string.form_page_count).toString() +
                displayedForm.getLastPageNumber());

        TextView instrView = findViewById(R.id.FormInstrText);
        instrView.setText(displayedForm.getFormInstructions());

        QueryAsyncTask task = new QueryAsyncTask(displayedForm.getFormId(),
                this, this.getApplication(), this);
        task.execute();
    }

    @Override
    public void onBackPressed() {
        Intent mainIntent = new Intent(this, FormListActivity.class);
        startActivity(mainIntent);
    }

    /* ***************************
     * TOOLBAR METHODS
     *****************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.form_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.form_fill_form) {
            FillingProcessAsyncTask task =
                    new FillingProcessAsyncTask(displayedForm.getFormId(),
                            this, this.getApplication(), this);
            task.execute();
            return true;
        }
        if (id == R.id.form_fill_form_image) {
            FillingProcessAsyncTask task =
                    new FillingProcessAsyncTask(displayedForm.getFormId(),
                            this, this.getApplication(), this);
            task.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickFillForm(View v) {
        FillingProcessAsyncTask task =
                new FillingProcessAsyncTask(displayedForm.getFormId(),
                        this, this.getApplication(), this);
        task.execute();
    }

    @Override
    public void taskFinished(List<CtcaeFormPage> pages) {
        displayedFormPages = pages;
    }

    @Override
    public void fillingProcessTaskFinished(int fillingProcessId) {
        //Intent intent = new Intent(this, FormPageActivity.class);
        Intent intent = new Intent(this, FormPageActivity.class);
        intent.putExtra("fillingProcessId", fillingProcessId);
        intent.putExtra("formId", displayedForm.getFormId());
        if(displayedFormPages!=null) {
            intent.putExtra("pageCount", displayedFormPages.size());
            String pageExtraBaseName = "page_";
            for (int i = 0; i < displayedFormPages.size(); i++) {
                int actualPageNumber = i + 1;
                String currExtraName = pageExtraBaseName + actualPageNumber;
                CtcaeFormPage currExtraPage = displayedFormPages.get(i);
                intent.putExtra(currExtraName, currExtraPage);
            }
            startActivity(intent);
        }
    }

    private static class FillingProcessAsyncTask extends AsyncTask<Void, Void, Integer> {
        private Context ctx;
        private ProgressDialog progDial;
        private int formId;
        private CtcaeCreateFillingProcessRepository fpRepository;
        private Application app;
        private FormPagesAsyncResponse delegate;

        FillingProcessAsyncTask(int formId,
                                Context context,
                                Application app,
                                FormPagesAsyncResponse delegate) {
            ctx = context;
            this.formId = formId;
            progDial = new ProgressDialog(ctx);
            this.app = app;
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDial.setMessage("Creating filling process...");
            progDial.setIndeterminate(false);
            progDial.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDial.setCancelable(false);
            progDial.show();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            fpRepository = new CtcaeCreateFillingProcessRepository(app, formId);
            return fpRepository.getFillingProcessId();
        }

        @Override
        protected void onPostExecute(Integer fillingProcessId) {
            super.onPostExecute(fillingProcessId);
            progDial.dismiss();
            delegate.fillingProcessTaskFinished(fillingProcessId);
        }
    }

    private static class QueryAsyncTask extends AsyncTask<Void, Void, List<CtcaeFormPage>> {
        private Context ctx;
        private ProgressDialog progDial;
        private int formId;
        private CtcaePagesByFormIdRepository pagesRepository;
        private Application app;
        private FormPagesAsyncResponse delegate;

        QueryAsyncTask(int formId,
                       Context context,
                       Application app,
                       FormPagesAsyncResponse delegate) {
            ctx = context;
            this.formId = formId;
            progDial = new ProgressDialog(ctx);
            this.app = app;
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDial.setMessage("Retrieving form pages...");
            progDial.setIndeterminate(false);
            progDial.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDial.setCancelable(false);
            progDial.show();
        }

        @Override
        protected List<CtcaeFormPage> doInBackground(Void... voids) {
            pagesRepository = new CtcaePagesByFormIdRepository(app, formId);
            return pagesRepository.getPages();
        }

        @Override
        protected void onPostExecute(List<CtcaeFormPage> ctcaeFormPages) {
            super.onPostExecute(ctcaeFormPages);
            progDial.dismiss();
            delegate.taskFinished(ctcaeFormPages);
        }
    }

}
