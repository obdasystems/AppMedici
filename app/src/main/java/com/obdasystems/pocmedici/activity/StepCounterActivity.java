package com.obdasystems.pocmedici.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.adapter.StepCounterListAdapter;
import com.obdasystems.pocmedici.message.helper.DividerItemDecoration;
import com.obdasystems.pocmedici.persistence.entities.StepCounter;
import com.obdasystems.pocmedici.persistence.viewmodel.StepCountersViewModel;

import java.util.ArrayList;
import java.util.List;

public class StepCounterActivity extends AppCompatActivity {

    private StepCountersViewModel viewModel;

    private List<StepCounter> counters= new ArrayList<>();
    private RecyclerView recyclerView;
    private StepCounterListAdapter mAdapter;

    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_step_counters_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.step_counter_list_toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_black_24dp);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(ctx, MainActivity.class);
                startActivity(mainIntent);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.step_counter_recycler_view);

        mAdapter = new StepCounterListAdapter(this, counters);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        viewModel = ViewModelProviders.of(this).get(StepCountersViewModel.class);
        viewModel.getAllCounters().observe(this, new Observer<List<StepCounter>>() {
            @Override
            public void onChanged(@Nullable List<StepCounter> stepCount) {
                mAdapter.setCounters(stepCount);
                mAdapter.notifyDataSetChanged();
            }
        });

    }


    @Override
    public void onBackPressed() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

}
