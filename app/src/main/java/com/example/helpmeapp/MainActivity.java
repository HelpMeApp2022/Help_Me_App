package com.example.helpmeapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //Declaring variables
    private TextView settings;
    Button btnVoice;

    private FirebaseAuth firebaseAuth;

    //testing sms
    String phone = "57604994"; //you can put any target phone number here to test
    String mes = "www.facebook.com";
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //hiding action bar on homepage
        Objects.requireNonNull(getSupportActionBar()).hide();

        //On click action on settings
        settings = findViewById(R.id.settings);
        settings.setOnClickListener(v -> openSettingsInterface());

        //initialising button voice
        btnVoice = findViewById(R.id.VoiceButton);

        firebaseAuth = FirebaseAuth.getInstance();

        // Setting OnClickListener to button Voice
        btnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }

    //Dialog for voice
    private void openDialog(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, 200);
    }

    //Speech to text from voice dialog
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK){
            ArrayList<String> arrayList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            //capturing full sentence in single array index 0
            String voice = arrayList.get(0);
            //splitting the sentence and add words one by one into array of Strings 'words'
            String[] words = voice.split(" ");
            ArrayList<String> s = new ArrayList<>();
            String voice1=words[0];
            String voice2=words[1];
            switch (voice1) {
                case "call":
                    getNumber(voice2);
                    break;
            }
           // Toast.makeText(this,voice1+voice2,Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(this,"Something went wrong",Toast.LENGTH_SHORT).show();
        }
    }

    //Getting the number from phone contact list to be called (retrieved by name)
    public void getNumber(String name){
        String number="";

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},0);
        }

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection    = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int idxName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int idxNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        // calling different services
        switch (name){
            case "police":
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+112));
                startActivity(callIntent);
                //Toast.makeText(this,"Please press on call",Toast.LENGTH_SHORT).show();
                break;
            case "poppy":
                Intent callIntent1 = new Intent(Intent.ACTION_CALL);
                callIntent1.setData(Uri.parse("tel:"+115));
                startActivity(callIntent1);
                break;
            case "ambulance":
                Intent callIntent3 = new Intent(Intent.ACTION_CALL);
                callIntent3.setData(Uri.parse("tel:"+114));
                startActivity(callIntent3);
                break;
            default:
                Toast.makeText(this, "Did not capture! Try-Again!!", Toast.LENGTH_SHORT).show();
                openDialog();
        }

        if(cursor.moveToFirst()) {

            do {
               String contactName   = cursor.getString(idxName);
               String contactNumber = cursor.getString(idxNumber);

                if (contactName.equals(name)){
                    //checking if permission has been granted by user else ask for permission
                   if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                       ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},100);
                    }
                   //calling the person
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+contactNumber));
                    startActivity(callIntent);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    //SetOnClick listener function from above
    private void openSettingsInterface() {
        Intent intent = new Intent(this, SettingsPage.class);
        startActivity(intent);
    }

    //Function for one-time login
    @Override
    protected void onStart(){
        super.onStart();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser!=null){
            //There is a user logged in
            Toast.makeText(this, "Welcome to HelpMeApp!", Toast.LENGTH_SHORT).show();
        } else {
            //No one is logged in
            startActivity(new Intent(this, RegisterPage.class));
            finish();
        }
    }

    //Logout function
    public void logout(View view) {
        firebaseAuth.signOut();
        Toast.makeText(this, "Logged out of HelpMeApp!", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(MainActivity.this, RegisterPage.class);
        //Clear stack to prevent user coming back to MainPage Activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); //Close mainpage activity
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
                                smsManager.sendTextMessage("57604994", null, constructMessage, null, null);
                                //
                                Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
                            } catch (Exception ex) {
                                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
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