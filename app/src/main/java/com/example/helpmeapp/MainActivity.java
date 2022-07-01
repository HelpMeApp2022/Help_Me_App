package com.example.helpmeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //Declaring variables
    private TextView settings;
    Button btnVoice, object;

    private FirebaseAuth firebaseAuth; //database connection
    private String textEmergency1, textEmergency2, textEmergency3, textRelation1,textRelation2,textRelation3;
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

        //On click action on object detection
        object = findViewById(R.id.ObjectButton);
        object.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openObjectDetectionInterface();
            }
        });

        //initialising button voice
        btnVoice = findViewById(R.id.VoiceButton);

        firebaseAuth = FirebaseAuth.getInstance();

        // Setting OnClickListener to button Voice
        btnVoice.setOnClickListener(v -> openDialog());

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        showNumbers(firebaseUser);

    }
    //SetOnClick listener function from above:
    private void openSettingsInterface() {
        Intent intent = new Intent(this, SettingsPage.class);
        startActivity(intent);
    }

    private void openObjectDetectionInterface() {
        Intent intent = new Intent(this, ObjectDetectionPage.class);
        startActivity(intent);
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
        String[] projection    = new String[]
                {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        //int idxName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        //int idxNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

        // calling different services
        switch (name){
            case "police":
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+112));
                startActivity(callIntent);
                //Toast.makeText(this,"Please press on call",Toast.LENGTH_SHORT).show();
                break;
            case "Firefighter":
                Intent callIntent1 = new Intent(Intent.ACTION_CALL);
                callIntent1.setData(Uri.parse("tel:"+115));
                startActivity(callIntent1);
                break;
            case "ambulance":
                Intent callIntent3 = new Intent(Intent.ACTION_CALL);
                callIntent3.setData(Uri.parse("tel:"+114));
                startActivity(callIntent3);
                break;
//            case "emergency":
//                Intent callIntent4 = new Intent(Intent.ACTION_CALL);
//                callIntent4.setData(Uri.parse("tel:"+textEmergency1));
//                startActivity(callIntent4);
//                break;

//            default:
//               Toast.makeText(this, "Did not capture! Try-Again!!", Toast.LENGTH_SHORT).show();
//                openDialog();
        }

        if(cursor.moveToFirst()) {

            do {
                //String contactName   = cursor.getString(idxName);
                //String contactNumber = cursor.getString(idxNumber);

               //Fetch details from settings page and associate with variables
               String contactName1   = textRelation1;
               String contactNumber1 = textEmergency1;
               String contactName2   = textRelation2;
               String contactNumber2 = textEmergency2;
               String contactName3   = textRelation3;
               String contactNumber3 = textEmergency3;


                if (contactName1.equals(name)){
                    //checking if permission has been granted by user else ask for permission
                   if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                       ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},100);
                    }
                   //calling the emergency contact 1
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+contactNumber1));
                    startActivity(callIntent);
                }else if (contactName2.equals(name)){
                    //checking if permission has been granted by user else ask for permission
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},100);
                    }
                    //calling the emergency contact 2
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+contactNumber2));
                    startActivity(callIntent);
                }else if (contactName3.equals(name)) {
                    //checking if permission has been granted by user else ask for permission
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 100);
                    }
                    //calling the emergency contact 3
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + contactNumber3));
                    startActivity(callIntent);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
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

    private void showNumbers(FirebaseUser firebaseUser) {
        String userIDofRegistered = firebaseUser.getUid();

        //Extracting user reference from db for "Help Me App Users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Help Me App Users");

        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInfo userInfo = snapshot.getValue(userInfo.class);
                if (userInfo !=null) {
                    textEmergency1 = userInfo.emergencyContact1;
                    textEmergency2 = userInfo.emergencyContact2;
                    textEmergency3 = userInfo.emergencyContact3;
                    textRelation1 = userInfo.relation1;
                    textRelation2 = userInfo.relation2;
                    textRelation3 = userInfo.relation3;

                } else {
                    Toast.makeText(MainActivity.this, "Something went wrong! Please try once again.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Something went wrong! Please try once again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    //function get triggered when sos button is pressed
    public void sos(View view) {
        Sms_and_location();
    }

    public void Sms_and_location(){

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){

            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){

                //get the location
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {

                    if (location!=null){
                        double latitude=location.getLatitude();
                        double longitude= location.getLongitude();
                        String str_lat=Double.toString(latitude);
                        String str_long=Double.toString(longitude);
                        String constructMessage= "I am in an EMERGENCY situation!!! " + "My location on map is: " +
                                "www.google.com/maps/place/"+str_lat+","+str_long;
                        String contactPerson1 = textEmergency1;
                        String contactPerson2 = textEmergency2;
                        String contactPerson3 = textEmergency3;

                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(contactPerson1, null, constructMessage, null, null);
                            smsManager.sendTextMessage(contactPerson2, null, constructMessage, null, null);
                            smsManager.sendTextMessage(contactPerson3, null, constructMessage, null, null);

                            Toast.makeText(getApplicationContext(), "Emergency Message Sent!!", Toast.LENGTH_LONG).show();
                        } catch (Exception ex) {
                            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                            ex.printStackTrace();
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