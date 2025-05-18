package com.example.projektmunka;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.DatePicker;

import com.example.projektmunka.Film;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.core.app.ActivityCompat;


import com.example.projektmunka.CalendarHelper;
import com.example.projektmunka.NotificationHelper;


public class ReservationTimeActivity extends AppCompatActivity {

    private String filmId;
    private String reservationType;
    private String selectedShowtime;
    private Calendar selectedDateCalendar;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private TextView filmTitleTextView;
    private Spinner showtimeSpinner;
    private Spinner numberOfSeatsSpinner;
    private GridLayout seatGridLayout;
    private Button backButton;
    private Button reserveSeatsButton;
    private EditText reservationNameEditText;

    private Button selectDateButton;
    private TextView selectedDateTextView;


    private List<View> seatViews;
    private List<View> selectedSeats;
    private Set<String> reservedSeatNames;

    private int selectedNumberOfSeats = 1;

    private static final int GRID_SIZE = 15;
    private static final int SEAT_SIZE_DP = 30;
    private static final int SEAT_MARGIN_DP = 4;
    private static final float CORNER_RADIUS_DP = 4;
    private static final int SEAT_PADDING_DP = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_time);

        NotificationHelper.createNotificationChannel(this);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Please log in to make a reservation.", Toast.LENGTH_LONG).show();
            Intent loginIntent = new Intent(this, LoginPage.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        filmTitleTextView = findViewById(R.id.filmTitleTextView);
        showtimeSpinner = findViewById(R.id.showtimeSpinner);
        numberOfSeatsSpinner = findViewById(R.id.numberOfSeatsSpinner);
        seatGridLayout = findViewById(R.id.seatGridLayout);
        backButton = findViewById(R.id.backButton);
        reserveSeatsButton = findViewById(R.id.reserveSeatsButton);
        reservationNameEditText = findViewById(R.id.reservationNameEditText);

        selectDateButton = findViewById(R.id.selectDateButton);
        selectedDateTextView = findViewById(R.id.selectedDateTextView);

        selectedDateCalendar = Calendar.getInstance();
        updateDateTextView();

        seatViews = new ArrayList<>();
        selectedSeats = new ArrayList<>();
        reservedSeatNames = new HashSet<>();

        Intent intent = getIntent();
        filmId = intent.getStringExtra("filmId");
        reservationType = intent.getStringExtra("reservationType");

        if (filmId != null) {
            fetchFilmDetails(filmId);
        } else {
            filmTitleTextView.setText("Film Details Not Available");
        }

        setupShowtimeSpinner();
        setupNumberOfSeatsSpinner();
        setupSeatGrid();

        if (filmId != null) {
            if (showtimeSpinner.getAdapter() != null && showtimeSpinner.getSelectedItem() != null) {
                selectedShowtime = showtimeSpinner.getSelectedItem().toString();
            }
            fetchReservedSeats();
        }

        selectDateButton.setOnClickListener(v -> showDatePickerDialog());


        backButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(ReservationTimeActivity.this, ReservationType.class);
            backIntent.putExtra("filmId", filmId);
            startActivity(backIntent);
            finish();
        });

        reserveSeatsButton.setOnClickListener(v -> {
            saveReservationToFirestore();
        });
    }

    private void showDatePickerDialog() {
        int year = selectedDateCalendar.get(Calendar.YEAR);
        int month = selectedDateCalendar.get(Calendar.MONTH);
        int day = selectedDateCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    selectedDateCalendar.set(year1, month1, dayOfMonth);
                    updateDateTextView();
                    fetchReservedSeats();
                },
                year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.show();
    }

    private void updateDateTextView() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        selectedDateTextView.setText(dateFormat.format(selectedDateCalendar.getTime()));
    }


    private void fetchFilmDetails(String id) {
        db.collection("films").document(id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Film film = document.toObject(Film.class);
                            if (film != null) {
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    filmTitleTextView.setText(film.getTitle());
                                });
                            } else {
                                filmTitleTextView.setText("Film Details Not Available");
                            }
                        } else {
                            filmTitleTextView.setText("Film Details Not Available");
                        }
                    } else {
                        Toast.makeText(this, "Error.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupShowtimeSpinner() {
        List<String> showtimes = ReservationTime.Times.getShowtimes();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, showtimes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        showtimeSpinner.setAdapter(adapter);

        showtimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedShowtime = parent.getItemAtPosition(position).toString();
                fetchReservedSeats();
                resetSeatSelection();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupNumberOfSeatsSpinner() {
        List<String> seatOptions = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            seatOptions.add(String.valueOf(i));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, seatOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numberOfSeatsSpinner.setAdapter(adapter);

        numberOfSeatsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedNumberOfSeats = Integer.parseInt(parent.getItemAtPosition(position).toString());
                resetSeatSelection();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupSeatGrid() {
        seatGridLayout.removeAllViews();
        seatViews.clear();

        int seatSizePx = dpToPx(SEAT_SIZE_DP);
        int seatMarginPx = dpToPx(SEAT_MARGIN_DP);
        float cornerRadiusPx = dpToPx(CORNER_RADIUS_DP);
        int seatPaddingPx = dpToPx(SEAT_PADDING_DP);

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                TextView seatView = new TextView(this);

                char rowChar = (char) ('A' + row);
                int colNum = col + 1;
                String seatName = String.valueOf(rowChar) + colNum;

                seatView.setText(seatName);
                seatView.setGravity(Gravity.CENTER);
                seatView.setTextColor(Color.BLACK);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = seatSizePx;
                params.height = seatSizePx;
                params.rowSpec = GridLayout.spec(row);
                params.columnSpec = GridLayout.spec(col);
                params.setMargins(seatMarginPx, seatMarginPx, seatMarginPx, seatMarginPx);
                seatView.setLayoutParams(params);

                seatView.setPadding(seatPaddingPx, seatPaddingPx, seatPaddingPx, seatPaddingPx);

                GradientDrawable drawable = new GradientDrawable();
                drawable.setShape(GradientDrawable.RECTANGLE);
                drawable.setCornerRadius(cornerRadiusPx);

                if (reservedSeatNames.contains(seatName)) {
                    drawable.setColor(Color.RED);
                    seatView.setClickable(false);
                } else {
                    drawable.setColor(Color.GREEN);
                    seatView.setClickable(true);
                    seatView.setOnClickListener(this::onSeatClick);
                }

                seatView.setBackground(drawable);
                seatView.setTag(seatName);

                seatGridLayout.addView(seatView);
                seatViews.add(seatView);
            }
        }
    }

    private void fetchReservedSeats() {
        if (filmTitleTextView.getText() == null || selectedShowtime == null || selectedDateCalendar == null) {
            return;
        }

        String movieTitle = filmTitleTextView.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String selectedDate = dateFormat.format(selectedDateCalendar.getTime());


        db.collection("reservations")
                .whereEqualTo("movieTitle", movieTitle)
                .whereEqualTo("time", selectedShowtime)
                .whereEqualTo("date", selectedDate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reservedSeatNames.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Reservation reservation = document.toObject(Reservation.class);
                            if (reservation != null && reservation.getSeatNumbers() != null) {
                                reservedSeatNames.addAll(reservation.getSeatNumbers());
                            }
                        }
                        setupSeatGrid();
                    } else {
                        Toast.makeText(this, "Error loading reserved seats.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void onSeatClick(View view) {
        String seatName = (String) view.getTag();

        if (reservedSeatNames.contains(seatName)) {
            return;
        }

        GradientDrawable drawable = (GradientDrawable) view.getBackground();

        if (selectedSeats.contains(view)) {
            selectedSeats.remove(view);
            drawable.setColor(Color.GREEN);
        } else {
            if (selectedSeats.size() < selectedNumberOfSeats) {
                selectedSeats.add(view);
                drawable.setColor(Color.parseColor("#FFA500"));
            } else {
                Toast.makeText(this, "Összeses csak " + selectedNumberOfSeats + " széket válaszhatsz.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void resetSeatSelection() {
        selectedSeats.clear();
        setupSeatGrid();
    }

    private int dpToPx(float dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void saveReservationToFirestore() {
        String selectedTime = selectedShowtime;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String selectedDate = dateFormat.format(selectedDateCalendar.getTime());


        List<String> selectedSeatNames = new ArrayList<>();
        for (View seatView : selectedSeats) {
            if (seatView instanceof TextView) {
                String seatName = ((TextView) seatView).getText().toString();
                if (!reservedSeatNames.contains(seatName)) {
                    selectedSeatNames.add(seatName);
                } else {
                    Toast.makeText(this, "Egg vagy többb kiválasztott szék már foglalt.", Toast.LENGTH_SHORT).show();
                    resetSeatSelection();
                    return;
                }
            }
        }

        String reservationName = reservationNameEditText.getText().toString().trim();

        if (reservationName.isEmpty()) {
            Toast.makeText(this, "Kérlek add meg a foglaló nevét.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedSeatNames.isEmpty()) {
            Toast.makeText(this, "Kérlek legalább egy széket válassz.", Toast.LENGTH_SHORT).show();
            return;
        }

        String movieTitle = filmTitleTextView.getText().toString();

        String userEmail = "Unknown Email";
        if (currentUser != null && currentUser.getEmail() != null) {
            userEmail = currentUser.getEmail();
        } else {
            Toast.makeText(this, "Autetikációs hiba.", Toast.LENGTH_LONG).show();
            return;
        }

        Reservation reservation = new Reservation(selectedDate, selectedTime, movieTitle, selectedSeatNames, reservationName, userEmail);
        reservation.setFilmId(filmId);

        CollectionReference reservationsCollection = db.collection("reservations");

        reservationsCollection.add(reservation)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Sikeres foglalás!", Toast.LENGTH_SHORT).show();
                    NotificationHelper.showReservationSuccessNotification(this);

                    CalendarHelper.addReservationToCalendar(this, movieTitle, selectedDate + " " + selectedTime, selectedSeatNames);


                    Intent intent = new Intent(ReservationTimeActivity.this, FilmList.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Hiba a foglaláskor.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        CalendarHelper.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }
}
