package com.obdasystems.pocmedici.utils;

import java.util.Calendar;

public class TimeUtils {

    public static String getSimpleDateStringRepresentation(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year+"/"+month+"/"+day;
    }

}
