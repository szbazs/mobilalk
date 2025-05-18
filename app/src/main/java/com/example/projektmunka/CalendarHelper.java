package com.example.projektmunka;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.CalendarContract;
import android.widget.Toast;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarHelper {

    private static final String TAG = "CalendarHelper";
    private static final int PERMISSION_REQUEST_CALENDAR = 200;

    public static void addReservationToCalendar(Activity activity, String title, String time, List<String> seats) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_CALENDAR}, PERMISSION_REQUEST_CALENDAR);
            Log.d(TAG, "Requesting Calendar permission.");
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Calendar beginCal = Calendar.getInstance();
            Calendar endCal = Calendar.getInstance();

            Date showtimeDate = sdf.parse(time);
            beginCal.setTime(showtimeDate);
            endCal.setTime(showtimeDate);

            endCal.add(Calendar.HOUR_OF_DAY, 2);

            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginCal.getTimeInMillis())
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endCal.getTimeInMillis())
                    .putExtra(CalendarContract.Events.TITLE, title)
                    .putExtra(CalendarContract.Events.DESCRIPTION, "Foglalás: " + String.join(", ", seats))
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, "Mozi helyszín (opcionális)")
                    .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_TENTATIVE);

            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivity(intent);
            } else {
                Toast.makeText(activity, "Nincs naptár alkalmazás telepítve.", Toast.LENGTH_SHORT).show();
            }


        } catch (ParseException e) {
            Toast.makeText(activity, "Hiba a naptár esemény létrehozásakor.", Toast.LENGTH_SHORT).show();
        }
    }

    public static void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CALENDAR) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, "Naptár engedély megadva. Kérjük, próbálja újra a foglalást a naptárhoz adáshoz.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Naptár engedély megtagadva. Az esemény nem adható hozzá a naptárhoz.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
