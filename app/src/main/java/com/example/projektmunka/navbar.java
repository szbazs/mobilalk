package com.example.projektmunka;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button; // Keep Button import if you have other buttons
import android.widget.ImageButton; // Import ImageButton
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

public class navbar {

    public static void setupNavbar(Activity activity) {
        Toolbar toolbar = activity.findViewById(R.id.navbar);
        if (toolbar == null) {
            return;
        }

        ImageButton menuButton = toolbar.findViewById(R.id.menu_button);
        if (menuButton == null) {
            return;
        }

        menuButton.setOnClickListener(v -> {
            if (activity instanceof AppCompatActivity) {
                AppCompatActivity appCompatActivity = (AppCompatActivity) activity;
                ActionBar actionBar = appCompatActivity.getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setDisplayShowTitleEnabled(false);
                }
            }

            Intent intent = new Intent(activity, popupMenu.class);
            activity.startActivity(intent);
        });
    }
}
