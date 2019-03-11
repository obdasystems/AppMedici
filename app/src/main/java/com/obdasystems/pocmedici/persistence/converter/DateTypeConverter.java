package com.obdasystems.pocmedici.persistence.converter;

import android.arch.persistence.room.TypeConverter;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateTypeConverter {

    @TypeConverter
    public static GregorianCalendar calendarFromTimestamp(String value) {
        if(value == null) {
            return null;
        }
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(Long.parseLong(value));
        return cal;
    }

    @TypeConverter
    public static String calendarToTimestamp(GregorianCalendar cal) {
        if(cal == null) {
            return null;
        }
        return "" + cal.getTimeInMillis();
    }
}
