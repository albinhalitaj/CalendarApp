package com.example.calendarapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.example.calendarapp.R;
import com.example.calendarapp.views.CustomCalendarView;

public class MainActivity extends AppCompatActivity {

    CustomCalendarView customCalendarView;
    TextView username;
    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.userEvents);
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME,0);
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false);
        user = sharedPreferences.getString("user","user");
        if (isLoggedIn) {
            customCalendarView = findViewById(R.id.custom_calendar_view);
            username.setText(String.format("%s's Events", user));
        } else
        {
            startActivity(new Intent(MainActivity.this,WelcomeActivity.class));
            finish();
        }
    }
}