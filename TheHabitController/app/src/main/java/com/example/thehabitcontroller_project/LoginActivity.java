package com.example.thehabitcontroller_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.text.HtmlCompat;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    final String TAG="LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle(getString(R.string.app_name));
            getSupportActionBar().setSubtitle("Sign In");
        }
        TextView tvRegister = findViewById(R.id.tvRegister);
        tvRegister.setText(HtmlCompat.fromHtml("Don't have an account? " +
                "<a href=\"#\">Register</a>",HtmlCompat.FROM_HTML_MODE_COMPACT));
        Button btnLogin = findViewById(R.id.btnLogin);
        TextInputLayout tiEmail = findViewById(R.id.tiLoginEmail);
        TextInputLayout tiPass = findViewById(R.id.tiLoginPassword);
        btnLogin.setOnClickListener(view -> {
            ProgressBar pb=findViewById(R.id.pbLogin);
            pb.setVisibility(View.VISIBLE);
            String strEmail,strPass;
            strEmail= Objects.requireNonNull(tiEmail.getEditText()).getText().toString();
            strPass= Objects.requireNonNull(tiPass.getEditText()).getText().toString();
            if (strEmail.isEmpty()||strPass.isEmpty()){
                Toast.makeText(getBaseContext(), "Email and password cannot be empty.", Toast.LENGTH_SHORT).show();
            } else {
                User.login(strEmail, strPass, u -> {
                    Log.d(TAG,"Auth Complete");
                    if (u!=null){
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), "Login Failed.", Toast.LENGTH_SHORT).show();
                    }
                    pb.setVisibility(View.GONE);
                });
            }
        });
        tvRegister.setOnClickListener(view -> {
            // Register activity
        });
    }
}