package com.obdasystems.pocmedici.persistence.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.obdasystems.pocmedici.persistence.dao.CtcaeFormDao;
import com.obdasystems.pocmedici.persistence.database.FormQuestionnaireDatabase;
import com.obdasystems.pocmedici.persistence.entities.CtcaeForm;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormPage;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormQuestion;
import com.obdasystems.pocmedici.persistence.entities.CtcaePossibleAnswer;
import com.obdasystems.pocmedici.persistence.entities.JoinFormWithMaxPageNumberData;

import java.util.List;

public class CtcaeFormRepository {

    private CtcaeFormDao dao;

    public CtcaeFormRepository(Context ctx) {
        FormQuestionnaireDatabase db = FormQuestionnaireDatabase.getDatabase(ctx);
        dao = db.formDao();
    }

    public LiveData<List<JoinFormWithMaxPageNumberData>> getAllForms() {
        return dao.getFormWithPagesCount();
    }

    public void insertForm(CtcaeForm form) {
        dao.insertForm(form);
    }

    public void insertFormPage(CtcaeFormPage formPage) {
        dao.insertFormPage(formPage);
    }

    public void insertFormQuestion(CtcaeFormQuestion quest) {
        dao.insertFormQuestion(quest);
    }

    public void insertFormAnswer(CtcaePossibleAnswer answ) {
        dao.insertPossibleAnswer(answ);
    }

}
