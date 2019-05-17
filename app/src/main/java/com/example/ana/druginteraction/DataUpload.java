package com.example.ana.druginteraction;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class DataUpload{

    static DatabaseReference databaseDrugs;
    static DatabaseReference databaseDrugInteractions;
    static DatabaseReference databaseFoodInteractions;
    static DatabaseReference databaseCategory;

    public static ArrayList<String> readRawTextFile(Context cx, int resId)
    {
        //open raw text file
        InputStream inputStream = cx.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        StringBuilder text = new StringBuilder();
        String[] drug;
        ArrayList<String> listOfDrugs = new ArrayList<>();

        try {
            //read lines from file
            while (( line = buffreader.readLine()) != null) {
                //append line to string builder
                text.append(line);
                text.append('\n');
                //split line by ##### to obtain each element from line
                drug = text.toString().split("#####");
                listOfDrugs.addAll(Arrays.asList(drug));
                text.setLength(0);
            }
        } catch (IOException e) {
            return null;
        }
        return listOfDrugs;
    }

    public static void loadDrugs(ArrayList<String> drugsList){

        String[] drugId = new String[drugsList.size()];
        String[] drugName = new String[drugsList.size()];
        String[] drugDescription = new String[drugsList.size()];
        int j = 0;

        //reference to firebase drugs database
        databaseDrugs = FirebaseDatabase.getInstance().getReference("drugs");

        for(int i = 0; i < drugsList.size(); i = i+3) {

            //take drug id, name and description
            drugId[j] = drugsList.get(i);
            drugName[j] = drugsList.get(i+1);
            drugDescription[j] = drugsList.get(i+2);

            //generate id and push in firebase
            String id = databaseDrugs.push().getKey();
            //create drug object in order to upload in drugs database
            Drug drug = new Drug(id, drugId[j], drugName[j], drugDescription[j]);
            databaseDrugs.child(id).setValue(drug);

            j++;
        }
    }

    //load drugInteractions in database the same as uploading drugs
    public static void loadDrugInteractions(ArrayList<String> drugInteractionList){

        String[] drug1Id = new String[drugInteractionList.size()];
        String[] drug2Id = new String[drugInteractionList.size()];
        String[] drug2Name = new String[drugInteractionList.size()];
        String[] interactions = new String[drugInteractionList.size()];
        int j = 0;

        databaseDrugInteractions= FirebaseDatabase.getInstance().getReference("drugInteractions");

        for(int i = 0; i < drugInteractionList.size(); i = i+4) {

            drug1Id[j] = drugInteractionList.get(i);
            drug2Id[j] = drugInteractionList.get(i+1);
            drug2Name[j] = drugInteractionList.get(i+2);
            interactions[j] = drugInteractionList.get(i+3);

            String id = databaseDrugInteractions.push().getKey();
            DrugInteractions drugInteractions = new DrugInteractions(id, drug1Id[j], drug2Id[j], drug2Name[j], interactions[j]);
            databaseDrugInteractions.child(id).setValue(drugInteractions);

            j++;
        }
    }

    //load foodInteractions in database the same as uploading drugs
    public static void loadFoodInteractions(ArrayList<String> foodInteractionList){

        String[] drugId = new String[foodInteractionList.size()];
        String[] interactions = new String[foodInteractionList.size()];
        int j = 0;

        databaseFoodInteractions= FirebaseDatabase.getInstance().getReference("foodInteractions");

        for(int i = 0; i < foodInteractionList.size(); i = i+2) {

            drugId[j] = foodInteractionList.get(i);
            interactions[j] = foodInteractionList.get(i+1);

            String id = databaseFoodInteractions.push().getKey();
            FoodInteractions foodInteractions = new FoodInteractions(id, drugId[j], interactions[j]);
            databaseFoodInteractions.child(id).setValue(foodInteractions);

            j++;
        }
    }

    //load categories in database the same as uploading drugs
    public static void loadCategories(ArrayList<String> categoryList){

        String[] drugId = new String[categoryList.size()];
        String[] categoryName = new String[categoryList.size()];
        int j = 0;

        databaseCategory= FirebaseDatabase.getInstance().getReference("category");

        for(int i = 0; i < categoryList.size(); i = i+3) {

            drugId[j] = categoryList.get(i);
            categoryName[j] = categoryList.get(i+2);

            String id = databaseCategory.push().getKey();
            Category category = new Category(id, drugId[j], categoryName[j]);
            databaseCategory.child(id).setValue(category);

            j++;
        }
    }

    public static void removeDataFromDatabase(){
        DatabaseReference root = FirebaseDatabase.getInstance().getReference();
        root.setValue(null);
    }
}
