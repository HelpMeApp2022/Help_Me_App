package com.example.helpmeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class Test_location extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationClient;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_location);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        textView=findViewById(R.id.locationText);
    }

    public void showLocation(View view) {

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){

                //get the location
                fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        if (location!=null){
                            double lat=location.getLatitude();
                            double longti= location.getLongitude();
                            textView.setText("latitude: "+lat +"Longitude: " +longti);
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