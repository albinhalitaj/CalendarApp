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

public class RegisterActivity extends AppCompatActivity {

    EditText username,email,password;
    Button register,login;
    DbOpenHelper dbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register = findViewById(R.id.register);
        login = findViewById(R.id.login);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        register.setOnClickListener(v -> {
           if (RegisterUser()) {
               LoginActivity();
           }
        });

        login.setOnClickListener(v -> LoginActivity());
    }

    private void LoginActivity(){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

    private Boolean RegisterUser(){
        dbOpenHelper = new DbOpenHelper(this);
        String username = this.username.getText().toString().trim();
        String password = this.password.getText().toString().trim();
        String email = this.email.getText().toString().trim();
        Boolean status = false;

        if (username.isEmpty() || password.isEmpty() || email.isEmpty()){
            Toast.makeText(this,"Please fill all the fields!",Toast.LENGTH_SHORT).show();
            status = false;
        }else{
            if (dbOpenHelper.checkUser(username)){
                Toast.makeText(this,"Username Already Exists!",Toast.LENGTH_SHORT).show();
                status = false;
            }else{
                Boolean result = dbOpenHelper.insertUser(username,password,email);
                if (result){
                    Toast.makeText(this,"Register Success!",Toast.LENGTH_SHORT).show();
                    status = true;
                }
            }
        }
        return status;
    }
}