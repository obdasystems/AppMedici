package com.obdasystems.pocmedici.persistence.repository;

import android.app.Application;
import android.util.Log;

import com.obdasystems.pocmedici.persistence.dao.CtcaeFormDao;
import com.obdasystems.pocmedici.persistence.database.FormQuestionnaireDatabase;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormFillingProcess;

import java.util.GregorianCalendar;

public class CtcaeGetFinalizedFillingProcessRepository {

    private CtcaeFormDao dao;
    private int fillingProcessId;
    private CtcaeFormFillingProcess finalized;

    public CtcaeGetFinalizedFillingProcessRepository(Application app, int fillingProcessId) {
        FormQuestionnaireDatabase db = FormQuestionnaireDatabase.getDatabase(app);
        dao = db.formDao();
        this.fillingProcessId = fillingProcessId;
        finalized = dao.getFillingProcesseById(fillingProcessId);
    }

    public CtcaeFormFillingProcess getFinalized() {
        return finalized;
    }
}
