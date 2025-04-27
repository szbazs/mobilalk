package com.example.projektmunka;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.ViewGroup;
import android.view.Gravity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.os.Handler;
import android.os.Looper;

public class ShopListActivity extends AppCompatActivity {

    private static final String LOG_TAG = ShopListActivity.class.getName();
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user!");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }

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
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject movie = results.optJSONObject(i);
                        String title = movie.optString("title");
                        String overview = movie.optString("overview");
                        String posterPath = movie.optString("poster_path");

                        LinearLayout movieItem = new LinearLayout(this);
                        movieItem.setOrientation(LinearLayout.VERTICAL);
                        movieItem.setLayoutParams(new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        ));
                        movieItem.setPadding(0, 24, 0, 24);
                        movieItem.setGravity(Gravity.CENTER_HORIZONTAL);

                        ImageView imageView = new ImageView(this);
                        imageView.setLayoutParams(new ViewGroup.LayoutParams(500, 750));
                        loadPosterImage(imageView, posterPath);

                        TextView titleView = new TextView(this);
                        titleView.setLayoutParams(new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        ));
                        titleView.setText(title);
                        titleView.setTextSize(20);
                        titleView.setGravity(Gravity.CENTER_HORIZONTAL);
                        titleView.setPadding(0, 16, 0, 8);

                        TextView overviewView = new TextView(this);
                        overviewView.setLayoutParams(new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        ));
                        overviewView.setText(overview);
                        overviewView.setTextSize(14);
                        overviewView.setPadding(0, 8, 0, 8);

                        movieItem.addView(imageView);
                        movieItem.addView(titleView);
                        movieItem.addView(overviewView);
                        moviesLayout.addView(movieItem);
                    }
                });

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
