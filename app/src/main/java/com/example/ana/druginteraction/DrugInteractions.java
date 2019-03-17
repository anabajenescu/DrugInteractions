package com.example.ana.druginteraction;

public class DrugInteractions {

    String id;
    String drug1Id;
    String drug2Id;
    String drug2Name;
    String interaction;

    public DrugInteractions() {};
    public DrugInteractions(String id, String drug1Id, String drug2Id, String drug2Name, String interaction) {

        this.id = id;
        this.drug1Id = drug1Id;
        this.drug2Id = drug2Id;
        this.drug2Name = drug2Name;
        this.interaction = interaction;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDrug1Id() {
        return drug1Id;
    }

    public void setDrug1Id(String drug1Id) {
        this.drug1Id = drug1Id;
    }

    public String getDrug2Id() {
        return drug2Id;
    }

    public void setDrug2Id(String drug2Id) {
        this.drug2Id = drug2Id;
    }

    public String getDrug2Name() {
        return drug2Name;
    }

    public void setDrug2Name(String drug2Name) {
        this.drug2Name = drug2Name;
    }

    public String getInteraction() {
        return interaction;
    }

    public void setInteraction(String interaction) {
        this.interaction = interaction;
    }
}
