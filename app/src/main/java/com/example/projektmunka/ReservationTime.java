package com.example.projektmunka;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar; // Import Calendar

public class ReservationTime {

    public static class Times {

        private static final List<String> SHOWTIMES = new ArrayList<>();

        static {
            SHOWTIMES.add("10:00");
            SHOWTIMES.add("12:30");
            SHOWTIMES.add("15:00");
            SHOWTIMES.add("17:45");
            SHOWTIMES.add("19:00");
            SHOWTIMES.add("20:30");
            SHOWTIMES.add("21:15");
            SHOWTIMES.add("22:00");
        }

        public static List<String> getShowtimes() {
            return SHOWTIMES;
        }
    }

    public static class Dates {

        private static final Calendar CURRENT_CALENDAR = Calendar.getInstance();

        public static Calendar getCurrentCalendar() {
            return CURRENT_CALENDAR;
        }

    }
}
