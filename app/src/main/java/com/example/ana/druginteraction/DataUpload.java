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

    public static ArrayList<String> readRawTextFile(Context cx, int resId)
    {
        InputStream inputStream = cx.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        StringBuilder text = new StringBuilder();
        String[] drug = null;
//        String[][] drugs = null;
//        int count = 0;
//        List<List<String>> listOfLists = Lists.newArrayList();
//        ArrayList<ArrayList<String>> listOfDrugs = new ArrayList<ArrayList<String>>();
        ArrayList<String> listOfDrugs = new ArrayList<>();

        try {
            while (( line = buffreader.readLine()) != null) {
                text.append(line);
                text.append('\n');
                drug = text.toString().split("#####");
                listOfDrugs.addAll(Arrays.asList(drug));
                text.setLength(0);
//                drugs[count] = drug;
//                count++;
            }
        } catch (IOException e) {
            return null;
        }
        return listOfDrugs;
    }

    public static void loadDrugs(ArrayList<String> drugsList){

        String[] drugId = new String[drugsList.size()];
        String[] drugName = new String[drugsList.size()];
        String[] drugDesription = new String[drugsList.size()];
        int j = 0;

        databaseDrugs = FirebaseDatabase.getInstance().getReference("drugs");

        for(int i = 0; i <= drugsList.size(); i = i+3) {

            drugId[j] = drugsList.get(i);
            drugName[j] = drugsList.get(i+1);
            drugDesription[j] = drugsList.get(i+2);

            String id = databaseDrugs.push().getKey();
            Drug drug = new Drug(id, drugId[j], drugName[j], drugDesription[j]);
            databaseDrugs.child(id).setValue(drug);

            j++;
        }
    }

    public static void loadDrugInteractions(ArrayList<String> drugInteractionList){

        String[] drug1Id = new String[drugInteractionList.size()];
        String[] drug2Id = new String[drugInteractionList.size()];
        String[] drug2Name = new String[drugInteractionList.size()];
        String[] interactions = new String[drugInteractionList.size()];
        int j = 0;

        databaseDrugInteractions= FirebaseDatabase.getInstance().getReference("drugInteractions");

        for(int i = 0; i <= drugInteractionList.size(); i = i+4) {

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

    public static void loadFoodInteractions(ArrayList<String> foodInteractionList){

        String[] drugId = new String[foodInteractionList.size()];
        String[] interactions = new String[foodInteractionList.size()];
        int j = 0;

        databaseFoodInteractions= FirebaseDatabase.getInstance().getReference("foodInteractions");

        for(int i = 0; i <= foodInteractionList.size(); i = i+2) {

            drugId[j] = foodInteractionList.get(i);
            interactions[j] = foodInteractionList.get(i+1);

            String id = databaseFoodInteractions.push().getKey();
            FoodInteractions foodInteractions = new FoodInteractions(id, drugId[j], interactions[j]);
            databaseDrugInteractions.child(id).setValue(foodInteractions);

            j++;
        }
    }
}
