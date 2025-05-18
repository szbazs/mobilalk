package com.example.projektmunka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.LayoutInflater;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.FirebaseApp;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FilmList extends AppCompatActivity {
    private static final String LOG_TAG = FilmList.class.getName();
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_film);

        navbar.setupNavbar(this);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user!");
            Toast.makeText(this, "Hitelesített felhasználó!", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(LOG_TAG, "Unauthenticated user!");
            Toast.makeText(this, "Nem hitelesített felhasználó!", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        fetchMovies();
    }

    private void fetchMovies() {
        String apiKey = "d38192976abfe099330634d8e1b58f00";
        String urlString = "https://api.themoviedb.org/3/movie/popular?api_key=" + apiKey;

        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray results = jsonObject.getJSONArray("results");

                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    mainHandler.post(() -> {
                        LinearLayout moviesLayout = findViewById(R.id.moviesLayout);

                        LayoutAnimationController controller =
                                AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);

                        moviesLayout.setLayoutAnimation(controller);

                        moviesLayout.removeAllViews();

                        LayoutInflater inflater = LayoutInflater.from(this);

                        for (int i = 0; i < results.length(); i++) {
                            JSONObject movie = results.optJSONObject(i);
                            if (movie != null) {
                                String title = movie.optString("title");
                                String overview = movie.optString("overview");
                                String posterPath = movie.optString("poster_path");
                                String filmId = movie.optString("id");

                                LinearLayout movieItem = (LinearLayout) inflater.inflate(R.layout.list_item_movie, moviesLayout, false);

                                ImageView imageView = movieItem.findViewById(R.id.moviePosterImageView);
                                TextView titleView = movieItem.findViewById(R.id.movieTitleTextView);
                                Button reserveButton = movieItem.findViewById(R.id.reserveButton);

                                titleView.setText(title);
                                loadPosterImage(imageView, posterPath);

                                reserveButton.setOnClickListener(v -> {
                                    Intent intent = new Intent(FilmList.this, ReservationType.class);
                                    intent.putExtra("filmId", filmId);
                                    startActivity(intent);
                                });

                                moviesLayout.addView(movieItem);
                            }
                        }
                    });

                } else {
                    Log.e(LOG_TAG, "HTTP error code: " + responseCode);
                }


            } catch (Exception e) {
                Log.e(LOG_TAG, "Exception: ", e);
            }
        }).start();
    }


    private void loadPosterImage(ImageView imageView, String posterPath) {
        new Thread(() -> {
            try {
                String fullUrl = "https://image.tmdb.org/t/p/w500" + posterPath;
                URL url = new URL(fullUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);

                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> imageView.setImageBitmap(bitmap));

            } catch (Exception e) {
                Log.e(LOG_TAG, "Image loading error: ", e);
            }
        }).start();
    }
}
