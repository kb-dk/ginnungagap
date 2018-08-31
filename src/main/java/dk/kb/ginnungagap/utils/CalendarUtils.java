package dk.kb.ginnungagap.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import dk.kb.ginnungagap.exception.ArgumentCheck;

/**
 * Utility class for dates and calendar methods.
 * Tries to use the RFC 1123 format for datetime representation. 
 */
public class CalendarUtils {
    /**
     * Retrieves the current date format as the ISO-8601 date time format.
     * @return The text for now.
     */
    public static String nowToText() {
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return date.format(formatter);        
    }
    
    /**
     * Retrieve the default text outprint of a date.
     * @param date The date to transform into text.
     * @return The date in text format.
     */
    public static String dateToText(Date date) {
        ArgumentCheck.checkNotNull(date, "Date date");
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss zzzz");
        return formatter.format(date);
    }
}
