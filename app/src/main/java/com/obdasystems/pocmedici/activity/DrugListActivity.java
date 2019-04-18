package com.obdasystems.pocmedici.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.adapter.DrugListAdapter;
import com.obdasystems.pocmedici.message.helper.DividerItemDecoration;
import com.obdasystems.pocmedici.network.MediciApi;
import com.obdasystems.pocmedici.network.MediciApiClient;
import com.obdasystems.pocmedici.network.NetworkUtils;
import com.obdasystems.pocmedici.network.RestDrug;
import com.obdasystems.pocmedici.network.RestDrugList;
import com.obdasystems.pocmedici.utils.SaveSharedPreference;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DrugListActivity extends AppActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        DrugListAdapter.DrugAdapterListener {
    private List<RestDrug> drugs = new ArrayList<>();
    private DrugListAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Context ctx;

    private int recursiveGetInboxCallCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ctx = this;
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
        swipeRefreshLayout.post(() -> getDrugs(recursiveGetInboxCallCounter));
    }

    private void backToMain() {
        Intent mainIntent = new Intent(ctx, MainActivity.class);
        startActivity(mainIntent);
    }

    @Override
    public void onBackPressed() {
        backToMain();
    }

    private void getDrugs(int counter) {
        swipeRefreshLayout.setRefreshing(true);

        if (counter < 15) {
            String usr = "james";
            String pwd = "bush";

            String authorizationToken = SaveSharedPreference.getAuthorizationToken(this);
            if (authorizationToken == null) {
                NetworkUtils.requestNewAuthorizationToken(pwd, usr, this);
            }
            authorizationToken = SaveSharedPreference.getAuthorizationToken(this);

            MediciApi apiService = MediciApiClient.createService(MediciApi.class, authorizationToken);

            Call<RestDrugList> call = apiService.getDrugs();
            call.enqueue(new Callback<RestDrugList>() {
                @Override
                public void onResponse(Call<RestDrugList> call, Response<RestDrugList> response) {

                    if (response.isSuccessful()) {
                        drugs.clear();
                        RestDrugList dl = response.body();
                        for(RestDrug drug:dl.getDrugs()) {
                            drugs.add(drug);
                        }
                        recursiveGetInboxCallCounter = 0;
                        mAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    } else {
                        switch (response.code()) {
                            case 401:
                                NetworkUtils.requestNewAuthorizationToken(pwd, usr, ctx);
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch drug list (401)");
                                if (!SaveSharedPreference.getAuthorizationIssue(ctx)) {
                                    getDrugs(recursiveGetInboxCallCounter++);
                                } else {
                                    String issueDescription = SaveSharedPreference.getAuthorizationIssueDescription(ctx);
                                    Toast.makeText(getApplicationContext(), "Unable to fetch drug list (401) [" + issueDescription + "]", Toast.LENGTH_LONG).show();
                                    Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch drug list (401) [" + issueDescription + "]");
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                                break;
                            case 404:
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch drug list (404)");
                                Toast.makeText(getApplicationContext(), "Unable to fetch drug list (404)", Toast.LENGTH_LONG).show();
                                swipeRefreshLayout.setRefreshing(false);
                                break;
                            case 500:
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch drug list (500)");
                                Toast.makeText(getApplicationContext(), "Unable to fetch drug list (500)", Toast.LENGTH_LONG).show();
                                swipeRefreshLayout.setRefreshing(false);
                                break;
                            default:
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch drug list (UNKNOWN)");
                                Toast.makeText(getApplicationContext(), "Unable to fetch drug list (UNKNOWN)", Toast.LENGTH_LONG).show();
                                swipeRefreshLayout.setRefreshing(false);
                                break;
                        }
                    }
                }

                @Override
                public void onFailure(Call<RestDrugList> call, Throwable t) {
                    Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch drug list: " + t.getMessage());
                    Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch drug list: " + t.getStackTrace());
                    Toast.makeText(getApplicationContext(), "Unable to fetch drug list..", Toast.LENGTH_LONG).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        } else {
            Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Max number of calls to getDrugs() reached!!");
            Toast.makeText(getApplicationContext(), "Max number of calls to getDrugs() reached!!", Toast.LENGTH_LONG).show();
            recursiveGetInboxCallCounter = 0;
        }
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
        getDrugs(this.recursiveGetInboxCallCounter);
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
