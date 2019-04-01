package com.obdasystems.pocmedici.persistence.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.obdasystems.pocmedici.persistence.dao.CtcaeFormDao;
import com.obdasystems.pocmedici.persistence.database.FormQuestionnaireDatabase;
import com.obdasystems.pocmedici.persistence.entities.Position;
import com.obdasystems.pocmedici.persistence.entities.StepCounter;

import java.util.GregorianCalendar;
import java.util.List;

public class PositionRepository {

    private CtcaeFormDao dao;

    public PositionRepository(Context app) {
        FormQuestionnaireDatabase db = FormQuestionnaireDatabase.getDatabase(app);
        dao = db.formDao();
    }

    public LiveData<List<Position>> getAllPositions() {
        return dao.getAllPositions();
    }

    public void insertPosition(GregorianCalendar cal, double latitude, double longitude) {
        Position pos = new Position(cal, latitude, longitude);
        dao.insertPosition(pos);
    }

}
