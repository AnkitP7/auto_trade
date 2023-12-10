package com.kite.automation;

import java.util.Calendar;

public class Utils {
    public static float getTickPrice(float price)
    {
        int price1 = Math.round(price*100);
        float price2 = Math.round(price1/5)*5;
        float price3 = price2/100;
        return price3;
    }

    public static int getDayOfWeek(String dayOfWeek)
    {
        if(dayOfWeek.toLowerCase().startsWith("mon"))
        {
            return Calendar.MONDAY;
        } else if(dayOfWeek.toLowerCase().startsWith("tue"))
        {
            return Calendar.TUESDAY;
        } else if(dayOfWeek.toLowerCase().startsWith("wed"))
        {
            return Calendar.WEDNESDAY;
        } else if(dayOfWeek.toLowerCase().startsWith("thu"))
        {
            return Calendar.THURSDAY;
        } else if(dayOfWeek.toLowerCase().startsWith("fri"))
        {
            return Calendar.FRIDAY;
        } else if(dayOfWeek.toLowerCase().startsWith("sat"))
        {
            return Calendar.SATURDAY;
        } else if(dayOfWeek.toLowerCase().startsWith("sun")) {
            return Calendar.SUNDAY;
        }
        return -1;
    }
}
