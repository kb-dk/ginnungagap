package dk.kb.ginnungagap.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for dates and calendar methods.
 * Tries to use the RFC 1123 format for datetime representation. 
 */
public class CalendarUtils {
    /**
     * Retrieves the current date format as 
     * @return The text for now.
     */
    public static String nowToText() {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
        return date.format(formatter);
    }
}
