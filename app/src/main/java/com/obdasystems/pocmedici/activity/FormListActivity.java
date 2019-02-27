package com.obdasystems.pocmedici.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.obdasystems.pocmedici.R;

public class FormListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, getIntent().getStringExtra("name"), Toast.LENGTH_SHORT).show();
    }
}
