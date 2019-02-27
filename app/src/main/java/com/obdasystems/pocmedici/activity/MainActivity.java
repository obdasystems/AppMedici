package com.obdasystems.pocmedici.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.obdasystems.pocmedici.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void cardViewSelected(View view) {
        Class activityClass;

        switch (view.getId()) {
            case R.id.card_ctcae_form:
            case R.id.card_sensors:
            case R.id.card_messages:
            case R.id.card_calendar:
            case R.id.card_negative_event:
            case R.id.card_prescriptions:
            case R.id.card_user_profile:
            case R.id.card_settings:
            default:
                activityClass = FormListActivity.class;
                break;
        }

        Intent intent = new Intent(this, activityClass);
        intent.putExtra("name", "Bravo!!!");
        startActivity(intent);
    }
}
