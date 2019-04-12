package com.obdasystems.pocmedici.persistence.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.obdasystems.pocmedici.persistence.dao.CtcaeFormDao;
import com.obdasystems.pocmedici.persistence.database.FormQuestionnaireDatabase;
import com.obdasystems.pocmedici.persistence.entities.StepCounter;

import java.util.GregorianCalendar;
import java.util.List;

public class StepCounterRepository {

    private CtcaeFormDao dao;

    public StepCounterRepository(Context app) {
        FormQuestionnaireDatabase db = FormQuestionnaireDatabase.getDatabase(app);
        dao = db.formDao();
    }

    public LiveData<List<StepCounter>> getAllStepCounters() {
        return dao.getAllStepCounters();
    }

    public void addStep(float steps, int year, int month, int day) {
        StepCounter sp = dao.getStepCounter(year, month, day);
        if(sp!=null) {
            int previous = sp.getStepCount();
            int current = previous+ (int)steps;
            sp.setStepCount(current);

        }
        else {
            sp = new StepCounter((int)steps,year,month,day, 0);
        }
        dao.insertStepCounter(sp);
    }

    public StepCounter getStepCounter(int year, int month, int day) {
        StepCounter sp = dao.getStepCounter(year, month, day);
        return sp;
    }

    public List<StepCounter> getNotSentStepCountersMinusCurrent(int year, int month, int day) {
        return dao.getNotSentStepCountersMinusCurrent(year,month,day);
    }

    public void finalizeStepCounter(StepCounter sp) {
        sp.setSentToServer(1);
        dao.insertStepCounter(sp);
    }

}
