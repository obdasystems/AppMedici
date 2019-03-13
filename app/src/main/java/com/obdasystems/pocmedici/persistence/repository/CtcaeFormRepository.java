package com.obdasystems.pocmedici.persistence.repository;

import android.app.Application;

import com.obdasystems.pocmedici.persistence.dao.CtcaeFormDao;
import com.obdasystems.pocmedici.persistence.database.FormQuestionnaireDatabase;
import com.obdasystems.pocmedici.persistence.entities.CtcaeForm;
import com.obdasystems.pocmedici.persistence.entities.JoinFormWithMaxPageNumberData;

import java.util.List;

import androidx.lifecycle.LiveData;

public class CtcaeFormRepository {

    private CtcaeFormDao dao;
    private LiveData<List<JoinFormWithMaxPageNumberData>> allForms;

    public CtcaeFormRepository(Application app) {
        FormQuestionnaireDatabase db = FormQuestionnaireDatabase.getDatabase(app);
        dao = db.formDao();
        allForms = dao.getFormWithPagesCount();
        //Log.i("ROOM","CtcaeFormRepository "+allForms.getValue().size());
    }

    public LiveData<List<JoinFormWithMaxPageNumberData>> getAllForms() {
        return allForms;
    }



    /*public void insertForm(CtcaeForm form) {
        new insertFormAsyncTask(dao).execute(form);
    }

    private static class insertFormAsyncTask extends AsyncTask<CtcaeForm, Void, Void> {

        private CtcaeFormDao mAsyncTaskDao;

        insertFormAsyncTask(CtcaeFormDao dao) {
            mAsyncTaskDao = dao;
        }

        protected Void doInBackground(final CtcaeForm... params) {
            mAsyncTaskDao.insertForm(params[0]);
            return null;
        }
    }*/
}
