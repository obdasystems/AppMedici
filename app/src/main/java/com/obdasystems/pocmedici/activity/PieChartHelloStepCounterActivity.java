package com.obdasystems.pocmedici.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.asyncresponse.StepCounterAsyncResponse;
import com.obdasystems.pocmedici.persistence.entities.StepCounter;
import com.obdasystems.pocmedici.persistence.repository.StepCounterRepository;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class PieChartHelloStepCounterActivity extends AppCompatActivity implements StepCounterAsyncResponse {


    public final static NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());

    int goal = 10000;

    boolean firstDraw = true;

    int year, month, day;

    PieChartView pieChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piechart_hello_step_counter);
        pieChartView = findViewById(R.id.chart);

        PieChartHelloStepCounterActivity.GetTodayStepCounterTask task = getTodayStepCounterTask();
        task.execute();
    }

    @Override
    public void getTodayStepCounterTaskFinished(StepCounter sp) {
        int stepsToday = 0;
        if (sp != null) {
            stepsToday = sp.getStepCount();
        }
        SliceValue stepTodaySlice = new SliceValue(stepsToday, Color.GREEN);
        SliceValue remainingSlice = new SliceValue(goal- stepsToday, Color.RED);
        List<SliceValue> pieData = new LinkedList<>();
        pieData.add(stepTodaySlice);
        pieData.add(remainingSlice);


        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasCenterCircle(true).setCenterText1("STEPS: " +stepsToday);
        pieChartView.setPieChartData(pieChartData);

    }

    private GetTodayStepCounterTask getTodayStepCounterTask() {
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        day = cal.get(Calendar.DAY_OF_MONTH);
        PieChartHelloStepCounterActivity.GetTodayStepCounterTask task = new PieChartHelloStepCounterActivity.GetTodayStepCounterTask(year, month, day, this, this);
        return task;
    }

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
