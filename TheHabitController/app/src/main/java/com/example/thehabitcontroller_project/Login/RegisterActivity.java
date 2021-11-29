package com.example.thehabitcontroller_project.Login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.thehabitcontroller_project.Community.User;
import com.example.thehabitcontroller_project.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    final String TAG="LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // configure support action bar
        if (getSupportActionBar()!=null){
            getSupportActionBar().setTitle(getString(R.string.app_name));
            getSupportActionBar().setSubtitle("Sign Up");
        }

        // bind all UI components
        TextInputLayout tiEmail = findViewById(R.id.tiRegEmail);
        TextInputLayout tiName = findViewById(R.id.tiRegName);
        TextInputLayout tiPass = findViewById(R.id.tiRegPass);
        Button btnSignUp= findViewById(R.id.btnSignUp);
        ProgressBar pbReg = findViewById(R.id.pbReg);

        btnSignUp.setOnClickListener(view -> {
            btnSignUp.setEnabled(false);
            // start loading spinner
            pbReg.setVisibility(View.VISIBLE);

            // get all field values in string
            String strEmail= Objects.requireNonNull(tiEmail.getEditText()).getText().toString();
            String strName= Objects.requireNonNull(tiName.getEditText()).getText().toString();
            String strPass= Objects.requireNonNull(tiPass.getEditText()).getText().toString();

            // validate
            if (strEmail.isEmpty()||
                    strPass.isEmpty()||
                    strName.isEmpty()||
                    !Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()){
                Toast.makeText(getBaseContext(), "Please fill in all fields.",
                        Toast.LENGTH_SHORT).show();
                pbReg.setVisibility(View.GONE);
                btnSignUp.setEnabled(true);
            } else {
                // backend registration process
                User.Register(strEmail, strName, strPass, u -> {
                    pbReg.setVisibility(View.GONE);
                    if (u==null){
                        Toast.makeText(getBaseContext(), "User registration failed.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        finish();
                    }
                });
            }
        });

    }
}