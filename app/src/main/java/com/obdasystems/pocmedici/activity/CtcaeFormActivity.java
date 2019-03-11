package com.obdasystems.pocmedici.activity;

import android.arch.lifecycle.LiveData;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.persistence.entities.CtcaeForm;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormPage;
import com.obdasystems.pocmedici.persistence.entities.JoinFormWithMaxPageNumberData;
import com.obdasystems.pocmedici.persistence.repository.CtcaePagesByFormIdRepository;

import java.util.List;

public class CtcaeFormActivity extends AppCompatActivity {

    JoinFormWithMaxPageNumberData displayedForm;
    List<CtcaeFormPage> displayedFormPages;
    CtcaePagesByFormIdRepository pagesRepository;

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

        pagesRepository = new CtcaePagesByFormIdRepository(this.getApplication(), displayedForm.getFormId());
        displayedFormPages = pagesRepository.getPages().getValue();
    }

    public void onClickFillForm(View v) {
        Intent intent = new Intent();
        intent.putExtra("submittedForm", displayedForm);
        intent.putExtra("pageCount",displayedFormPages.size());
        String pageExtraBaseName = "page_";
        for(int i=0;i<displayedFormPages.size();i++) {
            int actualPageNumber = i+1;
            String currExtraName = pageExtraBaseName+i;
            intent.putExtra(currExtraName,displayedFormPages.get(i));
        }
    }
}
