package com.obdasystems.pocmedici.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.persistence.entities.StepCounter;

import java.util.List;

public class StepCounterListAdapter extends RecyclerView.Adapter<StepCounterListAdapter.MyViewHolder> {
    private Context mContext;
    //private List<Message> messages;
    private List<StepCounter> stepCounters;



    public StepCounterListAdapter(Context mContext, List<StepCounter> counters) {
        this.mContext = mContext;
        this.stepCounters = counters;
    }


    //CUSTOM METHODS
    public void setCounters(List<StepCounter> counters) {
        this.stepCounters = counters;
    }



    @Override
    public StepCounterListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_counter_list_row, parent, false);
        return new StepCounterListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StepCounterListAdapter.MyViewHolder holder, final int position) {
        StepCounter counter = stepCounters.get(position);

        holder.value.setText("Step Count: "+ String.valueOf(counter.getStepCount()));

        holder.date.setText(counter.getDay()+ "/" + counter.getMonth()+ "/" + counter.getYear());

    }



    @Override
    public int getItemCount() {
        return stepCounters.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView value, date;
        public LinearLayout stepCounterContainer;

        public MyViewHolder(View view) {
            super(view);
            value = (TextView) view.findViewById(R.id.stepCounterValue);
            date = (TextView) view.findViewById(R.id.stepCounterDate);

            stepCounterContainer = (LinearLayout) view.findViewById(R.id.step_counter_container);
        }
    }
}

