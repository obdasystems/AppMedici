package com.obdasystems.pocmedici.persistence.repository;

import android.app.Application;
import android.util.Log;

import com.obdasystems.pocmedici.persistence.dao.CtcaeFormDao;
import com.obdasystems.pocmedici.persistence.database.FormQuestionnaireDatabase;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormFillingProcess;

import java.util.GregorianCalendar;

public class CtcaeCreateFillingProcessRepository {

    private CtcaeFormDao dao;
    private long fillingProcessId;

    public CtcaeCreateFillingProcessRepository(Application app, int formId) {
        FormQuestionnaireDatabase db = FormQuestionnaireDatabase.getDatabase(app);
        dao = db.formDao();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(System.currentTimeMillis());
        CtcaeFormFillingProcess fp = new CtcaeFormFillingProcess(formId,gc);
        fillingProcessId = dao.insertFillingProcess(fp);
        Log.i("appMedici","["+this.getClass()+"]created filling process id="+fillingProcessId);
    }


    public int getFillingProcessId() {
        return (int)fillingProcessId;
    }

}
