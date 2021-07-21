package com.example.calendarapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.calendarapp.R;

public class WelcomeActivity extends AppCompatActivity {


    Button loginBtn,registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);

        loginBtn.setOnClickListener(v -> LoginActivity());

        registerBtn.setOnClickListener(v -> RegisterActivity());
    }

    private void LoginActivity(){
       Intent intent = new Intent(this,LoginActivity.class);
       startActivity(intent);
    }

    private void RegisterActivity(){
       Intent intent = new Intent(this,RegisterActivity.class);
       startActivity(intent);
    }
}