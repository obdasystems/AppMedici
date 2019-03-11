package com.obdasystems.pocmedici.persistence.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.obdasystems.pocmedici.persistence.dao.CtcaeFormDao;
import com.obdasystems.pocmedici.persistence.entities.CtcaeForm;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormFillingProcess;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormPage;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormQuestion;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormQuestionAnswered;
import com.obdasystems.pocmedici.persistence.entities.CtcaePossibleAnswer;

@Database(entities = {CtcaeForm.class, CtcaeFormFillingProcess.class, CtcaeFormPage.class, CtcaeFormQuestion.class,
                      CtcaeFormQuestionAnswered.class, CtcaePossibleAnswer.class},
          version = 1)
public abstract class FormQuestionnaireDatabase extends RoomDatabase {

    public abstract CtcaeFormDao formDao();

    private static volatile FormQuestionnaireDatabase INSTANCE;

    private static RoomDatabase.Callback callBack = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            new PopulateDbAsync(INSTANCE).execute();
        }
    };

    public static FormQuestionnaireDatabase getDatabase(final Context context) {
        if(INSTANCE == null){
            synchronized (FormQuestionnaireDatabase.class) {
                if(INSTANCE == null) {
                    /*INSTANCE = Room.databaseBuilder(context.getApplicationContext(),FormQuestionnaireDatabase.class,
                                                    "form_questionnaire_database_2").addCallback(callBack).build();*/
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),FormQuestionnaireDatabase.class,
                            "form_questionnaire_database_2").addMigrations(MIGRATION_1_1).build();
                }
            }

        }
        return INSTANCE;
    }


    static final Migration MIGRATION_1_1 = new Migration(1, 1) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };


    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final CtcaeFormDao mDao;

        PopulateDbAsync(FormQuestionnaireDatabase db) {
            mDao = db.formDao();
        }

        protected Void doInBackground(final Void... params) {
            //mDao.deleteAllForms();
            /*CtcaeForm form1 = new CtcaeForm(1,2,3);
            mDao.insertForm(form1);

            Log.i("ROOM", "insert 1");
            System.out.println("insert 1");

            CtcaeForm form2 = new CtcaeForm(2,1,4);
            mDao.insertForm(form2);

            CtcaeForm form3 = new CtcaeForm(3,4,2);
            mDao.insertForm(form3);

            CtcaeForm form4 = new CtcaeForm(1,4,2);
            mDao.insertForm(form4);*/

            String title1 = "QUESTIONARIO SULLO STATO DI SALUTE SF 36";
            String instr1 = "Questo questionario intende valutare cosa Lei pensa della Sua salute. Le informazioni raccolte " +
                    "permetteranno di essere sempre aggiornati su come si sente e su come riesce a svolgere le Sue attività consuete.\n" +
                    "Risponda a ciascuna domanda del questionario indicando la Sua risposta come mostrato di volta in volta. " +
                    "Se non si sente certo della risposta, effettui la scelata che comunque Le sembra migliore.";
            CtcaeForm form1 = new CtcaeForm(1,1,1, title1, instr1);
            mDao.insertForm(form1);

            //page 1
            CtcaeFormPage form1Page1 = new CtcaeFormPage(1,form1.getId(),1, "Condizioni generali di salute", "Selezioni una risposta per ognuna delle seguenti domande");
            mDao.insertFormPage(form1Page1);
            CtcaeFormQuestion form1Page1Question1 = new CtcaeFormQuestion(1,form1.getId(),form1Page1.getId(),"In generale direbbe che la sua salute è:");
            mDao.insertFormQuestion(form1Page1Question1);
            CtcaePossibleAnswer form1Page1Question1Poss1 = new CtcaePossibleAnswer(1111,form1.getId(),form1Page1.getId(),form1Page1Question1.getId(),"Eccellente");
            mDao.insertPossibleAnswer(form1Page1Question1Poss1);
            CtcaePossibleAnswer form1Page1Question1Poss2 = new CtcaePossibleAnswer(1112,form1.getId(),form1Page1.getId(),form1Page1Question1.getId(),"Molto Buona");
            mDao.insertPossibleAnswer(form1Page1Question1Poss2);
            CtcaePossibleAnswer form1Page1Question1Poss3 = new CtcaePossibleAnswer(1113,form1.getId(),form1Page1.getId(),form1Page1Question1.getId(),"Buona");
            mDao.insertPossibleAnswer(form1Page1Question1Poss3);
            CtcaePossibleAnswer form1Page1Question1Poss4 = new CtcaePossibleAnswer(1114,form1.getId(),form1Page1.getId(),form1Page1Question1.getId(),"Passabile");
            mDao.insertPossibleAnswer(form1Page1Question1Poss4);
            CtcaePossibleAnswer form1Page1Question1Poss5 = new CtcaePossibleAnswer(1115,form1.getId(),form1Page1.getId(),form1Page1Question1.getId(),"Scadente");
            mDao.insertPossibleAnswer(form1Page1Question1Poss5);

            //page 2
            CtcaeFormPage form1Page2 = new CtcaeFormPage(2,form1.getId(),2, "Confronto ad un anno", "Selezioni una risposta per ognuna delle seguenti domande");
            mDao.insertFormPage(form1Page2);

            CtcaeFormQuestion form1Page2Question1 = new CtcaeFormQuestion(2,form1.getId(),form1Page2.getId(),"In generale direbbe che la sua salute è:");
            mDao.insertFormQuestion(form1Page2Question1);
            CtcaePossibleAnswer form1Page2Question1Poss1 = new CtcaePossibleAnswer(1211,form1.getId(),form1Page2.getId(),form1Page2Question1.getId(),"Decisamente migliore adesso");
            mDao.insertPossibleAnswer(form1Page2Question1Poss1);
            CtcaePossibleAnswer form1Page2Question1Poss2 = new CtcaePossibleAnswer(1212,form1.getId(),form1Page2.getId(),form1Page2Question1.getId(),"Un po' migliore adesso");
            mDao.insertPossibleAnswer(form1Page2Question1Poss2);
            CtcaePossibleAnswer form1Page2Question1Poss3 = new CtcaePossibleAnswer(1213,form1.getId(),form1Page2.getId(),form1Page2Question1.getId(),"Più o meno uguale");
            mDao.insertPossibleAnswer(form1Page2Question1Poss3);
            CtcaePossibleAnswer form1Page2Question1Poss4 = new CtcaePossibleAnswer(1214,form1.getId(),form1Page2.getId(),form1Page2Question1.getId(),"Un po' peggiore adesso");
            mDao.insertPossibleAnswer(form1Page2Question1Poss4);
            CtcaePossibleAnswer form1Page2Question1Poss5 = new CtcaePossibleAnswer(1215,form1.getId(),form1Page2.getId(),form1Page2Question1.getId(),"Decisamente peggiore adesso");
            mDao.insertPossibleAnswer(form1Page2Question1Poss5);

            //page 3
            CtcaeFormPage form1Page3 = new CtcaeFormPage(3,form1.getId(),3, "Impatto su attività giornaliere", "Selezioni una risposta per ognuna delle seguenti domande");
            mDao.insertFormPage(form1Page3);

            CtcaeFormQuestion form1Page3Question1 = new CtcaeFormQuestion(3,form1.getId(),form1Page3.getId(),"La Sua salute la limita attualmente nello svolgimento di attività " +
                    "fisicamente impegnative, come correre, sollevare oggetti pesanti, praticare sport faticosi");
            mDao.insertFormQuestion(form1Page3Question1);
            CtcaePossibleAnswer form1Page3Question1Poss1 = new CtcaePossibleAnswer(1311,form1.getId(),form1Page3.getId(),form1Page3Question1.getId(),"SI, mi limita parecchio");
            mDao.insertPossibleAnswer(form1Page3Question1Poss1);
            CtcaePossibleAnswer form1Page3Question1Poss2 = new CtcaePossibleAnswer(1312,form1.getId(),form1Page3.getId(),form1Page3Question1.getId(),"SI, mi limita parzialmente");
            mDao.insertPossibleAnswer(form1Page3Question1Poss2);
            CtcaePossibleAnswer form1Page3Question1Poss3 = new CtcaePossibleAnswer(1313,form1.getId(),form1Page3.getId(),form1Page3Question1.getId(),"NO, non mi limita per nulla");
            mDao.insertPossibleAnswer(form1Page3Question1Poss3);

            CtcaeFormQuestion form1Page3Question2 = new CtcaeFormQuestion(4,form1.getId(),form1Page3.getId(),"La Sua salute la limita attualmente nello svolgimento " +
                    "di attività di moderato impegno fisico, come spostare un tavolo, usare l'aspiravolvere, giocare a bocceo fare un giro in bicicletta");
            mDao.insertFormQuestion(form1Page3Question2);
            CtcaePossibleAnswer form1Page3Question2Poss1 = new CtcaePossibleAnswer(1321,form1.getId(),form1Page3.getId(),form1Page3Question2.getId(),"SI, mi limita parecchio");
            mDao.insertPossibleAnswer(form1Page3Question2Poss1);
            CtcaePossibleAnswer form1Page3Question2Poss2 = new CtcaePossibleAnswer(1322,form1.getId(),form1Page3.getId(),form1Page3Question2.getId(),"SI, mi limita parzialmente");
            mDao.insertPossibleAnswer(form1Page3Question2Poss2);
            CtcaePossibleAnswer form1Page3Question2Poss3 = new CtcaePossibleAnswer(1323,form1.getId(),form1Page3.getId(),form1Page3Question2.getId(),"NO, non mi limita per nulla");
            mDao.insertPossibleAnswer(form1Page3Question2Poss3);

            CtcaeFormQuestion form1Page3Question3 = new CtcaeFormQuestion(5,form1.getId(),form1Page3.getId(),"La Sua salute la limita attualmente nel " +
                    "sollevare o portare le borse della spesa");
            mDao.insertFormQuestion(form1Page3Question3);
            CtcaePossibleAnswer form1Page3Question3Poss1 = new CtcaePossibleAnswer(1331,form1.getId(),form1Page3.getId(),form1Page3Question3.getId(),"SI, mi limita parecchio");
            mDao.insertPossibleAnswer(form1Page3Question3Poss1);
            CtcaePossibleAnswer form1Page3Question3Poss2 = new CtcaePossibleAnswer(1332,form1.getId(),form1Page3.getId(),form1Page3Question3.getId(),"SI, mi limita parzialmente");
            mDao.insertPossibleAnswer(form1Page3Question3Poss2);
            CtcaePossibleAnswer form1Page3Question3Poss3 = new CtcaePossibleAnswer(1333,form1.getId(),form1Page3.getId(),form1Page3Question3.getId(),"NO, non mi limita per nulla");
            mDao.insertPossibleAnswer(form1Page3Question3Poss3);

            CtcaeFormQuestion form1Page3Question4 = new CtcaeFormQuestion(6,form1.getId(),form1Page3.getId(),"La Sua salute la limita attualmente nel " +
                    "zsalire qualche piano di scale");
            mDao.insertFormQuestion(form1Page3Question4);
            CtcaePossibleAnswer form1Page3Question4Poss1 = new CtcaePossibleAnswer(1341,form1.getId(),form1Page3.getId(),form1Page3Question4.getId(),"SI, mi limita parecchio");
            mDao.insertPossibleAnswer(form1Page3Question4Poss1);
            CtcaePossibleAnswer form1Page3Question4Poss2 = new CtcaePossibleAnswer(1342,form1.getId(),form1Page3.getId(),form1Page3Question4.getId(),"SI, mi limita parzialmente");
            mDao.insertPossibleAnswer(form1Page3Question4Poss2);
            CtcaePossibleAnswer form1Page3Question4Poss3 = new CtcaePossibleAnswer(1343,form1.getId(),form1Page3.getId(),form1Page3Question4.getId(),"NO, non mi limita per nulla");
            mDao.insertPossibleAnswer(form1Page3Question4Poss3);

            CtcaeFormQuestion form1Page3Question5 = new CtcaeFormQuestion(7,form1.getId(),form1Page3.getId(),"La Sua salute la limita attualmente nel " +
                    "piagarsi, inginocchiarsi o chinarsi");
            mDao.insertFormQuestion(form1Page3Question5);
            CtcaePossibleAnswer form1Page3Question5Poss1 = new CtcaePossibleAnswer(1351,form1.getId(),form1Page3.getId(),form1Page3Question5.getId(),"SI, mi limita parecchio");
            mDao.insertPossibleAnswer(form1Page3Question5Poss1);
            CtcaePossibleAnswer form1Page3Question5Poss2 = new CtcaePossibleAnswer(1352,form1.getId(),form1Page3.getId(),form1Page3Question5.getId(),"SI, mi limita parzialmente");
            mDao.insertPossibleAnswer(form1Page3Question5Poss2);
            CtcaePossibleAnswer form1Page3Question5Poss3 = new CtcaePossibleAnswer(1353,form1.getId(),form1Page3.getId(),form1Page3Question5.getId(),"NO, non mi limita per nulla");
            mDao.insertPossibleAnswer(form1Page3Question5Poss3);

            CtcaeFormQuestion form1Page3Question6 = new CtcaeFormQuestion(8,form1.getId(),form1Page3.getId(),"La Sua salute la limita attualmente nel " +
                    "camminare per un chilometro");
            mDao.insertFormQuestion(form1Page3Question6);
            CtcaePossibleAnswer form1Page3Question6Poss1 = new CtcaePossibleAnswer(1361,form1.getId(),form1Page3.getId(),form1Page3Question6.getId(),"SI, mi limita parecchio");
            mDao.insertPossibleAnswer(form1Page3Question6Poss1);
            CtcaePossibleAnswer form1Page3Question6Poss2 = new CtcaePossibleAnswer(1362,form1.getId(),form1Page3.getId(),form1Page3Question6.getId(),"SI, mi limita parzialmente");
            mDao.insertPossibleAnswer(form1Page3Question6Poss2);
            CtcaePossibleAnswer form1Page3Question6Poss3 = new CtcaePossibleAnswer(1363,form1.getId(),form1Page3.getId(),form1Page3Question6.getId(),"NO, non mi limita per nulla");
            mDao.insertPossibleAnswer(form1Page3Question6Poss3);

            CtcaeFormQuestion form1Page3Question7 = new CtcaeFormQuestion(9,form1.getId(),form1Page3.getId(),"La Sua salute la limita attualmente nel " +
                    "camminare per qualche centinaio di metri");
            mDao.insertFormQuestion(form1Page3Question7);
            CtcaePossibleAnswer form1Page3Question7Poss1 = new CtcaePossibleAnswer(1371,form1.getId(),form1Page3.getId(),form1Page3Question7.getId(),"SI, mi limita parecchio");
            mDao.insertPossibleAnswer(form1Page3Question7Poss1);
            CtcaePossibleAnswer form1Page3Question7Poss2 = new CtcaePossibleAnswer(1372,form1.getId(),form1Page3.getId(),form1Page3Question7.getId(),"SI, mi limita parzialmente");
            mDao.insertPossibleAnswer(form1Page3Question7Poss2);
            CtcaePossibleAnswer form1Page3Question7Poss3 = new CtcaePossibleAnswer(1373,form1.getId(),form1Page3.getId(),form1Page3Question7.getId(),"NO, non mi limita per nulla");
            mDao.insertPossibleAnswer(form1Page3Question7Poss3);

            CtcaeFormQuestion form1Page3Question8 = new CtcaeFormQuestion(10,form1.getId(),form1Page3.getId(),"La Sua salute la limita attualmente nel" +
                    " camminare per circa cento metri");
            mDao.insertFormQuestion(form1Page3Question8);
            CtcaePossibleAnswer form1Page3Question8Poss1 = new CtcaePossibleAnswer(1381,form1.getId(),form1Page3.getId(),form1Page3Question8.getId(),"SI, mi limita parecchio");
            mDao.insertPossibleAnswer(form1Page3Question8Poss1);
            CtcaePossibleAnswer form1Page3Question8Poss2 = new CtcaePossibleAnswer(1382,form1.getId(),form1Page3.getId(),form1Page3Question8.getId(),"SI, mi limita parzialmente");
            mDao.insertPossibleAnswer(form1Page3Question8Poss2);
            CtcaePossibleAnswer form1Page3Question8Poss3 = new CtcaePossibleAnswer(1383,form1.getId(),form1Page3.getId(),form1Page3Question8.getId(),"NO, non mi limita per nulla");
            mDao.insertPossibleAnswer(form1Page3Question8Poss3);

            CtcaeFormQuestion form1Page3Question9 = new CtcaeFormQuestion(11,form1.getId(),form1Page3.getId(),"La Sua salute la limita attualmente nel " +
                    "fare il bagno o vestirsi da soli");
            mDao.insertFormQuestion(form1Page3Question9);
            CtcaePossibleAnswer form1Page3Question9Poss1 = new CtcaePossibleAnswer(1391,form1.getId(),form1Page3.getId(),form1Page3Question9.getId(),"SI, mi limita parecchio");
            mDao.insertPossibleAnswer(form1Page3Question9Poss1);
            CtcaePossibleAnswer form1Page3Question9Poss2 = new CtcaePossibleAnswer(1392,form1.getId(),form1Page3.getId(),form1Page3Question9.getId(),"SI, mi limita parzialmente");
            mDao.insertPossibleAnswer(form1Page3Question9Poss2);
            CtcaePossibleAnswer form1Page3Question9Poss3 = new CtcaePossibleAnswer(1393,form1.getId(),form1Page3.getId(),form1Page3Question9.getId(),"NO, non mi limita per nulla");
            mDao.insertPossibleAnswer(form1Page3Question9Poss3);

            //page 4
            CtcaeFormPage form1Page4 = new CtcaeFormPage(4,form1.getId(),4, "Impatto a medio termine su attività giornaliere", "Risponda SI o NO ad ognuna delle seguenti domande");
            mDao.insertFormPage(form1Page4);
            CtcaeFormQuestion form1Page4Question1 = new CtcaeFormQuestion(12,form1.getId(),form1Page4.getId(),"Nelle ultime 4 settimane ha ridotto" +
                    "il tempo dedicato al lavoro o ad altre attività");
            mDao.insertFormQuestion(form1Page4Question1);
            CtcaePossibleAnswer form1Page4Question1Poss1 = new CtcaePossibleAnswer(1411,form1.getId(),form1Page4.getId(),form1Page4Question1.getId(),"SI");
            mDao.insertPossibleAnswer(form1Page4Question1Poss1);
            CtcaePossibleAnswer form1Page4Question1Poss2 = new CtcaePossibleAnswer(1412,form1.getId(),form1Page4.getId(),form1Page4Question1.getId(),"NO");
            mDao.insertPossibleAnswer(form1Page4Question1Poss2);

            CtcaeFormQuestion form1Page4Question2 = new CtcaeFormQuestion(13,form1.getId(),form1Page4.getId(),"Nelle ultime 4 settimane ha  " +
                    " reso di meno di quanto avrebbe voluto");
            mDao.insertFormQuestion(form1Page4Question2);
            CtcaePossibleAnswer form1Page4Question2Poss1 = new CtcaePossibleAnswer(1421,form1.getId(),form1Page4.getId(),form1Page4Question2.getId(),"SI");
            mDao.insertPossibleAnswer(form1Page4Question2Poss1);
            CtcaePossibleAnswer form1Page4Question2Poss2 = new CtcaePossibleAnswer(1422,form1.getId(),form1Page4.getId(),form1Page4Question2.getId(),"NO");
            mDao.insertPossibleAnswer(form1Page4Question2Poss2);

            CtcaeFormQuestion form1Page4Question3 = new CtcaeFormQuestion(14,form1.getId(),form1Page4.getId(),"Nelle ultime 4 settimane ha  " +
                    " dovuto limitare alcuni tipi di lavoro o di altre attività");
            mDao.insertFormQuestion(form1Page4Question3);
            CtcaePossibleAnswer form1Page4Question3Poss1 = new CtcaePossibleAnswer(1431,form1.getId(),form1Page4.getId(),form1Page4Question3.getId(),"SI");
            mDao.insertPossibleAnswer(form1Page4Question3Poss1);
            CtcaePossibleAnswer form1Page4Question3Poss2 = new CtcaePossibleAnswer(1432,form1.getId(),form1Page4.getId(),form1Page4Question3.getId(),"NO");
            mDao.insertPossibleAnswer(form1Page4Question3Poss2);

            CtcaeFormQuestion form1Page4Question4 = new CtcaeFormQuestion(15,form1.getId(),form1Page4.getId(),"Nelle ultime 4 settimane ha  " +
                    "avuto difficoltà nell'eseguire il lavoro o altre attività (ad esempio ha fatto più fatica)");
            mDao.insertFormQuestion(form1Page4Question4);
            CtcaePossibleAnswer form1Page4Question4Poss1 = new CtcaePossibleAnswer(1441,form1.getId(),form1Page4.getId(),form1Page4Question4.getId(),"SI");
            mDao.insertPossibleAnswer(form1Page4Question4Poss1);
            CtcaePossibleAnswer form1Page4Question4Poss2 = new CtcaePossibleAnswer(1442,form1.getId(),form1Page4.getId(),form1Page4Question4.getId(),"NO");
            mDao.insertPossibleAnswer(form1Page4Question4Poss2);


            return null;
        }
    }
}
