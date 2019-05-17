package com.example.ana.druginteraction;

//helper class for uploading categories in database
public class Category {
    String id;
    String drugId;
    String category;

    public Category() {}
    public Category(String id, String drugId, String category) {
        this.id = id;
        this.drugId = drugId;
        this.category = category;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
