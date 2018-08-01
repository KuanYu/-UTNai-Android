package com.butions.utnai;

import android.text.format.DateUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Chalitta Khampachua on 04-Aug-17.
 */

public class CalendarTime {
    private String TAG = "CalendarTime";
    private static CalendarTime mInstance = null;

    protected CalendarTime() {

    }

    public static synchronized CalendarTime getInstance(){
        if(mInstance == null){
            mInstance = new CalendarTime();
        }
        return mInstance;
    }

    public long getCurrentTime() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        Log.d(TAG, "calendarTimeMillis :" + calendar.getTimeInMillis());
        return calendar.getTimeInMillis();
    }

    public String getCurrentDate() {
        java.util.Calendar now = java.util.Calendar.getInstance();
        int year = now.get(java.util.Calendar.YEAR);
        int month = now.get(java.util.Calendar.MONTH) + 1; // Note: zero based!
        int day = now.get(java.util.Calendar.DAY_OF_MONTH);
        int hour = now.get(java.util.Calendar.HOUR_OF_DAY);
        int minute = now.get(java.util.Calendar.MINUTE);
        int second = now.get(java.util.Calendar.SECOND);
        int millis = now.get(java.util.Calendar.MILLISECOND);
        return year + "-" + month + "-" + day + "T" + hour + ":" + minute + ":" + second + "+" + millis;
    }

    public String getCurrentDateText() {
        java.util.Calendar now = java.util.Calendar.getInstance();
        int year = now.get(java.util.Calendar.YEAR);
        int month = now.get(java.util.Calendar.MONTH) + 1; // Note: zero based!
        int day = now.get(java.util.Calendar.DAY_OF_MONTH);
        int hour = now.get(java.util.Calendar.HOUR_OF_DAY);
        int minute = now.get(java.util.Calendar.MINUTE);

        SimpleDateFormat month_date = new SimpleDateFormat("MMM", Locale.ENGLISH);
        now.set(Calendar.MONTH, month);
        String monthName = month_date.format(now.getTime());

        return  monthName + " " +  day + ", " + year +  "  " + hour + ":" + minute;
    }

    public String getCurrentOnlyDateText() {
        java.util.Calendar now = java.util.Calendar.getInstance();
        int year = now.get(java.util.Calendar.YEAR);
        int month = now.get(java.util.Calendar.MONTH) + 1; // Note: zero based!
        int day = now.get(java.util.Calendar.DAY_OF_MONTH);

        SimpleDateFormat month_date = new SimpleDateFormat("MMM", Locale.ENGLISH);
        now.set(Calendar.MONTH, month);
        String monthName = month_date.format(now.getTime());

        return  monthName + " " + day + ", " + year;
    }

    public String getCurrentTimeText() {
        java.util.Calendar now = java.util.Calendar.getInstance();
        int hour = now.get(java.util.Calendar.HOUR_OF_DAY);
        int minute = now.get(java.util.Calendar.MINUTE);

        return  hour + ":" + minute;
    }

    public String getTimeAgo(String inputTime){
        if(inputTime != null && !inputTime.equals("null")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+'SSS", Locale.ENGLISH);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                long time = sdf.parse(inputTime).getTime();
                long now = sdf.parse(getCurrentDate()).getTime();

                CharSequence ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
//            Log.d(TAG, "Time Ago : " + ago);
                return ago.toString();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
