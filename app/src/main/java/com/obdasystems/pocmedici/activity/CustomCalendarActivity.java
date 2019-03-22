package com.obdasystems.pocmedici.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import com.obdasystems.pocmedici.R;

import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.vo.DateData;

public class CustomCalendarActivity extends AppCompatActivity {

    MCalendarView calendarView;
    TextView dateDisplay;
    DateData selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_calendar);

        calendarView = (MCalendarView) findViewById(R.id.customCalendarView);
        dateDisplay = (TextView) findViewById(R.id.custom_date_display);
        dateDisplay.setText("Date: ");

        calendarView.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {
                calendarView.getMarkedDates().removeAdd();
                calendarView.markDate(date);
                selectedDate = date;
                dateDisplay.setText("Date: " + selectedDate.getDay()+ "  " + selectedDate.getMonthString() + "  " + selectedDate.getYear());
            }
        });




    }
}
