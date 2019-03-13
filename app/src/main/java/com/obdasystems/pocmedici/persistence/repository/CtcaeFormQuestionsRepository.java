package com.obdasystems.pocmedici.persistence.repository;

import android.app.Application;

import com.obdasystems.pocmedici.persistence.dao.CtcaeFormDao;
import com.obdasystems.pocmedici.persistence.database.FormQuestionnaireDatabase;
import com.obdasystems.pocmedici.persistence.entities.CtcaeForm;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormPage;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormQuestion;
import com.obdasystems.pocmedici.persistence.entities.JoinFormPageQuestionsWithPossibleAnswerData;

import java.util.List;

public class CtcaeFormQuestionsRepository {

    private CtcaeFormDao dao;
    private List<JoinFormPageQuestionsWithPossibleAnswerData> allQuestionsWithAnswers;
    private List<CtcaeFormQuestion> allQuestions;

    public CtcaeFormQuestionsRepository(Application app, int pageId) {
        FormQuestionnaireDatabase db = FormQuestionnaireDatabase.getDatabase(app);
        dao = db.formDao();
        allQuestionsWithAnswers = dao.getQuestionsAndPossAnswersByPageId(pageId);
        allQuestions = dao.getQuestionsByPageId(pageId);
        //Log.i("ROOM","CtcaeFormRepository "+allForms.getValue().size());
    }

    public List<CtcaeFormQuestion> getAllQuestions() {
        return allQuestions;
    }

    public List<JoinFormPageQuestionsWithPossibleAnswerData> getAllQuestionsWithAnswers() {
        return allQuestionsWithAnswers;
    }

    /*public void insertFormPage(CtcaeFormPage formPage) {
        new CtcaeFormPageRepository.insertFormPageAsyncTask(dao).execute(formPage);
    }

    private static class insertFormPageAsyncTask extends AsyncTask<CtcaeFormPage, Void, Void> {

        private CtcaeFormDao mAsyncTaskDao;

        insertFormPageAsyncTask(CtcaeFormDao dao) {
            mAsyncTaskDao = dao;
        }

        protected Void doInBackground(final CtcaeFormPage... params) {
            mAsyncTaskDao.insertFormPage(params[0]);
            return null;
        }
    }*/
}
