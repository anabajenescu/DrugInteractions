package com.example.ana.druginteraction;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import static com.example.ana.druginteraction.DataUpload.loadCategories;
import static com.example.ana.druginteraction.DataUpload.loadDrugInteractions;
import static com.example.ana.druginteraction.DataUpload.loadDrugs;
import static com.example.ana.druginteraction.DataUpload.loadFoodInteractions;
import static com.example.ana.druginteraction.DataUpload.readRawTextFile;
import static com.example.ana.druginteraction.DataUpload.removeDataFromDatabase;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //buffers to store drugs, drug interactions, food interactions and categories
//        ArrayList<String> drugsBuffer;
//        ArrayList<String> drugInteractionsBuffer;
//        ArrayList<String> foodInteractionsBuffer;
//        ArrayList<String> categoryBuffer;

//        int drugsId = this.getResources().getIdentifier("drugs", "raw", this.getPackageName());
//        int drugInteractionsId = this.getResources().getIdentifier("drug_interactions", "raw", this.getPackageName());
//        int foodInteractionsId = this.getResources().getIdentifier("food_interactions", "raw", this.getPackageName());
//        int categoryId = this.getResources().getIdentifier("category", "raw", this.getPackageName());

        //read drugs, drug interactions, food interactions and categories from .txt files
//        drugsBuffer = readRawTextFile(this,drugsId);
//        drugInteractionsBuffer = readRawTextFile(this,drugInteractionsId);
//        foodInteractionsBuffer = readRawTextFile(this,foodInteractionsId);
//        categoryBuffer = readRawTextFile(this, categoryId);

        //empty database
        //removeDataFromDatabase();

        //insert drugs, drug interactions, food interactions and categories into realtime database
//        loadDrugs(drugsBuffer);
//        loadDrugInteractions(drugInteractionsBuffer);
//        loadFoodInteractions(foodInteractionsBuffer);
//        loadCategories(categoryBuffer);
    }
}
