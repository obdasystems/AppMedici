package com.obdasystems.pocmedici.activity;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.asyncresponse.PageQuestionsAsyncResponse;
import com.obdasystems.pocmedici.asyncresponse.StepCounterAsyncResponse;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormQuestion;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormQuestionAnswered;
import com.obdasystems.pocmedici.persistence.entities.JoinFormPageQuestionsWithPossibleAnswerData;
import com.obdasystems.pocmedici.persistence.entities.StepCounter;
import com.obdasystems.pocmedici.persistence.repository.CtcaeFillingProcessAnsweredQuestionRepository;
import com.obdasystems.pocmedici.persistence.repository.CtcaeFormQuestionsRepository;
import com.obdasystems.pocmedici.persistence.repository.StepCounterRepository;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PieChartStepCounterActivity extends AppCompatActivity implements StepCounterAsyncResponse {

    private TextView stepsView, totalView, averageView;
    private PieModel sliceGoal, sliceCurrent;
    private PieChart pg;

    private int todayOffset, total_start, since_boot, total_days;
    public final static NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
    private boolean showSteps = true;

    int goal = 10000;

    boolean firstDraw = true;

    int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piechart_step_counter);


        pg = (PieChart) findViewById(R.id.graph);
        stepsView = (TextView) findViewById(R.id.steps);

        sliceCurrent = new PieModel("", 100, Color.parseColor("#99CC00"));
        pg.addPieSlice(sliceCurrent);

        // slice for the "missing" steps until reaching the goal
        sliceGoal = new PieModel("", goal -100, Color.parseColor("#CC0000"));
        pg.addPieSlice(sliceGoal);

        pg.setDrawValueInPie(false);
        pg.setUsePieRotation(true);
        pg.startAnimation();

        pg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                showSteps = !showSteps;
                //stepsDistanceChanged();
                Log.i("appMedici", "CLICKED ON PIECHART!!");
                PieChartStepCounterActivity.GetTodayStepCounterTask task = getTodayStepCounterTask();
                task.execute();
            }
        });

        GetTodayStepCounterTask task = getTodayStepCounterTask();
        task.execute();
    }


    @Override
    public void getTodayStepCounterTaskFinished(StepCounter sp) {
        int stepsToday = 0;
        if (sp != null) {
            stepsToday = sp.getStepCount();
        }

        sliceCurrent.setValue(stepsToday);

        if (goal - stepsToday > 0) {
            // goal not reached yet
            sliceGoal.setValue(goal - stepsToday);
        }
        else {
            // goal reached
            pg.clearChart();
            pg.addPieSlice(sliceCurrent);
        }
        pg.update();
        stepsView.setText(formatter.format(stepsToday));
    }

    private GetTodayStepCounterTask getTodayStepCounterTask() {
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        day = cal.get(Calendar.DAY_OF_MONTH);
        PieChartStepCounterActivity.GetTodayStepCounterTask task = new PieChartStepCounterActivity.GetTodayStepCounterTask(year, month, day, this, this);
        return task;
    }

    //get all questions in page along with questions already answered in cyurrent filling process
    private static class GetTodayStepCounterTask extends AsyncTask<Void, Void, StepCounter> {
        private Context ctx;
        private ProgressDialog progDial;
        private int year, month, day;
        private StepCounterRepository repository;
        private StepCounterAsyncResponse delegate;

        GetTodayStepCounterTask(int year, int month, int day, Context context, StepCounterAsyncResponse delegate) {
            ctx = context;
            this.year = year;
            this.month = month;
            this.day = day;
            progDial = new ProgressDialog(ctx);
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progDial.setMessage("Retrieving step counter...");
            progDial.setIndeterminate(false);
            progDial.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDial.setCancelable(false);
            progDial.show();
        }

        @Override
        protected StepCounter doInBackground(Void... voids) {
            repository = new StepCounterRepository(ctx);
            StepCounter sp = repository.getStepCounter(year, month, day);
            return sp;
        }

        @Override
        protected void onPostExecute(StepCounter sp) {
            super.onPostExecute(sp);
            progDial.dismiss();
            delegate.getTodayStepCounterTaskFinished(sp);
        }
    }

}
