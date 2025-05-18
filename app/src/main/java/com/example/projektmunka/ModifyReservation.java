package com.example.projektmunka;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ModifyReservation extends AppCompatActivity {

    private static final String TAG = "ModifyReservation";

    private String reservationId;
    private FirebaseFirestore db;
    private DocumentReference reservationRef;

    private TextView modifyMovieTitleTextView;
    private Spinner modifyShowtimeSpinner;
    private TextView modifySeatNumbersTextView;
    private EditText modifyReservationNameEditText;
    private Button modifyBackButton;
    private Button modifySaveButton;

    private String selectedShowtime;
    private List<String> currentSeatNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_reservation);

        db = FirebaseFirestore.getInstance();

        modifyMovieTitleTextView = findViewById(R.id.modifyMovieTitleTextView);
        modifyShowtimeSpinner = findViewById(R.id.modifyShowtimeSpinner);
        modifySeatNumbersTextView = findViewById(R.id.modifySeatNumbersTextView);
        modifyReservationNameEditText = findViewById(R.id.modifyReservationNameEditText);
        modifyBackButton = findViewById(R.id.modifyBackButton);
        modifySaveButton = findViewById(R.id.modifySaveButton);

        Intent intent = getIntent();
        reservationId = intent.getStringExtra("reservationId");

        if (reservationId == null) {
            Toast.makeText(this, "Nincs megadva foglalási azonosító.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        reservationRef = db.collection("reservations").document(reservationId);

        fetchReservationDetails();
        setupSpinners();

        modifyBackButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(ModifyReservation.this, ReservationsMenu.class);
            startActivity(backIntent);
            finish();
        });

        modifySaveButton.setOnClickListener(v -> {
            saveModifiedReservation();
        });
    }

    private void fetchReservationDetails() {
        reservationRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Reservation reservation = document.toObject(Reservation.class);
                            if (reservation != null) {
                                displayReservationDetails(reservation);
                            } else {
                                Toast.makeText(this, "Nem található foglalási adat.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        } else {
                            Toast.makeText(this, "Nem található foglalás.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } else {
                        Log.w(TAG, "Error fetching reservation details: ", task.getException());
                        Toast.makeText(this, "Hiba a foglalási részletek betöltésekor.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void displayReservationDetails(Reservation reservation) {
        modifyMovieTitleTextView.setText(reservation.getMovieTitle());
        modifyReservationNameEditText.setText(reservation.getName());

        List<String> showtimes = ReservationTime.Times.getShowtimes();
        ArrayAdapter<String> showtimeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, showtimes);
        showtimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modifyShowtimeSpinner.setAdapter(showtimeAdapter);

        int showtimePosition = showtimeAdapter.getPosition(reservation.getTime());
        if (showtimePosition >= 0) {
            modifyShowtimeSpinner.setSelection(showtimePosition);
            selectedShowtime = reservation.getTime();
        }

        currentSeatNumbers = reservation.getSeatNumbers();
        if (currentSeatNumbers != null && !currentSeatNumbers.isEmpty()) {
            modifySeatNumbersTextView.setText(String.join(", ", currentSeatNumbers));
        } else {
            modifySeatNumbersTextView.setText("N/A");
        }

        List<String> seatOptions = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            seatOptions.add(String.valueOf(i));
        }
        ArrayAdapter<String> numberOfSeatsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, seatOptions);
        numberOfSeatsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        int currentNumberOfSeats = (currentSeatNumbers != null) ? currentSeatNumbers.size() : 0;
        int numberOfSeatsPosition = numberOfSeatsAdapter.getPosition(String.valueOf(currentNumberOfSeats));
        if (numberOfSeatsPosition >= 0) {

        }

    }

    private void setupSpinners() {
        modifyShowtimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedShowtime = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    private void saveModifiedReservation() {
        String newName = modifyReservationNameEditText.getText().toString().trim();

        if (newName.isEmpty()) {
            Toast.makeText(this, "Kérjük, adja meg a foglaló nevét.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("time", selectedShowtime);
        updates.put("name", newName);


        reservationRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Foglalás sikeresen frissítve!", Toast.LENGTH_SHORT).show();
                    Intent backIntent = new Intent(ModifyReservation.this, ReservationsMenu.class);
                    startActivity(backIntent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error updating reservation: ", e);
                    Toast.makeText(this, "Hiba a foglalás frissítésekor.", Toast.LENGTH_SHORT).show();
                });
    }
}
