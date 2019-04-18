package com.obdasystems.pocmedici.persistence.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.obdasystems.pocmedici.persistence.entities.CtcaeForm;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormFillingProcess;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormPage;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormQuestion;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormQuestionAnswered;
import com.obdasystems.pocmedici.persistence.entities.CtcaePossibleAnswer;
import com.obdasystems.pocmedici.persistence.entities.JoinFormPageQuestionsWithPossibleAnswerData;
import com.obdasystems.pocmedici.persistence.entities.JoinFormToPossibleAnswerData;
import com.obdasystems.pocmedici.persistence.entities.JoinFormWithMaxPageNumberData;
import com.obdasystems.pocmedici.persistence.entities.Position;
import com.obdasystems.pocmedici.persistence.entities.StepCounter;

import java.util.GregorianCalendar;
import java.util.List;

@Dao
public interface CtcaeFormDao {

    /*
     *      SENSORS
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertStepCounter(StepCounter counter);

    @Query("SELECT * FROM step_counter ORDER BY year,month,day DESC")
    LiveData<List<StepCounter>> getAllStepCounters();

    @Query("SELECT * FROM step_counter WHERE NOT(year=:year AND month=:month AND day=:day) AND sent_to_server=0 ")
    List<StepCounter> getNotSentStepCountersMinusCurrent(int year, int month, int day);

    @Query("SELECT * FROM step_counter WHERE year=:year AND month=:month AND day=:day")
    StepCounter getStepCounter(int year, int month, int day);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPosition(Position position);

    @Query("SELECT * FROM device_position")
    LiveData<List<Position>> getAllPositions();


    /*
     *      STRUCTURAL FORMS (INTENSIONS)
     */


    //FORMS
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertForm(CtcaeForm form);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMultipleForm(List<CtcaeForm> forms);

    @Delete
    void deleteForm(CtcaeForm form);

    @Query("DELETE FROM ctcae_form_questionnaire")
    void deleteAllForms();

    @Query("SELECT * FROM ctcae_form_questionnaire")
    LiveData<List<CtcaeForm>> getAllForms();

    @Query("SELECT * FROM ctcae_form_questionnaire WHERE id=:formId")
    LiveData<List<CtcaeForm>> getFormsById(int formId);

    @Query("SELECT * FROM ctcae_form_questionnaire WHERE class=:formClass")
    LiveData<List<CtcaeForm>> getFormsByClass(int formClass);

    @Query("SELECT * FROM ctcae_form_questionnaire WHERE periodicity=:formPeriodicity")
    LiveData<List<CtcaeForm>> getFormsByPeriodicity(int formPeriodicity);

    @Query("SELECT * FROM ctcae_form_questionnaire WHERE class=:formClass AND periodicity=:formPeriodicity")
    LiveData<List<CtcaeForm>> getFormByClassAndPeriodicity(int formClass, int formPeriodicity);

    //PAGE FORMS
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertFormPage(CtcaeFormPage page);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMultiplePages(List<CtcaeFormPage> pages);

    @Delete
    void deleteFormPage(CtcaeFormPage formPage);

    @Query("SELECT * FROM ctcae_form_page WHERE form_id=:formId")
    LiveData<List<CtcaeFormPage>> getPagesByFormId(int formId);

    /*@Query("SELECT * FROM ctcae_form_page WHERE form_id=:formId ORDER BY page_number ASC")
    LiveData<List<CtcaeFormPage>> getPagesByFormIdSortedByNumber(int formId);*/

    @Query("SELECT * FROM ctcae_form_page WHERE form_id=:formId ORDER BY page_number ASC")
    List<CtcaeFormPage> getPagesByFormIdSortedByNumber(int formId);

    @Query("SELECT * FROM ctcae_form_page WHERE id=:pageId")
    CtcaeFormPage getPageById(int pageId);

    @Query("SELECT * FROM ctcae_form_page WHERE form_id=:formId AND page_number=:formPage")
    CtcaeFormPage getPageByFormIdAndPageNumber(int formId, int formPage);

    //QUESTIONS
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertFormQuestion(CtcaeFormQuestion question);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMultipleQuestions(List<CtcaeFormQuestion> questions);

    @Delete
    void deleteQuestion(CtcaeFormQuestion question);

    @Query("SELECT * FROM ctcae_form_question")
    LiveData<List<CtcaeFormQuestion>> getAllQuestions();

    @Query("SELECT * FROM ctcae_form_question WHERE page_id IN (SELECT id FROM ctcae_form_page WHERE form_id=:formId)")
    LiveData<List<CtcaeFormQuestion>> getQuestionsByFormId(int formId);

    @Query("SELECT * FROM ctcae_form_question WHERE page_id=:pageId")
    List<CtcaeFormQuestion> getQuestionsByPageId(int pageId);

    @Query("SELECT * FROM ctcae_form_question WHERE page_id IN (SELECT id FROM ctcae_form_page WHERE form_id=:formId AND page_number=:pageNumber)")
    LiveData<List<CtcaeFormQuestion>> getQuestionsByFormIdAndPageNumber(int formId, int pageNumber);

    @Query("SELECT * FROM ctcae_form_question WHERE id=:questionId")
    CtcaeFormQuestion getQuestionById(int questionId);

    //POSSIBLE ANSWERS
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertPossibleAnswer(CtcaePossibleAnswer answer);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMultiplePossibleAnswers(List<CtcaePossibleAnswer> answers);

    @Delete
    void deletePossibleAnswer(CtcaePossibleAnswer answer);

    @Query("SELECT * FROM ctcae_form_possible_answer")
    LiveData<List<CtcaePossibleAnswer>> getAllPossibleAnswers();

    @Query("SELECT * FROM ctcae_form_possible_answer WHERE question_id=:questionId")
    LiveData<List<CtcaePossibleAnswer>> getPossibleAnswersByQuestionId(int questionId);

    @Query("SELECT * FROM ctcae_form_possible_answer WHERE question_id IN (SELECT id FROM ctcae_form_question WHERE page_id=:pageId)")
    LiveData<List<CtcaePossibleAnswer>> getPossibleAnswersByPageId(int pageId);

    @Query("SELECT * FROM ctcae_form_possible_answer  WHERE question_id IN " +
            "(SELECT id FROM ctcae_form_question WHERE page_id IN " +
            "(SELECT id FROM ctcae_form_page WHERE form_id=:formId))")
    LiveData<List<CtcaePossibleAnswer>> getPossibleAnswersByFormId(int formId);

    @Query("SELECT * FROM ctcae_form_possible_answer ans WHERE question_id IN " +
            "(SELECT id FROM ctcae_form_question WHERE page_id IN " +
            "(SELECT id FROM ctcae_form_page WHERE form_id=:formId AND page_number=:pageNumber))")
    LiveData<List<CtcaePossibleAnswer>> getPossibleAnswersByFormIdAndPageNumber(int formId, int pageNumber);

    @Query(("SELECT * FROM ctcae_form_possible_answer WHERE id=:answerId"))
    CtcaePossibleAnswer getPossibleAnswerById(int answerId);

    //JOIN FROM FORMS TO POSSIBLE ANSWERS
    @Query("SELECT form.id AS formId, page.id AS pageId, page.page_number AS pageNumber , quest.id AS questionId, quest.text as questionText," +
            "ans.id AS answerId, ans.text AS answerText " +
            "FROM ctcae_form_questionnaire form, ctcae_form_page page, ctcae_form_question quest, ctcae_form_possible_answer ans " +
            "WHERE form.id=page.form_id AND page.id=quest.page_id AND quest.id=ans.question_id")
    List<JoinFormToPossibleAnswerData> getAllFormsWithPagesQuestionsAndPossibleAnswers();

    @Query("SELECT form.id AS formId, page.id AS pageId, page.page_number AS pageNumber , quest.id AS questionId, quest.text as questionText," +
            "ans.id AS answerId, ans.text AS answerText " +
            "FROM ctcae_form_questionnaire form, ctcae_form_page page, ctcae_form_question quest, ctcae_form_possible_answer ans " +
            "WHERE form.id=:formId AND form.id=page.form_id AND page.id=quest.page_id AND quest.id=ans.question_id")
    List<JoinFormToPossibleAnswerData> getFormWithPagesQuestionsAndPossibleAnswersByFormId(int formId);

    @Query("SELECT form.id AS formId, page.id AS pageId, page.page_number AS pageNumber , quest.id AS questionId, quest.text as questionText," +
            "ans.id AS answerId, ans.text AS answerText " +
            "FROM ctcae_form_questionnaire form, ctcae_form_page page, ctcae_form_question quest, ctcae_form_possible_answer ans " +
            "WHERE form.id=:formId AND page.page_number=:pageNumber AND form.id=page.form_id AND page.id=quest.page_id AND quest.id=ans.question_id")
    List<JoinFormToPossibleAnswerData> getFormWithPageQuestionsAndPossibleAnswersByFormIdAndPageNumber(int formId, int pageNumber);

    /*
     *     FORM FILLING PROCESSES (EXTENSIONS)
     */

    //FILLING PROCESSES
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertFillingProcess(CtcaeFormFillingProcess fillProc);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    List<Long> insertMultipleFillingProcess(List<CtcaeFormFillingProcess> fillProcesses);

    @Delete
    void deleteFillingProcess(CtcaeFormFillingProcess fillProc);

    @Query("DELETE FROM ctcae_form_filling_process WHERE id=:fillProcId")
    void deleteFillingProcess(int fillProcId);

    /*@Query("UPDATE ctcae_form_filling_process SET end_date=")
    int updateFillingProcess(int fillProcId, GregorianCalendar endDate);*/

    @Query("UPDATE ctcae_form_filling_process SET end_date=:endDate WHERE id=:fillProcId")
    int updateFillingProcessEndDate(int fillProcId, GregorianCalendar endDate);

    @Query("UPDATE ctcae_form_filling_process SET sent_to_server=:sent WHERE id=:fillProcId")
    int updateFillingProcessSentToServer(int fillProcId, int sent);

    @Query("SELECT * from ctcae_form_filling_process where id=:fpId")
    CtcaeFormFillingProcess getFillingProcesseById(int fpId);

    @Query("SELECT * from ctcae_form_filling_process")
    LiveData<List<CtcaeFormFillingProcess>> getAllFillingProcesses();

    @Query("SELECT * from ctcae_form_filling_process ORDER BY start_date ASC")
    LiveData<List<CtcaeFormFillingProcess>> getAllFillingProcessesOrderedByStartDateAsc();

    @Query("SELECT * from ctcae_form_filling_process ORDER BY start_date DESC")
    LiveData<List<CtcaeFormFillingProcess>> getAllFillingProcessesOrderedByStartDateDesc();

    @Query("SELECT * from ctcae_form_filling_process ORDER BY end_date ASC")
    LiveData<List<CtcaeFormFillingProcess>> getAllFillingProcessesOrderedByEndDateAsc();

    @Query("SELECT * from ctcae_form_filling_process ORDER BY end_date DESC")
    LiveData<List<CtcaeFormFillingProcess>> getAllFillingProcessesOrderedByEndDateDesc();

    @Query("SELECT * from ctcae_form_filling_process WHERE end_date IS NULL")
    LiveData<List<CtcaeFormFillingProcess>> getAllIncompleteFillingProcesses();

    @Query("SELECT * from ctcae_form_filling_process WHERE end_date IS NULL ORDER BY start_date ASC")
    LiveData<List<CtcaeFormFillingProcess>> getAllIncompleteFillingProcessesOrderedByStartDateAsc();

    @Query("SELECT * from ctcae_form_filling_process WHERE end_date IS NULL ORDER BY start_date DESC")
    LiveData<List<CtcaeFormFillingProcess>> getAllIncompleteFillingProcessesOrderedByStartDateDesc();

    @Query("SELECT * from ctcae_form_filling_process WHERE end_date IS NULL ORDER BY end_date ASC")
    LiveData<List<CtcaeFormFillingProcess>> getAllIncompleteFillingProcessesOrderedByEndDateAsc();

    @Query("SELECT * from ctcae_form_filling_process WHERE end_date IS NULL ORDER BY end_date DESC")
    LiveData<List<CtcaeFormFillingProcess>> getAllIncompleteFillingProcessesOrderedByEndDateDesc();

    @Query("SELECT * from ctcae_form_filling_process WHERE end_date IS NOT NULL")
    LiveData<List<CtcaeFormFillingProcess>> getAllCompletedFillingProcesses();

    @Query("SELECT * from ctcae_form_filling_process WHERE end_date IS NOT NULL ORDER BY start_date ASC")
    LiveData<List<CtcaeFormFillingProcess>> getAllCompletedFillingProcessesOrderedByStartDateAsc();

    @Query("SELECT * from ctcae_form_filling_process WHERE end_date IS NOT NULL ORDER BY start_date DESC")
    LiveData<List<CtcaeFormFillingProcess>> getAllCompletedFillingProcessesOrderedByStartDateDesc();

    @Query("SELECT * from ctcae_form_filling_process WHERE end_date IS NOT NULL ORDER BY end_date ASC")
    LiveData<List<CtcaeFormFillingProcess>> getAllCompletedFillingProcessesOrderedByEndDateAsc();

    @Query("SELECT * from ctcae_form_filling_process WHERE end_date IS NOT NULL ORDER BY end_date DESC")
    LiveData<List<CtcaeFormFillingProcess>> getAllCompletedFillingProcessesOrderedByEndDateDesc();

    @Query("SELECT * from ctcae_form_filling_process WHERE end_date IS NULL AND form_id=:formId")
    LiveData<List<CtcaeFormFillingProcess>> getIncompleteFillingProcessesByFormId(int formId);

    @Query("SELECT * from ctcae_form_filling_process WHERE end_date IS NULL AND form_id=:formId")
    LiveData<List<CtcaeFormFillingProcess>> getAllCompletedFillingProcessesByFormId(int formId);

    @Query("SELECT * FROM ctcae_form_filling_process WHERE sent_to_server>0")
    LiveData<List<CtcaeFormFillingProcess>> getAllCompletedFillingProcessesAlreadySentToServer();

    @Query("SELECT * FROM ctcae_form_filling_process WHERE sent_to_server=0")
    LiveData<List<CtcaeFormFillingProcess>> getAllCompletedFillingProcessesNotYetSentToServer();

    //ANSWERED QUESTIONS
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAnsweredQuestion(CtcaeFormQuestionAnswered answered);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMultipleAnsweredQuestion(List<CtcaeFormQuestionAnswered> answeredList);

    @Delete
    void deleteAnsweredQuestion(CtcaeFormQuestionAnswered answered);

    @Query("SELECT * FROM ctcae_form_question_answered")
    LiveData<List<CtcaeFormQuestionAnswered>> getAllQuestionAnswered();

    @Query("SELECT * FROM ctcae_form_question_answered WHERE proc_id=:procId")
    List<CtcaeFormQuestionAnswered> getAllQuestionAnsweredByProcessId(int procId);



    @Query("SELECT * FROM ctcae_form_question_answered WHERE form_id=:formId")
    LiveData<List<CtcaeFormQuestionAnswered>> getAllQuestionAnsweredByFormId(int formId);

    @Query("SELECT * FROM ctcae_form_question_answered WHERE proc_id=:pageId")
    LiveData<List<CtcaeFormQuestionAnswered>> getAllQuestionAnsweredByPageId(int pageId);

    /*
     *     SPARE QUERIES
     */
    //last page of a particular form
    @Query("SELECT MAX(page.page_number) FROM ctcae_form_page page  WHERE page.form_id=:formId")
    int getLastPageNumberInForm(int formId);

    //forms with index of their last page
    @Query("SELECT form.id as formId, form.class as formClass, form.periodicity as formPeriodicity, " +
                "form.title as formTitle, form.instructions as formInstructions, MAX(page.page_number) as lastPageNumber " +
            "FROM ctcae_form_questionnaire form, ctcae_form_page page  " +
            "WHERE form.id=page.form_id " +
            "GROUP BY form.id")
    LiveData<List<JoinFormWithMaxPageNumberData>> getFormWithPagesCount();

    //questions and possible answers for a given form page
    @Query("SELECT quest.id as questionId, quest.form_id as formId, quest.page_id as pageId, " +
            "quest.text as questionText, answ.id as possibleAnswerId, answ.text as possibleAnswerText, " +
            " answ.code as possibleAnswerCode " +
            "FROM ctcae_form_question quest, ctcae_form_possible_answer answ  " +
            "WHERE quest.page_id=:pageId AND quest.id=answ.question_id " +
            "ORDER BY quest.page_id, answ.id")
    List<JoinFormPageQuestionsWithPossibleAnswerData> getQuestionsAndPossAnswersByPageId(int pageId);

    //Questions to be answered to complete a filling process by ID
    @Query("SELECT * FROM ctcae_form_question " +
            "WHERE form_id=:formId " +
            "AND id NOT IN (SELECT question_id FROM ctcae_form_question_answered WHERE proc_id=:procId)")
    List<CtcaeFormQuestion> getUnansweredQuestionsByFillingProcessAndFormIds(int procId, int formId);

    @Query("SELECT * FROM ctcae_form_question " +
            "WHERE form_id IN (SELECT form_id FROM ctcae_form_filling_process where id=:procId)" +
            "AND id NOT IN (SELECT question_id FROM ctcae_form_question_answered WHERE proc_id=:procId)")
    List<CtcaeFormQuestion> getUnansweredQuestionsByFillingProcessId(int procId);


}



