package com.example.ana.druginteraction;

public class Patient extends Person {

//    private String token;

    public Patient(String name, String surname, String email, String phone, String age, String gender,
                   String profilePicUrl) {//, String token){
        super(name, surname, email, phone, age, gender, "P", profilePicUrl);
//        this.token = token;
    }
}
