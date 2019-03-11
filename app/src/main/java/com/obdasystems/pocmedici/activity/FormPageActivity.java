package com.obdasystems.pocmedici.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.adapter.FormQuestionListAdapter;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormPage;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormQuestion;
import com.obdasystems.pocmedici.persistence.entities.JoinFormPageQuestionsWithPossibleAnswerData;
import com.obdasystems.pocmedici.persistence.viewmodel.CtcaeFormPageViewModel;
import com.obdasystems.pocmedici.persistence.viewmodel.factory.FormPageViewModelFactory;

import java.util.LinkedList;
import java.util.List;

public class FormPageActivity extends AppCompatActivity {

    private int currentPage = 1;
    private int totalPagecount = 0;
    private List<CtcaeFormPage> formPages;
    private CtcaeFormPageViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ctcae_formpage);
        formPages = new LinkedList<>();

        Intent intent = getIntent();
        totalPagecount = intent.getIntExtra("pageCount", 0);
        for(int i=1;i<=totalPagecount;i++) {
            String extraName = "page_"+i;
            CtcaeFormPage page = intent.getParcelableExtra(extraName);
            formPages.add(page);
        }


        RecyclerView recyclerView = findViewById(R.id.formPageRecyclerView);
        final FormQuestionListAdapter adapter = new FormQuestionListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(totalPagecount>0) {
            currentPage = 1;
            viewModel = ViewModelProviders.of(this,new FormPageViewModelFactory(this.getApplication(),getCurrentPageId())).get(CtcaeFormPageViewModel.class);
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
            });
        }
        else {
            //view model vuoto
        }
    }

    private int getCurrentPageId() {
        return formPages.get(currentPage-1).getId();
    }

}
