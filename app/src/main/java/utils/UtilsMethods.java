package utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class UtilsMethods {

    private static  DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
    private static SimpleDateFormat  sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);

    public static int getNumberOfDays(String time) {
        LocalDateTime now = LocalDateTime.now();
        String nowString = dtf.format(now).toString();
        Date firstDate = null;
        Date secondDate = null;
        try {
            firstDate = sdf.parse(nowString);
            secondDate = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
        long seconds = diffInMillies / 1000;
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        return day;
    }
}
