package com.example.ana.druginteraction;

//helper class used to upload drugs in firebase database
public class Drug {
    public String id;
    public String drugId;
    public String name;
    public String description;

    public Drug() {
        // Default constructor required for calls to DataSnapshot.getValue(Drug.class)
    }

    public Drug(String id, String drugId, String name, String description) {
        this.id = id;
        this.drugId = drugId;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getDrugId() {
        return drugId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDrugId(String drugId) {
        this.drugId = drugId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
