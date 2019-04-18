package com.obdasystems.pocmedici.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.network.MediciApi;
import com.obdasystems.pocmedici.network.MediciApiClient;
import com.obdasystems.pocmedici.network.NetworkUtils;
import com.obdasystems.pocmedici.network.RestPrescriptions;
import com.obdasystems.pocmedici.utils.SaveSharedPreference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrescriptionListActivity extends AppActivity {
    private WebView webView;
    private int recursiveCallCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup toolbar
        setContentView(R.layout.activity_prescription_list);
        Toolbar toolbar = find(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Setup Web view
        webView = find(R.id.prescription_details_view);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView = find(R.id.prescription_details_view);
        getPrescriptions();
    }

    @Override
    public void onBackPressed() {
        backToMain();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_BACK
                && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void updateWebView(String url) {
        this.webView.loadUrl(url);
    }

    private void backToMain() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

    private void getPrescriptions() {
        if (recursiveCallCounter < 15) {
            recursiveCallCounter++;
            String usr = "james";
            String pwd = "bush";

            String authorizationToken = SaveSharedPreference.getAuthorizationToken(this);
            if (authorizationToken == null) {
                NetworkUtils.requestNewAuthorizationToken(pwd, usr, this);
            }
            authorizationToken = SaveSharedPreference.getAuthorizationToken(this);

            MediciApi apiService = MediciApiClient.createService(MediciApi.class, authorizationToken);

            Call<RestPrescriptions> call = apiService.getPrescriptions();
            call.enqueue(new Callback<RestPrescriptions>() {
                @Override
                public void onResponse(Call<RestPrescriptions> call, Response<RestPrescriptions> response) {

                    if (response.isSuccessful()) {
                        updateWebView(response.body().getUrl());
                        recursiveCallCounter = 0;
                    } else {
                        switch (response.code()) {
                            case 401:
                                NetworkUtils.requestNewAuthorizationToken(pwd, usr, context());
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch prescriptions (401)");
                                if (!SaveSharedPreference.getAuthorizationIssue(context())) {
                                    getPrescriptions();
                                } else {
                                    String issueDescription = SaveSharedPreference.getAuthorizationIssueDescription(context());
                                    Toast.makeText(getApplicationContext(), "Unable to fetch prescriptions (401) [" + issueDescription + "]", Toast.LENGTH_LONG).show();
                                    Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch prescriptions (401) [" + issueDescription + "]");
                                }
                                break;
                            case 404:
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch prescriptions (404)");
                                Toast.makeText(getApplicationContext(), "Unable to fetch prescriptions (404)", Toast.LENGTH_LONG).show();
                                break;
                            case 500:
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch prescriptions (500)");
                                Toast.makeText(getApplicationContext(), "Unable to fetch prescriptions (500)", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch prescriptions (UNKNOWN)");
                                Toast.makeText(getApplicationContext(), "Unable to fetch prescriptions (UNKNOWN)", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                }

                @Override
                public void onFailure(Call<RestPrescriptions> call, Throwable t) {
                    Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch prescriptions: " + t.getMessage());
                    Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch prescriptions: " + t.getStackTrace());
                    Toast.makeText(getApplicationContext(), "Unable to fetch prescriptions..", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Max number of calls to getPrescriptions() reached!!");
            Toast.makeText(getApplicationContext(), "Max number of calls to getPrescriptions() reached!!", Toast.LENGTH_LONG).show();
            recursiveCallCounter = 0;
        }
    }

}
