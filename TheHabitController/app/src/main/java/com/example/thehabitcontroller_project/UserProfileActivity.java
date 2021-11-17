package com.example.thehabitcontroller_project;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        EditText editUserName = findViewById(R.id.editUserName);
        editUserName.setText(User.getCurrentUser().getUserName());
        Button btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editUserName.getText().toString().length()>5){
                    User.setUserName(editUserName.getText().toString());
                } else {
                    Toast.makeText(getBaseContext(),
                            "User name cannot be less than 5 characters.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}