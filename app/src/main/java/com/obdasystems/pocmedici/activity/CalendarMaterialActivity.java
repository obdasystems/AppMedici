package com.obdasystems.pocmedici.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import java.util.GregorianCalendar;
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


        GregorianCalendar gregCal = new GregorianCalendar();
        gregCal.set(2019,Calendar.MARCH,30);
        Log.i("appMedici", "gregCal= "+gregCal.toString());
        events.add(new EventDay(gregCal, DrawableUtils.getCircleDrawableWithText(this, "Visit")));

        Calendar testCal = Calendar.getInstance();
        testCal.set(2019,Calendar.MARCH,14);
        Log.i("appMedici", "testCal= "+testCal.toString());
        EventDay ed = new EventDay(testCal, DrawableUtils.getCircleDrawableWithText(this, "V"));
        events.add(ed);

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

        //calendarView.setDisabledDays(getDisabledDays());

        calendarView.setOnDayClickListener(eventDay ->{
                if(events.contains(eventDay)) {
                    Toast.makeText(getApplicationContext(),
                            "there is an event on this date!!",
                            Toast.LENGTH_SHORT).show();
                    launchEventIntent(eventDay);
                }
                else {
                    Toast.makeText(getApplicationContext(),
                            "NO events on this date!!",
                            Toast.LENGTH_SHORT).show();
                    Calendar getCalendar = eventDay.getCalendar();
                    Log.i("appMedici", "getCalendar= "+getCalendar.toString());
                }
        });



    }

    private void launchEventIntent(EventDay eventDay) {
        Intent eventIntent = new Intent(this, EventResumeeActivity.class);
        Calendar cal = eventDay.getCalendar();
        eventIntent.putExtra("eventTitle","TITOLO EVENTO");
        eventIntent.putExtra("notes","NOTE EVENTO IN UFFICIO 18. PORTARE ANALISI DEL SANGUE");

        int year = cal.get(Calendar.YEAR);
        eventIntent.putExtra("year",year);
        int month = cal.get(Calendar.MONTH);
        eventIntent.putExtra("month",month);
        String monthName = getMonthString(month);
        eventIntent.putExtra("monthName",monthName);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        eventIntent.putExtra("day",day);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        String dayName = getDayString(dayOfWeek);
        eventIntent.putExtra("dayName",dayName);
        int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
        eventIntent.putExtra("hourOfDay",hourOfDay);
        int minutes = cal.get(Calendar.MINUTE);
        eventIntent.putExtra("minutes",minutes);

        startActivity(eventIntent);
    }

    private String getMonthString(int month) {
        String res = "";
        switch (month) {
            case Calendar.JANUARY:
                res = "January";
                break;
            case Calendar.FEBRUARY:
                res = "February";
                break;
            case Calendar.MARCH:
                res = "March";
                break;
            case Calendar.APRIL:
                res = "April";
                break;
            case Calendar.MAY:
                res = "May";
                break;
            case Calendar.JUNE:
                res = "June";
                break;
            case Calendar.JULY:
                res = "July";
                break;
            case Calendar.AUGUST:
                res = "August";
                break;
            case Calendar.SEPTEMBER:
                res = "September";
                break;
            case Calendar.OCTOBER:
                res = "October";
                break;
            case Calendar.NOVEMBER:
                res = "November";
                break;
            case Calendar.DECEMBER:
                res = "December";
                break;
        }
        return res;
    }

    private String getDayString(int day) {
        String res = "";
        switch (day) {
            case Calendar.MONDAY:
                res = "Monday";
                break;
            case Calendar.TUESDAY:
                res = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                res = "Wednesday";
                break;
            case Calendar.THURSDAY:
                res = "Thursday";
                break;
            case Calendar.FRIDAY:
                res = "Friday";
                break;
            case Calendar.SATURDAY:
                res = "Saturday";
                break;
            case Calendar.SUNDAY:
                res = "Sunday";
                break;
        }
        return res;
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

