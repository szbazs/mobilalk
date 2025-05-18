package com.example.projektmunka;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

import com.example.projektmunka.Film;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ReservationType extends AppCompatActivity {

    private String filmId;
    private FirebaseFirestore db;

    private ImageView filmPosterImageView;
    private TextView filmTitleTextView;
    private Button reserveButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.type_reservation);

        db = FirebaseFirestore.getInstance();

        filmPosterImageView = findViewById(R.id.filmPosterImageView);
        filmTitleTextView = findViewById(R.id.filmTitleTextView);
        reserveButton = findViewById(R.id.reserveButton);

        backButton = findViewById(R.id.backButton);

        filmId = getIntent().getStringExtra("filmId");

        if (filmId != null) {
            fetchFilmDetails(filmId);
        } else {
            filmTitleTextView.setText("Film részletek nem elérhetőek.");
        }

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(ReservationType.this, FilmList.class);
            startActivity(intent);
            finish();
        });

        reserveButton.setOnClickListener(v -> {
            Intent intent = new Intent(ReservationType.this, ReservationTimeActivity.class);
            intent.putExtra("filmId", filmId);
            intent.putExtra("reservationType", "reservation");
            startActivity(intent);
        });

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
                                    loadPosterImage(filmPosterImageView, film.getImageUrl());
                                });
                            } else {
                                filmTitleTextView.setText("Film részletek nem elérhetőek.");
                            }
                        } else {
                            filmTitleTextView.setText("Film nem található.");
                        }
                    } else {
                        filmTitleTextView.setText("Hiba.");
                    }
                });
    }

    private void loadPosterImage(ImageView imageView, String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            imageView.setImageResource(R.drawable.ic_launcher_background);
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream input = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(input);

                    if (bitmap != null) {
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(() -> {
                            imageView.setImageBitmap(bitmap);
                        });
                    } else {
                        Handler mainHandler = new Handler(Looper.getMainLooper());
                        mainHandler.post(() -> imageView.setImageResource(R.drawable.ic_launcher_background));
                    }
                    input.close();
                } else {
                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(() -> imageView.setImageResource(R.drawable.ic_launcher_background));
                }
                connection.disconnect();

            } catch (Exception e) {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> imageView.setImageResource(R.drawable.ic_launcher_background));
            }
        }).start();
    }
}
