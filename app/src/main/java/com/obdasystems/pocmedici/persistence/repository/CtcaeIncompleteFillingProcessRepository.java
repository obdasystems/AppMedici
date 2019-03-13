package com.obdasystems.pocmedici.persistence.repository;

import android.app.Application;
import android.util.Log;

import com.obdasystems.pocmedici.persistence.dao.CtcaeFormDao;
import com.obdasystems.pocmedici.persistence.database.FormQuestionnaireDatabase;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormPage;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormQuestion;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormQuestionAnswered;

import java.util.LinkedList;
import java.util.List;

public class CtcaeIncompleteFillingProcessRepository {

    private CtcaeFormDao dao;
    private int fillingProcessId;
    private int formId;
    private List<CtcaeFormQuestion> incompleteQuestions;
    private List<Integer> incompletePages = new LinkedList<>();

    public CtcaeIncompleteFillingProcessRepository(Application app, int fillingProcessId, int formId) {
        FormQuestionnaireDatabase db = FormQuestionnaireDatabase.getDatabase(app);
        dao = db.formDao();
        this.fillingProcessId = fillingProcessId;
        this.formId = formId;
        Log.i("appMedici","["+this.getClass()+"]get incomplete questions for fillingProcessId="+fillingProcessId+" " +
                "for formId="+formId);
        incompleteQuestions = dao.getUnansweredQuestionsByFillingProcessAndFormIds(fillingProcessId, formId);
        for(CtcaeFormQuestion question:incompleteQuestions) {
            CtcaeFormPage incPage = dao.getPageById(question.getPageId());
            if(!incompletePages.contains(incPage.getPageNumber())) {
                incompletePages.add(incPage.getPageNumber());
            }
        }
    }

    public List<CtcaeFormQuestion> getIncompleteQuestions() {
        return incompleteQuestions;
    }

    public List<Integer> getIncompletePages() {
        return incompletePages;
    }
}
