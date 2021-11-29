package com.example.thehabitcontroller_project.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thehabitcontroller_project.Community.User;
import com.example.thehabitcontroller_project.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    final String TAG="LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle("Sign In");
        }
        TextView tvRegister = findViewById(R.id.tvRegister);
        tvRegister.setText(HtmlCompat.fromHtml("Don't have an account? " +
                "<a href=\"#\">Register</a>",HtmlCompat.FROM_HTML_MODE_COMPACT));
        Button btnLogin = findViewById(R.id.btnLogin);
        TextInputLayout tiEmail = findViewById(R.id.tiLoginEmail);
        TextInputLayout tiPass = findViewById(R.id.tiLoginPassword);
        btnLogin.setOnClickListener(view -> {
            btnLogin.setEnabled(false);
            ProgressBar pb=findViewById(R.id.pbLogin);
            pb.setVisibility(View.VISIBLE);
            String strEmail,strPass;
            strEmail= Objects.requireNonNull(tiEmail.getEditText()).getText().toString();
            strPass= Objects.requireNonNull(tiPass.getEditText()).getText().toString();
            if (strEmail.isEmpty()||strPass.isEmpty()){
                Toast.makeText(getBaseContext(), "Email and password cannot be empty.", Toast.LENGTH_SHORT).show();
                pb.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
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
            Intent intent = new Intent(this,RegisterActivity.class);
            startActivity(intent);
        });
    }

    /**
     * {@inheritDoc}
     * <p>
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"LoginActivity resumed.");
        if (User.getCurrentUser()!=null){
            finish();
        }
    }
}