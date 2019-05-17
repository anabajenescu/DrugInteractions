package com.example.ana.druginteraction;

//helper class for uploading food interactions in database
public class FoodInteractions {

    String id;
    String drugId;
    String interactions;

    public FoodInteractions() {};
    public FoodInteractions(String id, String drugId, String interactions) {
        this.id = id;
        this.drugId = drugId;
        this.interactions = interactions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDrugId() {
        return drugId;
    }

    public void setDrugId(String drugId) {
        this.drugId = drugId;
    }

    public String getInteractions() {
        return interactions;
    }

    public void setInteractions(String interactions) {
        this.interactions = interactions;
    }
}
