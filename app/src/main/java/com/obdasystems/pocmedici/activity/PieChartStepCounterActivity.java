package com.obdasystems.pocmedici.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.adapter.StepCounterListAdapter;
import com.obdasystems.pocmedici.asyncresponse.StepCounterAsyncResponse;
import com.obdasystems.pocmedici.message.helper.DividerItemDecoration;
import com.obdasystems.pocmedici.persistence.entities.StepCounter;
import com.obdasystems.pocmedici.persistence.repository.StepCounterRepository;
import com.obdasystems.pocmedici.persistence.viewmodel.StepCountersViewModel;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PieChartStepCounterActivity extends AppActivity
        implements StepCounterAsyncResponse {
    private int goal = 1000;
    private boolean showSteps = true;
    private NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
    private TextView stepsView;
    private PieModel sliceGoal, sliceCurrent;
    private PieChart pg;
    private List<StepCounter> counters = new ArrayList<>();
    private StepCounterListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piechart_step_counter);

        Toolbar toolbar = find(R.id.step_counter_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> backToMain());

        pg = find(R.id.graph);
        stepsView = find(R.id.steps);

        sliceCurrent = new PieModel("", 0, Color.parseColor("#99CC00"));
        pg.addPieSlice(sliceCurrent);

        // slice for the "missing" steps until reaching the goal
        sliceGoal = new PieModel("", goal, Color.parseColor("#CC0000"));
        pg.addPieSlice(sliceGoal);

        pg.setDrawValueInPie(false);
        pg.setUsePieRotation(true);
        pg.startAnimation();

        pg.setOnClickListener(view -> {
            showSteps = !showSteps;
            //stepsDistanceChanged();
            Log.i(tag(), "CLICKED ON PIECHART!!");
            GetTodayStepCounterTask task = getTodayStepCounterTask();
            task.execute();
        });

        GetTodayStepCounterTask task = getTodayStepCounterTask();
        task.execute();

        RecyclerView recyclerView = find(R.id.step_counter_recycler_view);

        mAdapter = new StepCounterListAdapter(this, counters);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        StepCountersViewModel viewModel = ViewModelProviders.of(this).get(StepCountersViewModel.class);
        viewModel.getAllCounters().observe(this, stepCount -> {
            mAdapter.setCounters(stepCount);
            mAdapter.notifyDataSetChanged();
            GetTodayStepCounterTask task1 = getTodayStepCounterTask();
            task1.execute();
        });
    }

    @Override
    public void onBackPressed() {
        backToMain();
    }

    private void backToMain() {
        Intent mainIntent = new Intent(context(), MainActivity.class);
        startActivity(mainIntent);
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
        } else {
            // goal reached
            pg.clearChart();
            pg.addPieSlice(sliceCurrent);
        }
        pg.update();
        stepsView.setText(formatter.format(stepsToday));
    }

    private GetTodayStepCounterTask getTodayStepCounterTask() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return new GetTodayStepCounterTask(year, month, day, this, this);
    }

    // Get all questions in page along with questions already answered in cyurrent filling process
    private static class GetTodayStepCounterTask extends AsyncTask<Void, Void, StepCounter> {
        private Context ctx;
        //private ProgressDialog progDial;
        private int year, month, day;
        private StepCounterRepository repository;
        private StepCounterAsyncResponse delegate;

        GetTodayStepCounterTask(int year, int month, int day, Context context, StepCounterAsyncResponse delegate) {
            ctx = context;
            this.year = year;
            this.month = month;
            this.day = day;
            //progDial = new ProgressDialog(ctx);
            this.delegate = delegate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*progDial.setMessage("Retrieving step counter...");
            progDial.setIndeterminate(false);
            progDial.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDial.setCancelable(false);
            progDial.show();*/
        }

        @Override
        protected StepCounter doInBackground(Void... voids) {
            repository = new StepCounterRepository(ctx);
            return repository.getStepCounter(year, month, day);
        }

        @Override
        protected void onPostExecute(StepCounter sp) {
            super.onPostExecute(sp);
            //progDial.dismiss();
            delegate.getTodayStepCounterTaskFinished(sp);
        }
    }

}
