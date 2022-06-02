package com.example.helpmeapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //Declaring variables
    private TextView settings;

    private Button trial; //TO DELETE

    //testing sms
    String phone = "58806717"; //you can put any target phone number here to test
    String mes = "www.facebook.com";
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
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

        //convert latitude and longitude to string to send


        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            //
            Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }

    }

    //function get triggered when sos button is pressed

    public void sos(View view) {
        Sms_and_location();
    }






    public void Sms_and_location(){

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){

                //get the location
                fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location!=null){
                            double latitude=location.getLatitude();
                            double longitude= location.getLongitude();
                            String str_lat=Double.toString(latitude);
                            String str_long=Double.toString(longitude);
                            String constructMessage= "I am in an Emergency situation " + "My location on map is: " +
                                    "www.google.com/maps/place/"+str_lat+","+str_long;

                            try {
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage("58806717", null, constructMessage, null, null);
                                //
                                Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
                            } catch (Exception ex) {
                                Toast.makeText(getApplicationContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                                ex.printStackTrace();
                            }

                        }
                    }
                });


            }
            else{
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

            }

        }

    }
}