package com.obdasystems.pocmedici.persistence.repository;

import android.app.Application;
import android.util.Log;

import com.obdasystems.pocmedici.persistence.dao.CtcaeFormDao;
import com.obdasystems.pocmedici.persistence.database.FormQuestionnaireDatabase;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormQuestionAnswered;

import java.util.List;

public class CtcaeFillingProcessAnsweredQuestionRepository {

    private CtcaeFormDao dao;
    private int fillingProcessId;
    private int formId;
    private List<CtcaeFormQuestionAnswered> answeredQuestions;

    public CtcaeFillingProcessAnsweredQuestionRepository(Application app, int fillingProcessId, int formId) {
        FormQuestionnaireDatabase db = FormQuestionnaireDatabase.getDatabase(app);
        dao = db.formDao();
        this.fillingProcessId = fillingProcessId;
        this.formId = formId;
        Log.i("appMedici","["+this.getClass()+"]get answered questions for fillingProcessId="+fillingProcessId+" " +
                "for formId="+formId);
        answeredQuestions = dao.getAllQuestionAnsweredByProcessId(fillingProcessId);
        Log.i("appMedici","["+this.getClass()+"]found "+answeredQuestions.size()+" " +
                " answered questions");
    }

    public void insertAnsweredQuestion(int pageId, int questionId, int answerId) {
        Log.i("appMedici","["+this.getClass()+"]insert questions for fillingProcessId="+fillingProcessId+" " +
                "for formId="+formId + " pageId="+pageId+ " questionId="+questionId+ " answerId="+answerId);
        CtcaeFormQuestionAnswered answ = new CtcaeFormQuestionAnswered(fillingProcessId, formId, pageId, questionId, answerId);
        dao.insertAnsweredQuestion(answ);
    }

    public List<CtcaeFormQuestionAnswered> getAnsweredQuestions() {
        return answeredQuestions;
    }



}
