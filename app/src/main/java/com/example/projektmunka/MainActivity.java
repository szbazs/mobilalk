package com.example.projektmunka;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";

    private EditText userNameEditText;
    private EditText userEmailEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;
    private Button registerButton;
    private Button loginButton; // Declare loginButton

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(MainActivity.this, FilmList.class);
            startActivity(intent);
            finish();
            return;
        }

        mAuth = FirebaseAuth.getInstance();

        userNameEditText = findViewById(R.id.editTextUsername);
        userEmailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        passwordConfirmEditText = findViewById(R.id.editTextConfirmPassword);
        registerButton = findViewById(R.id.register);
        loginButton = findViewById(R.id.login);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginPage.class);
                startActivity(intent);
            }
        });
    }


    private void registerUser() {
        final String userName = userNameEditText.getText().toString().trim();
        final String email = userEmailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String passwordConfirm = passwordConfirmEditText.getText().toString().trim();

        if (userName.isEmpty() || email.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            Toast.makeText(this, "Kérjük, töltsön ki minden mezőt", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(passwordConfirm)) {
            Toast.makeText(this, "A jelszavak nem egyeznek", Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, "Nem egyenlő a jelszó és a megerősítése.");
            return;
        }

        Log.i(LOG_TAG, "Regisztrált: " + userName + ", e-mail: " + email);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(LOG_TAG, "User created successfully");
                            Toast.makeText(MainActivity.this, "Sikeres regisztráció!", Toast.LENGTH_SHORT).show();

                        } else {
                            Log.d(LOG_TAG, "User wasn't created successfully");
                            Toast.makeText(MainActivity.this, "Registration failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
