package com.obdasystems.pocmedici.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

import com.obdasystems.pocmedici.R;

public class DrugActivity extends AppActivity {
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drug);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setTitle(intent.getStringExtra("name"));
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> backToList());

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
        Intent mainIntent = new Intent(this, DrugListActivity.class);
        startActivity(mainIntent);
    }

}
