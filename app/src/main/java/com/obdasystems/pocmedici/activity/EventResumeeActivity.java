package com.obdasystems.pocmedici.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.obdasystems.pocmedici.R;

import java.util.Calendar;

public class EventResumeeActivity extends AppCompatActivity {

    private String title;
    private String description;
    private int type;

    private int year;
    private int month;
    private String monthName;
    private int day;
    private String dayName;
    private int hour;
    private int minutes;


    private Toolbar toolbar;

    private TextView titleTextView;
    private TextView dateTextView;
    private TextView noteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_resumee);

        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        description = intent.getStringExtra("description");
        type = intent.getIntExtra("type",-1);

        year = intent.getIntExtra("year",-1);
        month = intent.getIntExtra("month",-1);
        monthName = intent.getStringExtra("monthName");
        day = intent.getIntExtra("day",-1);
        dayName = intent.getStringExtra("dayName");
        hour = intent.getIntExtra("hour",-1);
        minutes = intent.getIntExtra("minutes",-1);

        titleTextView = (TextView)  findViewById(R.id.eventTitleText);
        titleTextView.setText(title);
        dateTextView = (TextView)  findViewById(R.id.eventDateText);
        dateTextView.setText(getTimestampString());
        noteTextView = (TextView)  findViewById(R.id.eventNotesText);
        noteTextView.setText(description);

        toolbar = (Toolbar) findViewById(R.id.event_resumee_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToCalendar();
            }
        });

    }

    @Override
    public void onBackPressed() {
        backToCalendar();
    }

    private String getTimestampString() {
        String result = dayName + " " + day + " " + monthName ;
        Calendar cal = Calendar.getInstance();
        if(cal.get(Calendar.YEAR)!=year) {
            result += " " + year;
        }

        if(hour>0 && minutes>0) {
            result += " - " + hour + ":" + minutes;
        }
        return result;
    }

    private void backToCalendar() {
        Intent calIntent = new Intent(this, CalendarActivity.class);
        startActivity(calIntent);
    }

}
