package com.obdasystems.pocmedici.persistence.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.obdasystems.pocmedici.persistence.dao.CtcaeFormDao;
import com.obdasystems.pocmedici.persistence.database.FormQuestionnaireDatabase;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormPage;
import com.obdasystems.pocmedici.persistence.entities.JoinFormPageQuestionsWithPossibleAnswerData;

import java.util.List;

public class CtcaePagesByFormIdRepository {

    private CtcaeFormDao dao;

    private LiveData<List<CtcaeFormPage>> pages;

    public CtcaePagesByFormIdRepository(Application app, int formId) {
        FormQuestionnaireDatabase db = FormQuestionnaireDatabase.getDatabase(app);
        dao = db.formDao();
        pages = dao.getPagesByFormIdSortedByNumber(formId);
        //Log.i("ROOM","CtcaeFormRepository "+allForms.getValue().size());
    }

    public LiveData<List<CtcaeFormPage>> getPages() {
        return pages;
    }

}
