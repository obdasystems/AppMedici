package com.obdasystems.pocmedici.activity;

import android.app.Application;
import android.app.ProgressDialog;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.asyncresponse.FormPagesAsyncResponse;
import com.obdasystems.pocmedici.persistence.entities.CtcaeForm;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormPage;
import com.obdasystems.pocmedici.persistence.entities.JoinFormWithMaxPageNumberData;
import com.obdasystems.pocmedici.persistence.repository.CtcaePagesByFormIdRepository;

import java.util.List;

public class CtcaeFormActivity extends AppCompatActivity implements FormPagesAsyncResponse {

    JoinFormWithMaxPageNumberData displayedForm;
    List<CtcaeFormPage> displayedFormPages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctcae_form);

        Intent intent = getIntent();
        displayedForm = intent.getParcelableExtra("clickedForm");

        TextView titleView = findViewById(R.id.FormTitleText);
        titleView.setText(displayedForm.getFormTitle());

        TextView pagesView = findViewById(R.id.PagesText);
        pagesView.setText("Nr. of pages: "+displayedForm.getLastPageNumber());

        TextView instrView = findViewById(R.id.FormInstrText);
        instrView.setText(displayedForm.getFormInstructions());

        QueryAsyncTask task = new QueryAsyncTask(displayedForm.getFormId(), this, this.getApplication(), this);
        task.execute();
    }

    public void onClickFillForm(View v) {
        Intent intent = new Intent(v.getContext(), FormPageActivity.class);
        intent.putExtra("submittedForm", displayedForm);
        if(displayedFormPages!=null) {
            intent.putExtra("pageCount", displayedFormPages.size());
            String pageExtraBaseName = "page_";
            for (int i = 0; i < displayedFormPages.size(); i++) {
                int actualPageNumber = i + 1;
                String currExtraName = pageExtraBaseName + actualPageNumber;
                CtcaeFormPage currExtraPage = displayedFormPages.get(i);
                intent.putExtra(currExtraName, currExtraPage);
            }
            Log.i("formActivity","son qui");
            startActivity(intent);
        }
    }

    @Override
    public void taskFinished(List<CtcaeFormPage> pages) {
        displayedFormPages = pages;
    }

    private static class QueryAsyncTask extends AsyncTask<Void, Void, List<CtcaeFormPage>> {

        private Context ctx;
        private ProgressDialog progDial;
        private int formId;
        private CtcaePagesByFormIdRepository pagesRepository;
        private Application app;
        private FormPagesAsyncResponse delegate;

        QueryAsyncTask(int formId, Context context, Application app, FormPagesAsyncResponse delegate) {
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
