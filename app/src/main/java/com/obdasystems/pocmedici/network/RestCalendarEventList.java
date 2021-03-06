package com.obdasystems.pocmedici.network;

import java.util.ArrayList;
import java.util.List;

public class RestCalendarEventList {
    private List<RestCalendarEvent> events;

    public RestCalendarEventList() {
        this.events = new ArrayList<>();
    }

    public RestCalendarEventList(List<RestCalendarEvent> events) {
        this.events = events;
    }

    public List<RestCalendarEvent> getEvents() {
        return events;
    }

    public void setEvents(List<RestCalendarEvent> events) {
        this.events = events;
    }
}
