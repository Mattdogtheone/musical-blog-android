package com.example.musicalblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerEmail, registerPassword1, registerPassword2;
    Button registerButton;
    TextView singIn;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        registerButton = findViewById(R.id.registerbutton);
        registerEmail = findViewById(R.id.reg_editEmail);
        registerPassword1 = findViewById(R.id.reg_editPassword1);
        registerPassword2 = findViewById(R.id.reg_editPassword2);
        singIn = findViewById(R.id.signin);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Regisztráció");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        progressDialog = new ProgressDialog(this);

        singIn.setOnClickListener((v) -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = registerEmail.getText().toString().trim();
                String password = "";
                if (registerPassword1.getText().toString().equals(registerPassword2.getText().toString())) {
                    password = registerPassword1.getText().toString();
                } else {
                    registerPassword1.setError("A jelszó nem egyezik");
                    registerPassword2.setError("A jelszó nem egyezik");
                }
                if (TextUtils.isEmpty(email)) {
                    registerEmail.setError("Az email üres");
                } else if (TextUtils.isEmpty(password)) {
                    registerPassword1.setError("A jelszó üres");
                    registerPassword2.setError("A jelszó üres");
                } else {
                    register(email, password);
                }

            }
        });
    }

    private void register(String email, String password) {
        progressDialog.setTitle("Percek kérdése");
        progressDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    startActivity(new Intent(RegisterActivity.this, BlogActivity.class));
                    Toast.makeText(RegisterActivity.this, "Regisztráció sikeres", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Regisztráció nem sikerült", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}