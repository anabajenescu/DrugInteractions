package com.example.ana.druginteraction;

//helper class for uploading doctor-patient data in firebase database
public class DoctorPatient {

    private String doctorEmail;
    private String patientEmail;
    private String patientName;
    private String patientSurname;

    public DoctorPatient(String doctorEmail, String patientEmail, String patientName, String patientSurname) {
        this.doctorEmail = doctorEmail;
        this.patientEmail = patientEmail;
        this.patientName = patientName;
        this.patientSurname = patientSurname;
    }

    public String getDoctorEmail() {
        return doctorEmail;
    }

    public void setDoctorEmail(String doctorEmail) {
        this.doctorEmail = doctorEmail;
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
}
