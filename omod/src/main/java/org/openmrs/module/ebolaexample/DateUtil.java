package org.openmrs.module.ebolaexample;

import org.openmrs.DrugOrder;
import org.openmrs.util.OpenmrsUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    public static Date mostRecentChange(DrugOrder order) {
        return max(order.getDateActivated(), actualStopDate(order), order.getDateChanged());
    }

    public static Date mostRecentChange(List<DrugOrder> orders) {
        Date mostRecent = null;
        for (DrugOrder drugOrder : orders) {
            mostRecent = max(mostRecent, mostRecentChange(drugOrder));
        }
        return mostRecent;
    }

    private static Date max(Date... dates) {
        Date max = null;
        for (Date date : dates) {
            if (date == null) {
                continue;
            }
            if (max == null || date.compareTo(max) > 0) {
                max = date;
            }
        }
        return max;
    }

    /**
     * The date that a drug order was actually stopped, or null if it hasn't stopped yet. Specifically, if an order has
     * an autoExpireDate in the future, but hasn't been stopped/expired yet, this method will return null.
     * @param order
     * @return The date that a drug order was actually stopped
     */
    public static Date actualStopDate(DrugOrder order) {
        if (order.getDateStopped() != null) {
            return order.getDateStopped();
        }
        if (order.getAutoExpireDate() != null && OpenmrsUtil.compare(order.getAutoExpireDate(), new Date()) < 0) {
            return order.getAutoExpireDate();
        }
        return null;
    }

    public static Date getDateToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
