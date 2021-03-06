package com.example.helpmeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executor;

public class SettingsPage extends AppCompatActivity {

    //Declaring variables for fingerprint
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    ScrollView mMainLayout;

    //Declaring variables for editTexts and others
    private EditText updateFirstName, updateLastName, updateEmail, updateEmergency1, updateRelation1;
    private EditText updateEmergency2, updateRelation2, updateEmergency3, updateRelation3;
    private String textFirstName, textLastName, textEmail, textEmergency1, textRelation1;
    private String textEmergency2, textRelation2, textEmergency3, textRelation3, txtGender;

    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);
        mMainLayout = findViewById(R.id.main_layout);

        //Action Bar
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#97d3dd"));

        //Set BackgroundDrawable
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setTitle("Settings Page");
        actionBar.setBackgroundDrawable(colorDrawable);

        updateFirstName = findViewById(R.id.userFirstName);
        updateLastName = findViewById(R.id.userLastName);
        updateEmail = findViewById(R.id.userEmail);
        updateEmergency1 = findViewById(R.id.updateEmergencyNumber1);
        updateRelation1 = findViewById(R.id.updateRelationship1);
        updateEmergency2 = findViewById(R.id.updateEmergencyNumber2);
        updateRelation2 = findViewById(R.id.updateRelationship2);
        updateEmergency3 = findViewById(R.id.updateEmergencyNumber3);
        updateRelation3 = findViewById(R.id.updateRelationship3);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        //Show profile data
        showProfile(firebaseUser);

        //Update details
        Button save_button = findViewById(R.id.saveButton);
        save_button.setOnClickListener(v -> updateDetails(firebaseUser));

        //Fingerprint codes
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate())
        {
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(getApplicationContext(), "Device does not have any fingerprint sensors!", Toast.LENGTH_SHORT).show();
                break;

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(getApplicationContext(), "Fingerprint sensor not working!", Toast.LENGTH_SHORT).show();

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(getApplicationContext(), "No fingerprint assigned!", Toast.LENGTH_SHORT).show();
        }

        Executor executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(SettingsPage.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "Authentication Successful!", Toast.LENGTH_SHORT).show();
                mMainLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication Failed! Please try again!!", Toast.LENGTH_SHORT).show();
            }
        });

        //Prompting fingerprint part/interface
        promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("HelpMeApp Confirm Access")
                .setDescription("Touch the fingerprint sensor").setDeviceCredentialAllowed(true).build();
        biometricPrompt.authenticate(promptInfo);
    }

    //Update details function
    private void updateDetails(FirebaseUser firebaseUser) {

        if (TextUtils.isEmpty(textFirstName)){
            Toast.makeText(getApplicationContext(), "Please enter user's firstname!", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(textLastName)){
            Toast.makeText(getApplicationContext(), "Please enter user's lastname!", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(textEmergency1)){
            Toast.makeText(getApplicationContext(), "Please enter user's emergency contact 1!", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(textRelation1)){
            Toast.makeText(getApplicationContext(), "Please enter user's relationship to contact 1!", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(textEmergency2)){
            Toast.makeText(getApplicationContext(), "Please enter user's emergency contact 2!", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(textRelation2)){
            Toast.makeText(getApplicationContext(), "Please enter user's relationship to contact 2!", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(textEmergency3)){
            Toast.makeText(getApplicationContext(), "Please enter user's emergency contact 3!", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(textRelation3)){
            Toast.makeText(getApplicationContext(), "Please enter user's relationship to contact 3!", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            textFirstName = updateFirstName.getText().toString();
            textLastName = updateLastName.getText().toString();
            textEmail = updateEmail.getText().toString();
            textEmergency1 = updateEmergency1.getText().toString();
            textRelation1 = updateRelation1.getText().toString();
            textEmergency2 = updateEmergency2.getText().toString();
            textRelation2 = updateRelation2.getText().toString();
            textEmergency3 = updateEmergency3.getText().toString();
            textRelation3 = updateRelation3.getText().toString();

            //Enter data into firebase db
            userInfo userInfo = new userInfo(textFirstName, textLastName, textEmail,
                                             textEmergency1, textRelation1,
                                             textEmergency2, textRelation2,
                                             textEmergency3, textRelation3, txtGender);

            //Extract user reference from db for help me app users
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Help Me App Users");

            String userID = firebaseUser.getUid();

            referenceProfile.child(userID).setValue(userInfo).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                     Toast.makeText(SettingsPage.this, "Update Successful", Toast.LENGTH_LONG).show();

                     //send user back to main page after updating data
                    Intent intent = new Intent(SettingsPage.this, MainActivity.class);
                    startActivity(intent);
                }
                else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(SettingsPage.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    //fetch data from firebase and display
    private void showProfile(FirebaseUser firebaseUser) {
        String userIDofRegistered = firebaseUser.getUid();

        //Extracting user reference from db for "Help Me App Users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Help Me App Users");

        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInfo userInfo = snapshot.getValue(userInfo.class);
                if (userInfo !=null) {
                    textFirstName = userInfo.firstName;
                    textLastName = userInfo.lastName;
                    textEmail = userInfo.email;
                    textEmergency1 = userInfo.emergencyContact1;
                    textRelation1 = userInfo.relation1;
                    textEmergency2 = userInfo.emergencyContact2;
                    textRelation2 = userInfo.relation2;
                    textEmergency3 = userInfo.emergencyContact3;
                    textRelation3 = userInfo.relation3;

                    updateFirstName.setText(textFirstName);
                    updateLastName.setText(textLastName);
                    updateEmail.setText(textEmail);
                    updateEmergency1.setText(textEmergency1);
                    updateRelation1.setText(textRelation1);
                    updateEmergency2.setText(textEmergency2);
                    updateRelation2.setText(textRelation2);
                    updateEmergency3.setText(textEmergency3);
                    updateRelation3.setText(textRelation3);
                } else {
                    Toast.makeText(SettingsPage.this, "Something went wrong! Please try once again.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SettingsPage.this, "Something went wrong! Please try once again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item1) {
            authProfile.signOut();
            Toast.makeText(this, "Logged Out of HelpMeApp", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, RegisterPage.class);

            //Clear stack to prevent user coming back to Settings Activity
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); //Close setting activity
        }
        else if (id == R.id.actionBarMain) {
            Intent intent = new Intent(SettingsPage.this, MainActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.actionBarSetting) {
            Intent intent = new Intent(SettingsPage.this, SettingsPage.class);
            startActivity(intent);
        }
        else if (id==R.id.menu_refresh) {
            //Refresh page/activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
        }
        else {
            Toast.makeText(this, "Something went wrong! Please try again!", Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

}