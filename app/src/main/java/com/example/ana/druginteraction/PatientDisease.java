package com.example.ana.druginteraction;

public class PatientDisease {

    private String patientEmail;
    private String patientName;
    private String patientSurname;
    private String diseaseName;

    public PatientDisease(String patientEmail, String patientName, String patientSurname, String diseaseName) {
        this.patientEmail = patientEmail;
        this.patientName = patientName;
        this.patientSurname = patientSurname;
        this.diseaseName = diseaseName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientSurname() {
        return patientSurname;
    }

    public void setPatientSurname(String patientSurname) {
        this.patientSurname = patientSurname;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }
}
