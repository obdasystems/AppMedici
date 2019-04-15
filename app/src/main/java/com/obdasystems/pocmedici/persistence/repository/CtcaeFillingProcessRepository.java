package com.obdasystems.pocmedici.persistence.repository;

import android.content.Context;
import android.util.Log;

import com.obdasystems.pocmedici.network.RestFilledForm;
import com.obdasystems.pocmedici.network.RestFilledQuestion;
import com.obdasystems.pocmedici.persistence.dao.CtcaeFormDao;
import com.obdasystems.pocmedici.persistence.database.FormQuestionnaireDatabase;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormQuestionAnswered;

import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

public class CtcaeFillingProcessRepository {
    private CtcaeFormDao dao;
    private int updateReturnValue;

    public CtcaeFillingProcessRepository(Context ctx) {
        FormQuestionnaireDatabase db = FormQuestionnaireDatabase.getDatabase(ctx);
        dao = db.formDao();

    }

    public void deleteFillingProcess(int fillingProcessId) {
        dao.deleteFillingProcess(fillingProcessId);
    }

    public RestFilledForm finalizeFillingProcess(int fillingProcessId, int formId, GregorianCalendar cal) {
        int year = cal.get(GregorianCalendar.YEAR);
        int month = cal.get(GregorianCalendar.MONTH);
        int day = cal.get(GregorianCalendar.DAY_OF_YEAR);
        int hour = cal.get(GregorianCalendar.HOUR_OF_DAY);
        int minute = cal.get(GregorianCalendar.MINUTE);
        String dateStr = year+"/"+month+"/"+day+" "+hour+":"+minute;
        updateReturnValue = dao.updateFillingProcessEndDate(fillingProcessId, cal);
        Log.i("appMedici","["+this.getClass()+"]finalized fillingProcessId="+fillingProcessId+" " +
                "with timestamp="+dateStr+ " ("+updateReturnValue+")");

        List<CtcaeFormQuestionAnswered> answeredQuestions = dao.getAllQuestionAnsweredByProcessId(fillingProcessId);

        List<RestFilledQuestion> answers = new LinkedList<>();
        for(CtcaeFormQuestionAnswered qa:answeredQuestions) {
            RestFilledQuestion fq = new RestFilledQuestion(qa.getQuestionId(), qa.getAnswerId());
            answers.add(fq);
        }

        RestFilledForm ff = new RestFilledForm(formId,answers);
        return ff;
    }

}
