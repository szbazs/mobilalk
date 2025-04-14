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

public class LoginPage extends AppCompatActivity {

    private EditText editTextLoginUsername;
    private EditText editTextLoginPassword;
    private Button buttonLogin;
    private Button buttonRegister;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_login);

        mAuth = FirebaseAuth.getInstance();

        editTextLoginUsername = findViewById(R.id.editTextLoginUsername);
        editTextLoginPassword = findViewById(R.id.editTextLoginPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextLoginUsername.getText().toString();
                String password = editTextLoginPassword.getText().toString();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginPage.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d("LoginPage", "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(LoginPage.this, "Sikeres bejelentkezés!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginPage.this, ShopListActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Log.w("LoginPage", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(LoginPage.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginPage.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
