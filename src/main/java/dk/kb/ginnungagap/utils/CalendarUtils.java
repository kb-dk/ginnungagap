package dk.kb.ginnungagap.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
}
