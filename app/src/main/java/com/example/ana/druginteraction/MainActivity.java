package com.example.ana.druginteraction;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import static com.example.ana.druginteraction.DataUpload.loadDrugInteractions;
import static com.example.ana.druginteraction.DataUpload.loadDrugs;
import static com.example.ana.druginteraction.DataUpload.loadFoodInteractions;
import static com.example.ana.druginteraction.DataUpload.readRawTextFile;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<String> drugsBuffer;
        ArrayList<String> drugInteractionsBuffer;
        ArrayList<String> foodInteractionsBuffer;

        int drugsId = this.getResources().getIdentifier("drugs", "raw", this.getPackageName());
        int drugInteractionsId = this.getResources().getIdentifier("drug_interactions", "raw", this.getPackageName());
        int foodInteractionsId = this.getResources().getIdentifier("food_interactions", "raw", this.getPackageName());

        drugsBuffer = readRawTextFile(this,drugsId);
        //drugInteractionsBuffer = readRawTextFile(this,drugInteractionsId);
        foodInteractionsBuffer = readRawTextFile(this,foodInteractionsId);

        loadDrugs(drugsBuffer);
        //loadDrugInteractions(drugInteractionsBuffer);
        loadFoodInteractions(foodInteractionsBuffer);
    }
}
