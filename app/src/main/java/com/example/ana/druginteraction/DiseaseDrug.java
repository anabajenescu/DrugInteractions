package com.example.ana.druginteraction;

//helper class created to insert disease drug relationship in firebase database
public class DiseaseDrug {

    private String userEmail;
    private String userName;
    private String userSurname;
    private String diseaseName;
    private String drugName;

    public DiseaseDrug(String userEmail, String userName, String userSurname, String diseaseName, String drugName) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.userSurname = userSurname;
        this.diseaseName = diseaseName;
        this.drugName = drugName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSurname() {
        return userSurname;
    }

    public void setUserSurname(String userSurname) {
        this.userSurname = userSurname;
    }
}
