package com.example.helpmeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.Objects;

public class RegisterPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        //hiding action bar on homepage PARSKI LI VILLAIN
        Objects.requireNonNull(getSupportActionBar()).hide();

        //Shrishti editing
    }
}