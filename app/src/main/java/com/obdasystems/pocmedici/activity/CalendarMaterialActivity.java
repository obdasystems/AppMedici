package com.obdasystems.pocmedici.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.utils.DateUtils;
import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.calendar.material.DrawableUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class CalendarMaterialActivity extends AppCompatActivity {

    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_material);
        ctx = this;

        Toolbar toolbar = (Toolbar) findViewById(R.id.calendar_material_toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_black_24dp);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(ctx, MainActivity.class);
                startActivity(mainIntent);
            }
        });

        List<EventDay> events = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        events.add(new EventDay(calendar, DrawableUtils.getCircleDrawableWithText(this, "M")));

        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.DAY_OF_MONTH, 2);
        events.add(new EventDay(calendar1, R.drawable.sample_icon_2));

        Calendar calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.DAY_OF_MONTH, 5);
        events.add(new EventDay(calendar2, R.drawable.sample_icon_3));

        Calendar calendar3 = Calendar.getInstance();
        calendar3.add(Calendar.DAY_OF_MONTH, 7);
        events.add(new EventDay(calendar3, R.drawable.sample_four_icons));

        Calendar calendar4 = Calendar.getInstance();
        calendar4.add(Calendar.DAY_OF_MONTH, 13);
        events.add(new EventDay(calendar4, DrawableUtils.getThreeDots(this)));

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarMaterialView);

        Calendar min = Calendar.getInstance();
        min.add(Calendar.MONTH, -120);

        Calendar max = Calendar.getInstance();
        max.add(Calendar.MONTH, 120);

        calendarView.setMinimumDate(min);
        calendarView.setMaximumDate(max);

        calendarView.setEvents(events);

        calendarView.setDisabledDays(getDisabledDays());

        calendarView.setOnDayClickListener(eventDay ->
                Toast.makeText(getApplicationContext(),
                        eventDay.getCalendar().getTime().toString() + " "
                                + eventDay.isEnabled(),
                        Toast.LENGTH_SHORT).show());


    }

    private List<Calendar> getDisabledDays() {
        Calendar firstDisabled = DateUtils.getCalendar();
        firstDisabled.add(Calendar.DAY_OF_MONTH, 2);

        Calendar secondDisabled = DateUtils.getCalendar();
        secondDisabled.add(Calendar.DAY_OF_MONTH, 1);

        Calendar thirdDisabled = DateUtils.getCalendar();
        thirdDisabled.add(Calendar.DAY_OF_MONTH, 18);

        List<Calendar> calendars = new ArrayList<>();
        calendars.add(firstDisabled);
        calendars.add(secondDisabled);
        calendars.add(thirdDisabled);
        return calendars;
    }

    private Calendar getRandomCalendar() {
        Random random = new Random();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, random.nextInt(99));

        return calendar;
    }
}

