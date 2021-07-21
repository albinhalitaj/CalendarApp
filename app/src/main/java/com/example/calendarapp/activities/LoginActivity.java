package com.example.calendarapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.calendarapp.R;
import com.example.calendarapp.data.DbOpenHelper;

public class LoginActivity extends AppCompatActivity {

    EditText username,password;
    Button login,register;
    DbOpenHelper dbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.loginBtn);
        register = findViewById(R.id.signup);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        login.setOnClickListener(v -> LoginUser());

        register.setOnClickListener(v -> RegisterActivity());
    }

    private void RegisterActivity(){
        Intent intent = new Intent(this,RegisterActivity.class);
        startActivity(intent);
    }

    private void LoginUser(){
        dbOpenHelper = new DbOpenHelper(this);
        String username = this.username.getText().toString().trim();
        String password = this.password.getText().toString().trim();
        Boolean result = dbOpenHelper.checkUserPass(username,password);
        if (result){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(this,"Wrong Username or Password",Toast.LENGTH_SHORT).show();
        }
    }
}