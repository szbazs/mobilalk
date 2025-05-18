package com.example.projektmunka;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;


public class popupMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.popup_menu);

        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int screenWidth = displayMetrics.widthPixels;

            int menuWidth = screenWidth;

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = menuWidth;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.gravity = Gravity.START;

            window.setAttributes(layoutParams);

        }

        Button logoutButton = findViewById(R.id.menu_logout_button);
        Button reservationsButton = findViewById(R.id.menu_reservations_button);
        Button filmListButton = findViewById(R.id.menu_film_list_button);


        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        reservationsButton.setOnClickListener(v -> {
            Intent reservationsIntent = new Intent(this, ReservationsMenu.class);
            startActivity(reservationsIntent);
            finish();
        });

        filmListButton.setOnClickListener(v -> {
            Intent filmListIntent = new Intent(this, FilmList.class);
            startActivity(filmListIntent);
            finish();
        });

        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            rootView.setOnClickListener(v -> {
                finish();
            });
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
