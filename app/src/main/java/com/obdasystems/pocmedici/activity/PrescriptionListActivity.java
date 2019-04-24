package com.obdasystems.pocmedici.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.network.ApiClient;
import com.obdasystems.pocmedici.network.ItcoService;
import com.obdasystems.pocmedici.network.RestPrescriptions;
import com.obdasystems.pocmedici.network.interceptors.AuthenticationInterceptor;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PrescriptionListActivity extends AppActivity {
    private WebView webView;

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
        ItcoService apiService = ApiClient
                .forService(ItcoService.class)
                .baseURL(ApiClient.BASE_URL)
                .logging(HttpLoggingInterceptor.Level.BODY)
                .addInterceptor(new AuthenticationInterceptor(this))
                .build();

        apiService.getPrescriptions().enqueue(new Callback<RestPrescriptions>() {
            @Override
            public void onResponse(Call<RestPrescriptions> call,
                                   Response<RestPrescriptions> response) {
                if (response.isSuccessful()) {
                    updateWebView(response.body().getUrl());
                } else {
                    Log.e(tag(), "Unable to fetch prescriptions");
                    snack("Unable to fetch prescriptions (UNKNOWN)", Snackbar.LENGTH_LONG);
                }
            }

            @Override
            public void onFailure(Call<RestPrescriptions> call, Throwable t) {
                Log.e(tag(), "Unable to fetch prescriptions: ", t);
                snack("Unable to fetch prescriptions..", Snackbar.LENGTH_LONG);
            }
        });
    }

}
