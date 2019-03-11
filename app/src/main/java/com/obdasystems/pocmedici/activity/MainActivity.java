package com.obdasystems.pocmedici.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.persistence.database.FormQuestionnaireDatabase;

public class MainActivity extends AppCompatActivity {

    private final int MAIN_LOGIN_CODE = 10000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sp = getSharedPreferences("app_medici_login", MODE_PRIVATE);
        boolean logged = sp.getBoolean("logged",false);
        if(!logged) {
            Intent logIntent = new Intent(this,LoginActivity.class);
            startActivityForResult(logIntent, MAIN_LOGIN_CODE);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==MAIN_LOGIN_CODE){
            Toast.makeText(this,"Benvenuto!!! Bravo!!", Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
