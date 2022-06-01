package com.example.helpmeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //Declaring variables
    private TextView settings;

    private Button trial; //TO DELETE

    //testing sms
    String phone = "58806717"; //you can put any target phone number here to test
    String mes = "www.facebook.com";


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


    //Function for sending SMS

    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            //smsManager.sendTextMessage("59215712", null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }

    }

    //function get triggered when sos button is pressed

    public void sos(View view) {
        sendSMS(phone,mes);

    }
}