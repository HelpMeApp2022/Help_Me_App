package com.example.helpmeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //Declaring variables
    private TextView settings;

    private Button trial; //TO DELETE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hiding action bar on homepage PARSKI LI VILLAIN
        Objects.requireNonNull(getSupportActionBar()).hide();

        //On click action on settings
        settings = findViewById(R.id.settings);
        settings.setOnClickListener(v -> openSettingsInterface());

       //TO DELETE
        // JUST FOR CHECKING REGISTER INTERFACE
        trial = findViewById(R.id.trial);
        trial.setOnClickListener(v -> checkRegisterPage());
        //TO DELETE

    }

    //TO DELETE
    private void checkRegisterPage() {
        Intent intent = new Intent(this, RegisterPage.class);
        startActivity(intent);
    } //TO DELETE

    //SetOnClick listener function from above
    private void openSettingsInterface() {
        Intent intent = new Intent(this, SettingsPage.class);
        startActivity(intent);
    }
}