package com.example.projektmunka;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.Button;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Html;
import android.view.LayoutInflater;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.ArrayList;

public class ReservationsMenu extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    private LinearLayout reservationsListLayout;
    private TextView emptyReservationsMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservations_menu);

        navbar.setupNavbar(this);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Kérlek jelentkezz be hoggy lást a foglalásaid.", Toast.LENGTH_LONG).show();
            Intent loginIntent = new Intent(this, LoginPage.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        db = FirebaseFirestore.getInstance();

        reservationsListLayout = findViewById(R.id.reservationsListLayout);
        emptyReservationsMessage = findViewById(R.id.emptyReservationsMessage);

        fetchUserReservations();
    }

    private void fetchUserReservations() {
        String userEmail = currentUser.getEmail();

        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Email nem létezik/nem elérhető", Toast.LENGTH_SHORT).show();
            emptyReservationsMessage.setVisibility(View.VISIBLE);
            return;
        }

        db.collection("reservations")
                .whereEqualTo("email", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            emptyReservationsMessage.setVisibility(View.GONE);
                            displayReservations(querySnapshot.getDocuments());
                        } else {
                            emptyReservationsMessage.setVisibility(View.VISIBLE);
                            reservationsListLayout.removeAllViews();
                        }
                    } else {
                        Toast.makeText(this, "Error.", Toast.LENGTH_SHORT).show();
                        emptyReservationsMessage.setVisibility(View.VISIBLE);
                        reservationsListLayout.removeAllViews();
                    }
                });
    }

    private void displayReservations(List<DocumentSnapshot> documents) {
        reservationsListLayout.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(this);

        for (DocumentSnapshot document : documents) {
            Reservation reservation = document.toObject(Reservation.class);
            String documentId = document.getId();

            if (reservation != null) {
                View reservationItemView = inflater.inflate(R.layout.list_item_reservation, reservationsListLayout, false);

                TextView reservationTextView = reservationItemView.findViewById(R.id.reservationDetailsTextView);
                Button deleteButton = reservationItemView.findViewById(R.id.deleteReservationButton);
                Button modifyButton = reservationItemView.findViewById(R.id.modifyReservationButton);

                // Include the date in the HTML string
                String reservationDetailsHtml = "Film: <b>" + reservation.getMovieTitle() + "</b>" +
                        "<br>Dátum: <b>" + (reservation.getDate() != null ? reservation.getDate() : "N/A") + "</b>" + // Added date
                        "<br>Idő: <b>" + reservation.getTime() + "</b>" +
                        "<br>Székek: <b>" + (reservation.getSeatNumbers() != null ? String.join(", ", reservation.getSeatNumbers()) : "N/A") + "</b>" +
                        "<br>Név: <b>" + (reservation.getName() != null ? reservation.getName() : "N/A") + "</b>";
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    reservationTextView.setText(Html.fromHtml(reservationDetailsHtml, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    reservationTextView.setText(Html.fromHtml(reservationDetailsHtml));
                }


                deleteButton.setOnClickListener(v -> deleteReservation(documentId));


                modifyButton.setOnClickListener(v -> {
                    Intent modifyIntent = new Intent(ReservationsMenu.this, ModifyReservation.class);
                    modifyIntent.putExtra("reservationId", documentId);
                    startActivity(modifyIntent);
                });

                reservationsListLayout.addView(reservationItemView);
            }
        }
    }

    private void deleteReservation(String documentId) {
        db.collection("reservations").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Foglalás törölve.", Toast.LENGTH_SHORT).show();
                    fetchUserReservations();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Hiba a törléskor.", Toast.LENGTH_SHORT).show();
                });
    }
}
