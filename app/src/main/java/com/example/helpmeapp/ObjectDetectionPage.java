package com.example.helpmeapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

public class ObjectDetectionPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_detection);

        //Action Bar
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#97d3dd"));

        //Set BackgroundDrawable
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setTitle("Object Detection Page");
        actionBar.setBackgroundDrawable(colorDrawable);
    }
}