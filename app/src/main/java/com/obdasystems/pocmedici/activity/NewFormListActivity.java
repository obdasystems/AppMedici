package com.obdasystems.pocmedici.activity;

import android.app.Application;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.adapter.FormListAdapter;
import com.obdasystems.pocmedici.adapter.MessagesAdapter;
import com.obdasystems.pocmedici.adapter.NewFormListAdapter;
import com.obdasystems.pocmedici.asyncresponse.FinalizedFormAsyncResponse;
import com.obdasystems.pocmedici.listener.OnFormRecyclerViewItemClickListener;
import com.obdasystems.pocmedici.message.helper.DividerItemDecoration;
import com.obdasystems.pocmedici.persistence.entities.CtcaeFormFillingProcess;
import com.obdasystems.pocmedici.persistence.entities.JoinFormWithMaxPageNumberData;
import com.obdasystems.pocmedici.persistence.repository.CtcaeFormRepository;
import com.obdasystems.pocmedici.persistence.repository.CtcaeGetFinalizedFillingProcessRepository;
import com.obdasystems.pocmedici.persistence.viewmodel.CtcaeFormListViewModel;

import java.util.ArrayList;
import java.util.List;


public class NewFormListActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, NewFormListAdapter.FormAdapterListener{
    private CtcaeFormListViewModel formListViewModel;

    private List<JoinFormWithMaxPageNumberData> forms= new ArrayList<>();
    private RecyclerView recyclerView;
    private NewFormListAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Context ctx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ctx = this;
        setContentView(R.layout.activity_form_list_new);

        Toolbar toolbar = (Toolbar) findViewById(R.id.new_form_list_toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_black_24dp);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMain();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.form_list_recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.form_list_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new NewFormListAdapter(this, forms, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        formListViewModel = ViewModelProviders.of(this).get(CtcaeFormListViewModel.class);
        formListViewModel.getAllForms().observe(this, new Observer<List<JoinFormWithMaxPageNumberData>>() {
            @Override
            public void onChanged(@Nullable List<JoinFormWithMaxPageNumberData> ctcaeForms) {
                mAdapter.setForms(ctcaeForms);
                mAdapter.notifyDataSetChanged();
            }
        });


        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        getFormsByRest();
                    }
                }
        );
    }


    @Override
    public void onBackPressed() {
        backToMain();
    }


    private void backToMain() {
        Intent mainIntent = new Intent(ctx, MainActivity.class);
        startActivity(mainIntent);
    }

    /**********************************
     * METHOD TO GET FORMS BY REST CALL
     *
     **********************************/
    private void getFormsByRest() {
        swipeRefreshLayout.setRefreshing(true);


        Toast.makeText(getApplicationContext(), "REST CALLS NOT IMPLEMENTED YET!!", Toast.LENGTH_LONG).show();
        swipeRefreshLayout.setRefreshing(false);
        /* TODO replace with our call for forms
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<List<Message>> call = apiService.getInbox();
        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                // clear the inbox
                messages.clear();

                // add all the messages
                // messages.addAll(response.body());

                // TODO - avoid looping
                // the loop was performed to add colors to each message
                for (Message message : response.body()) {
                    // generate a random color
                    message.setColor(getRandomMaterialColor("400"));
                    messages.add(message);
                }

                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });*/
    }

    /************************
     * SWIPE REFRESH METHODS
     *
     ************************/
    @Override
    public void onRefresh() {
        getFormsByRest();
    }


    /**************************
     * ADAPTER LISTENER METHODS
     *
     **************************/
    @Override
    public void onIconClicked(int position) {

    }

    @Override
    public void onFormRowClicked(int position) {
        JoinFormWithMaxPageNumberData clickedForm = mAdapter.getFormAtPosition(position);
        if(clickedForm!=null) {
            Intent intent = new Intent(this,CtcaeFormActivity.class);
            intent.putExtra("clickedForm", clickedForm);
            //startActivityForResult(intent, CTCAE_FORM_SUBMITTED_CODE);
            startActivity(intent);
        }
    }
}


