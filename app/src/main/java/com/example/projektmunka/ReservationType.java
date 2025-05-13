package com.example.projektmunka;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ReservationType extends AppCompatActivity {

    private String filmId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.type_reservation);

        filmId = getIntent().getStringExtra("filmId");

        Button reserveButton = findViewById(R.id.reserveButton);
        Button buyButton = findViewById(R.id.buyButton);

        reserveButton.setOnClickListener(v -> {
            Toast.makeText(this, "Reserving seats for film ID: " + filmId, Toast.LENGTH_SHORT).show();
            // Add logic to go to reservation screen or handle reservation
        });

        buyButton.setOnClickListener(v -> {
            Toast.makeText(this, "Buying seats for film ID: " + filmId, Toast.LENGTH_SHORT).show();
            // Add logic to go to purchase screen or handle purchase
        });
    }
}
