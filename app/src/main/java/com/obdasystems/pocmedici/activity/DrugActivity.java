package com.obdasystems.pocmedici.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;

import com.obdasystems.pocmedici.R;

public class DrugActivity extends AppCompatActivity {

    private Context ctx;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_drug);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_black_24dp);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToList();
            }
        });

        Intent intent = getIntent();
        url = intent.getStringExtra("url");

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
        WebView webView = findViewById(R.id.drug_details_view);
        webView.loadUrl(this.url);
    }

    @Override
    public void onBackPressed() {
        backToList();
    }


    private void backToList() {
        Intent mainIntent = new Intent(ctx, DrugListActivity.class);
        startActivity(mainIntent);
    }
}
