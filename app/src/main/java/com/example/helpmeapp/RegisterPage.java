package com.example.helpmeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterPage extends AppCompatActivity {

    //Creating variables for edittext and button
    private Button btnReg;
    private EditText userFirstName, userLastName, userEmail, emergency1, relationship1;
    private EditText emergency2, relationship2, emergency3, relationship3;

    //Variables for radiobutton
    RadioButton radio_male, radio_female;
    String txtGender = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        //hiding action bar on homepage
        Objects.requireNonNull(getSupportActionBar()).hide();

        //Initializing editTexts
        userFirstName = findViewById(R.id.firstName);
        userLastName = findViewById(R.id.lastName);
        userEmail = findViewById(R.id.email);
        emergency1 = findViewById(R.id.editTextEmergencyNumber1);
        relationship1 = findViewById(R.id.relationship1);
        emergency2 = findViewById(R.id.editTextEmergencyNumber2);
        relationship2 = findViewById(R.id.relationship2);
        emergency3 = findViewById(R.id.editTextEmergencyNumber3);
        relationship3 = findViewById(R.id.relationship3);

        //Initializing radio buttons
        radio_male = findViewById(R.id.radioButtonMale);
        radio_female = findViewById(R.id.radioButtonFemale);

        //Register button
        btnReg = findViewById(R.id.registerButton);
        //Setting on click listener on register button
        btnReg.setOnClickListener(v -> {

            //Obtain entered data
            String firstName = userFirstName.getText().toString();
            String lastName = userLastName.getText().toString();
            String email = userEmail.getText().toString();
            String emergencyContact1 = emergency1.getText().toString();
            String relation1 = relationship1.getText().toString();
            String emergencyContact2 = emergency2.getText().toString();
            String relation2 = relationship2.getText().toString();
            String emergencyContact3 = emergency3.getText().toString();
            String relation3 = relationship3.getText().toString();

            if (radio_male.isChecked()) {
                txtGender = "Male";
            } else if (radio_female.isChecked()) {
                txtGender = "Female";
            }

            if (TextUtils.isEmpty(firstName)) {
                Toast.makeText(getApplicationContext(), "Please enter your first name!", Toast.LENGTH_LONG).show();
                return;
            } else if (TextUtils.isEmpty(lastName)) {
                Toast.makeText(getApplicationContext(), "Please enter your last name!", Toast.LENGTH_LONG).show();
                return;
            } else if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), "Please enter your email!", Toast.LENGTH_LONG).show();
                return;
            } else if (TextUtils.isEmpty(emergencyContact1)) {
                Toast.makeText(getApplicationContext(), "Please enter your emergency contact 1!", Toast.LENGTH_LONG).show();
                return;
            } else if (TextUtils.isEmpty(relation1)) {
                Toast.makeText(getApplicationContext(), "Please enter your relationship with emergency contact 1!", Toast.LENGTH_LONG).show();
                return;
            } else if (TextUtils.isEmpty(emergencyContact2)) {
                Toast.makeText(getApplicationContext(), "Please enter your emergency contact 2!", Toast.LENGTH_LONG).show();
                return;
            } else if (TextUtils.isEmpty(relation2)) {
                Toast.makeText(getApplicationContext(), "Please enter your relationship with emergency contact 2!", Toast.LENGTH_LONG).show();
                return;
            } else if (TextUtils.isEmpty(emergencyContact3)) {
                Toast.makeText(getApplicationContext(), "Please enter your emergency contact 3!", Toast.LENGTH_LONG).show();
                return;
            } else if (TextUtils.isEmpty(relation3)) {
                Toast.makeText(getApplicationContext(), "Please enter your relationship with emergency contact 3!", Toast.LENGTH_LONG).show();
                return;
            } else if (!radio_male.isChecked() && !radio_female.isChecked()) {
                Toast.makeText(getApplicationContext(), "Chose your gender!", Toast.LENGTH_SHORT).show();
                return;
            }

            registerUser(firstName, lastName, email, emergencyContact1, relation1, emergencyContact2, relation2, emergencyContact3, relation3, txtGender);

        });
    }

    private void registerUser(String firstName, String lastName, String email, String emergencyContact1, String relation1, String emergencyContact2, String relation2, String emergencyContact3, String relation3, String txtGender) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, email).addOnCompleteListener(RegisterPage.this, new OnCompleteListener<AuthResult>() {

            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "User registration successful!", Toast.LENGTH_LONG).show();
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    userInfo userInfo = new userInfo(firstName, lastName, email, emergencyContact1, relation1, emergencyContact2, relation2, emergencyContact3, relation3, txtGender);

                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Help Me App Users");

                    referenceProfile.child(firebaseUser.getUid()).setValue(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){

                                Toast.makeText(RegisterPage.this, "User registered successfully!", Toast.LENGTH_LONG).show();

                                //if user created intent to login
                                Intent intent = new Intent(RegisterPage.this, MainActivity.class);
                                //to prevent user from returning back to register activity on pressing back button
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish(); //to close register activity

                            }
                            else {
                                Toast.makeText(RegisterPage.this, "User registration has failed. Please try once again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
}