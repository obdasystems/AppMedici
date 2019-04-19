package com.obdasystems.pocmedici.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.obdasystems.pocmedici.R;
import com.obdasystems.pocmedici.network.MediciApi;
import com.obdasystems.pocmedici.network.MediciApiClient;
import com.obdasystems.pocmedici.network.NetworkUtils;
import com.obdasystems.pocmedici.network.RestCalendarEvent;
import com.obdasystems.pocmedici.network.RestCalendarEventList;
import com.obdasystems.pocmedici.utils.DateUtils;
import com.obdasystems.pocmedici.utils.DrawableUtils;
import com.obdasystems.pocmedici.utils.SaveSharedPreference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarActivity extends AppActivity {
    private CalendarView calendarView;
    private Map<Long, RestCalendarEvent> timestampToEvent = new HashMap<>();
    private List<EventDay> eventDays = new LinkedList<>();
    private int recursiveCallCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_material);

        Toolbar toolbar = find(R.id.calendar_material_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> backToMain());

        calendarView = find(R.id.calendar_material_view);

        Calendar min = Calendar.getInstance();
        min.add(Calendar.MONTH, -120);

        Calendar max = Calendar.getInstance();
        max.add(Calendar.MONTH, 120);

        calendarView.setMinimumDate(min);
        calendarView.setMaximumDate(max);

        getCalendarEvents();

        calendarView.setOnDayClickListener(eventDay -> {
            if (eventDays.contains(eventDay)) {
                launchEventIntent(eventDay);
            } else {
                snack(calendarView, "No events on this date");
                Calendar getCalendar = eventDay.getCalendar();
                Log.i(tag(), "getCalendar= " + getCalendar.toString());
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
        eventIntent.putExtra("title", event.getTitle());
        eventIntent.putExtra("description", event.getDescription());
        eventIntent.putExtra("type", event.getType());
        int year = cal.get(Calendar.YEAR);
        eventIntent.putExtra("year", year);
        int month = cal.get(Calendar.MONTH);
        eventIntent.putExtra("month", month);
        String monthName = getMonthString(month);
        eventIntent.putExtra("monthName", monthName);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        eventIntent.putExtra("day", day);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        String dayName = getDayString(dayOfWeek);
        eventIntent.putExtra("dayName", dayName);
        int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
        eventIntent.putExtra("hourOfDay", hourOfDay);
        int minutes = cal.get(Calendar.MINUTE);
        eventIntent.putExtra("minutes", minutes);

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

    private void addEventsToCalendar(RestCalendarEventList eventList) {
        for (RestCalendarEvent event : eventList.getEvents()) {
            Calendar cal = Calendar.getInstance();
            // Round to day as it is done in EventDay anyway
            // FIXME: use a data structure that allows to have more events per day
            cal.setTimeInMillis(event.getTimestamp());
            DateUtils.setMidnight(cal);
            timestampToEvent.put(cal.getTimeInMillis(), event);
            EventDay ed = new EventDay(cal,
                    DrawableUtils.getCircleDrawableWithText(this,
                            event.getType().substring(0, 1).toUpperCase()));
            eventDays.add(ed);
        }
        calendarView.setEvents(eventDays);
    }

    private void backToMain() {
        Intent mainIntent = new Intent(context(), MainActivity.class);
        startActivity(mainIntent);
    }

    private void getCalendarEvents() {
        if (recursiveCallCounter < 15) {
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
                        recursiveCallCounter = 0;
                    } else {
                        switch (response.code()) {
                            case 401:
                                NetworkUtils.requestNewAuthorizationToken(pwd, usr, context());
                                Log.e(tag(), "Unable to fetch calendar events (401)");
                                if (!SaveSharedPreference.getAuthorizationIssue(context())) {
                                    getCalendarEvents();
                                } else {
                                    String issueDescription = SaveSharedPreference.getAuthorizationIssueDescription(context());
                                    toast("Unable to fetch calendar events (401) [" + issueDescription + "]");
                                    Log.e(tag(), "[" + this.getClass().getSimpleName() + "] Unable to fetch calendar events (401) [" + issueDescription + "]");
                                }
                                break;
                            case 404:
                                Log.e(tag(), "Unable to fetch calendar events (404)");
                                toast("Unable to fetch calendar events (404)");
                                break;
                            case 500:
                                Log.e(tag(), "Unable to fetch calendar events (500)");
                                toast("Unable to fetch calendar events (500)");
                                break;
                            default:
                                Log.e(tag(), "Unable to fetch calendar events (UNKNOWN)");
                                toast("Unable to fetch calendar events (UNKNOWN)");
                                break;
                        }
                    }
                }

                @Override
                public void onFailure(Call<RestCalendarEventList> call, Throwable t) {
                    Log.e(tag(), "Unable to fetch calendar events: " + t.getMessage());
                    Log.e(tag(), "Unable to fetch calendar events: " + t.getStackTrace());
                    toast("Unable to fetch calendar events..");
                }
            });
        } else {
            Log.e(tag(), "Max number of calls to getCalendarEvents() reached!!");
            toast("Max number of calls to getCalendarEvents() reached!!");
            recursiveCallCounter = 0;
        }
    }

}

