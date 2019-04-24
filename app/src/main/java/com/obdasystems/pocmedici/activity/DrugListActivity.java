package com.obdasystems.pocmedici.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.adapter.DrugListAdapter;
import com.obdasystems.pocmedici.message.helper.DividerItemDecoration;
import com.obdasystems.pocmedici.network.ApiClient;
import com.obdasystems.pocmedici.network.ItcoService;
import com.obdasystems.pocmedici.network.RestDrug;
import com.obdasystems.pocmedici.network.RestDrugList;
import com.obdasystems.pocmedici.network.interceptors.AuthenticationInterceptor;

import java.util.ArrayList;
import java.util.List;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DrugListActivity extends AppActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        DrugListAdapter.DrugAdapterListener {
    private List<RestDrug> drugs = new ArrayList<>();
    private DrugListAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug_list);

        Toolbar toolbar = find(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> backToMain());

        RecyclerView recyclerView = find(R.id.recycler_view);
        swipeRefreshLayout = find(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new DrugListAdapter(this, drugs, this);
        RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(
                new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        // show loader and fetch messages
        swipeRefreshLayout.post(this::getDrugs);
    }

    private void backToMain() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

    @Override
    public void onBackPressed() {
        backToMain();
    }

    private void getDrugs() {
        swipeRefreshLayout.setRefreshing(true);

        ItcoService apiService = ApiClient
                .forService(ItcoService.class)
                .baseURL(ApiClient.BASE_URL)
                .logging(HttpLoggingInterceptor.Level.BODY)
                .addInterceptor(new AuthenticationInterceptor(this))
                .build();

        apiService.getDrugs()
                .enqueue(new Callback<RestDrugList>() {
                    @Override
                    public void onResponse(Call<RestDrugList> call,
                                           Response<RestDrugList> response) {
                        if (response.isSuccessful()) {
                            drugs.clear();
                            drugs.addAll(response.body().getDrugs());
                            mAdapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            Log.e(tag(), "Unable to fetch drug list");
                            snack("Unable to fetch drug list", Snackbar.LENGTH_LONG);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onFailure(Call<RestDrugList> call, Throwable t) {
                        Log.e(tag(), "Unable to fetch drug list: ", t);
                        snack("Unable to fetch drug list..", Snackbar.LENGTH_LONG);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
        getDrugs();
    }

    @Override
    public void onDrugRowClicked(int position) {
        RestDrug drug = drugs.get(position);
        Intent drugIntent = new Intent(this, DrugActivity.class);
        drugIntent.putExtra("url", drug.getUrl());
        drugIntent.putExtra("name", drug.getName());
        startActivity(drugIntent);
    }

}
