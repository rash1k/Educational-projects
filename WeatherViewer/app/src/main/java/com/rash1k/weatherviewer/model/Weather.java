package com.rash1k.weatherviewer.model;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class Weather {

    private static final String LOG_TAG = String.class.getSimpleName();
    public String dayOfWeek;
    public String minTemp;
    public String maxTemp;
    public String humidity;
    public String description;
    public String iconUrl;

    public Weather(long timeStamp, double minTemp, double maxTemp, double humidity,
                   String description, String iconName) {

//            NumberFormat для форматирования температуры в целое число
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(0);

        this.dayOfWeek = convertTimeStampToDay(timeStamp);
        this.minTemp = numberFormat.format(minTemp) + "\u2103";
        this.maxTemp = numberFormat.format(maxTemp) + "\u2103";
        this.humidity = NumberFormat.getPercentInstance().format(humidity / 100);
        this.description = description;
        this.iconUrl = "http://openweathermap.org/img/w/" + iconName + ".png";
    }

    private String convertTimeStampToDay(long timeStamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp * 1000);
        TimeZone tz = calendar.getTimeZone();
        calendar.add(Calendar.MILLISECOND, tz.getOffset(Calendar.DST_OFFSET));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

        return simpleDateFormat.format(calendar.getTime());
    }

}
