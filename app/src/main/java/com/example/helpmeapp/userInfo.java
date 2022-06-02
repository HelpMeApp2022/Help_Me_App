package com.example.helpmeapp;

public class userInfo {

    public String firstName, lastName, email, emergencyContact1, relation1, emergencyContact2, relation2, emergencyContact3, relation3, gender;

    public userInfo(String firstName, String lastName, String email, String emergencyContact1, String relation1,
                    String emergencyContact2, String relation2, String emergencyContact3, String relation3, String txtGender){
        this.firstName=firstName;
        this.lastName=lastName;
        this.email=email;
        this.emergencyContact1=emergencyContact1;
        this.relation1=relation1;
        this.emergencyContact2=emergencyContact2;
        this.relation2=relation2;
        this.emergencyContact3=emergencyContact3;
        this.relation3=relation3;
        this.gender=txtGender;
    }

    public userInfo() {
    }
}
