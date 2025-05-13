package com.example.projektmunka;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class navbar {

    public static void setupNavbar(Activity activity) {
        Button logoutButton = activity.findViewById(R.id.logout_button);

        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                activity.startActivity(new Intent(activity, LoginPage.class));
                activity.finish();
            });
        }
    }
}
