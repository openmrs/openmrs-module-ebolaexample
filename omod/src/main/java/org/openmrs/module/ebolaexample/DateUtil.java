package org.openmrs.module.ebolaexample;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static boolean isInLast24Hours(Date date) {
        long diff = differenceInDays(date, new Date());
        return diff<1;
    }

    private static long differenceInDays(Date date1, Date date2){
        long diff = date2.getTime() - date1.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000);
        return diffDays;
    }

}
