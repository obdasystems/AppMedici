package com.obdasystems.pocmedici.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.calendar.material.DrawableUtils;
import com.obdasystems.pocmedici.network.MediciApi;
import com.obdasystems.pocmedici.network.MediciApiClient;
import com.obdasystems.pocmedici.network.NetworkUtils;
import com.obdasystems.pocmedici.network.RestCalendarEvent;
import com.obdasystems.pocmedici.network.RestCalendarEventList;
import com.obdasystems.pocmedici.utils.SaveSharedPreference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarMaterialActivity extends AppCompatActivity {

    private Context ctx;

    private CalendarView calendarView;
    private int recursiveCallCounter = 0;

    private Map<Long, RestCalendarEvent> timestampToEvent = new HashMap<>();
    private List<EventDay> eventDays = new LinkedList<>();

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
                backToMain();
            }
        });

        /*List<EventDay> events = new ArrayList<>();


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
        calendarView.setEvents(events);*/


        calendarView = (CalendarView) findViewById(R.id.calendarMaterialView);

        Calendar min = Calendar.getInstance();
        min.add(Calendar.MONTH, -120);

        Calendar max = Calendar.getInstance();
        max.add(Calendar.MONTH, 120);

        calendarView.setMinimumDate(min);
        calendarView.setMaximumDate(max);


        getCalendarEvents();


        calendarView.setOnDayClickListener(eventDay ->{
                if(eventDays.contains(eventDay)) {
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

    @Override
    public void onBackPressed() {
        backToMain();
    }

    private void launchEventIntent(EventDay eventDay) {
        Calendar cal = eventDay.getCalendar();
        Long timestamp = cal.getTimeInMillis();
        RestCalendarEvent event = this.timestampToEvent.get(timestamp);

        Intent eventIntent = new Intent(this, EventResumeeActivity.class);
        eventIntent.putExtra("title",event.getTitle());
        eventIntent.putExtra("description",event.getDescription());
        eventIntent.putExtra("type",event.getType());
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

    private void getCalendarEvents() {

        if(recursiveCallCounter<15) {
            recursiveCallCounter++;
            String usr = "james";
            String pwd = "bush";

            String authorizationToken = SaveSharedPreference.getAuthorizationToken(this);
            if (authorizationToken == null) {
                NetworkUtils.requestNewAuthorizationToken(pwd, usr, this);
            }
            authorizationToken = SaveSharedPreference.getAuthorizationToken(this);

            MediciApi apiService = MediciApiClient.createService(MediciApi.class, authorizationToken);

            Call<RestCalendarEventList> call = apiService.getCalendarEvents();
            call.enqueue(new Callback<RestCalendarEventList>() {
                @Override
                public void onResponse(Call<RestCalendarEventList> call, Response<RestCalendarEventList> response) {

                    if (response.isSuccessful()) {
                        addEventsToCalendar(response.body());
                        recursiveCallCounter=0;
                    } else {
                        switch (response.code()) {
                            case 401:
                                NetworkUtils.requestNewAuthorizationToken(pwd, usr, ctx);
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch calendar events (401)");
                                if (!SaveSharedPreference.getAuthorizationIssue(ctx)) {
                                    getCalendarEvents();
                                } else {
                                    String issueDescription = SaveSharedPreference.getAuthorizationIssueDescription(ctx);
                                    Toast.makeText(getApplicationContext(), "Unable to fetch calendar events (401) [" + issueDescription + "]", Toast.LENGTH_LONG).show();
                                    Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch calendar events (401) [" + issueDescription + "]");
                               }
                                break;
                            case 404:
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch calendar events (404)");
                                Toast.makeText(getApplicationContext(), "Unable to fetch calendar events (404)", Toast.LENGTH_LONG).show();
                                break;
                            case 500:
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch calendar events (500)");
                                Toast.makeText(getApplicationContext(), "Unable to fetch calendar events (500)", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch calendar events (UNKNOWN)");
                                Toast.makeText(getApplicationContext(), "Unable to fetch calendar events (UNKNOWN)", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                }

                @Override
                public void onFailure(Call<RestCalendarEventList> call, Throwable t) {
                    Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch calendar events: " + t.getMessage());
                    Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Unable to fetch calendar events: " + t.getStackTrace());
                    Toast.makeText(getApplicationContext(), "Unable to fetch calendar events..", Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            Log.e("appMedici", "[" + this.getClass().getSimpleName() + "] Max number of calls to getCalendarEvents() reached!!");
            Toast.makeText(getApplicationContext(), "Max number of calls to getCalendarEvents() reached!!", Toast.LENGTH_LONG).show();
            recursiveCallCounter=0;
        }
    }

    private void addEventsToCalendar(RestCalendarEventList eventList) {

        for(RestCalendarEvent event:eventList.getEvents()) {
            timestampToEvent.put(event.getTimestamp(),event);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(event.getTimestamp());
            EventDay ed = new EventDay(cal, DrawableUtils.getCircleDrawableWithText(this, "V"));
            eventDays.add(ed);
            //events.add(new EventDay(calendar1, R.drawable.sample_icon_2));
        }
        calendarView.setEvents(eventDays);
    }

    private void backToMain() {
        Intent mainIntent = new Intent(ctx, MainActivity.class);
        startActivity(mainIntent);
    }
}

