package com.obdasystems.pocmedici.persistence.viewmodel;

import android.app.Application;

import com.obdasystems.pocmedici.persistence.entities.CtcaeFormQuestion;
import com.obdasystems.pocmedici.persistence.entities.JoinFormPageQuestionsWithPossibleAnswerData;
import com.obdasystems.pocmedici.persistence.repository.CtcaeFormQuestionsRepository;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;

public class CtcaeFormPageViewModel extends AndroidViewModel {

    private CtcaeFormQuestionsRepository repository;
    private List<CtcaeFormQuestion> pageQuestions;
    private List<JoinFormPageQuestionsWithPossibleAnswerData> pageQuestionsWithAnswers;

    public CtcaeFormPageViewModel(Application app, int pageId) {
        super(app);
        repository = new CtcaeFormQuestionsRepository(app,pageId);
        pageQuestions = repository.getAllQuestions();
        pageQuestionsWithAnswers = repository.getAllQuestionsWithAnswers();
    }

    public List<CtcaeFormQuestion> getPageQuestions() {
        return pageQuestions;
    }

    public List<JoinFormPageQuestionsWithPossibleAnswerData> getPageQuestionsWithAnswers() {
        return pageQuestionsWithAnswers;
    }

    /*public void insertForm(CtcaeFormPage page) {
        repository.insertFormPage(page);
    }*/

}
