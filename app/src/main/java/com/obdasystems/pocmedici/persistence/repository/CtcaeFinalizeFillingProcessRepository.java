package com.obdasystems.pocmedici.persistence.repository;

import android.app.Application;
import android.util.Log;

import com.obdasystems.pocmedici.persistence.dao.CtcaeFormDao;
import com.obdasystems.pocmedici.persistence.database.FormQuestionnaireDatabase;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormPage;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormQuestion;

import java.util.Date;
import java.util.GregorianCalendar;

public class CtcaeFinalizeFillingProcessRepository {
    private CtcaeFormDao dao;
    private int fillingProcessId;
    private int updateReturnValue;

    public CtcaeFinalizeFillingProcessRepository(Application app, int fillingProcessId, GregorianCalendar cal) {
        FormQuestionnaireDatabase db = FormQuestionnaireDatabase.getDatabase(app);
        dao = db.formDao();
        this.fillingProcessId = fillingProcessId;
        int year = cal.get(GregorianCalendar.YEAR);
        int month = cal.get(GregorianCalendar.MONTH);
        int day = cal.get(GregorianCalendar.DAY_OF_YEAR);
        int hour = cal.get(GregorianCalendar.HOUR_OF_DAY);
        int minute = cal.get(GregorianCalendar.MINUTE);
        String dateStr = year+"/"+month+"/"+day+" "+hour+":"+minute;
        updateReturnValue = dao.updateFillingProcessEndDate(fillingProcessId, cal);
        Log.i("appMedici","["+this.getClass()+"]finalized fillingProcessId="+fillingProcessId+" " +
                "with timestamp="+dateStr+ " ("+updateReturnValue+")");
    }

    public int getUpdateReturnValue() {
        return updateReturnValue;
    }
}
