package com.example.ana.druginteraction;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.IAccountAccessor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SelectDiseaseAdapter extends RecyclerView.Adapter<SelectDiseaseAdapter.SearchViewHolder> {

    Context context;
    ArrayList<String> diseaseNameList;
    String drugName;
    String patient_email;
    String pos;

    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
    private DatabaseReference diseaseDrugRef = FirebaseDatabase.getInstance().getReference().child("DiseaseDrug");

    class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView diseaseName;

        public SearchViewHolder(View itemView) {
            super(itemView);
            diseaseName = (TextView) itemView.findViewById(R.id.diseaseName);
        }
    }

    public SelectDiseaseAdapter(Context context, ArrayList<String> diseaseNameList, String drugName, String patient_email, String pos) {
        this.context = context;
        this.diseaseNameList = diseaseNameList;
        this.drugName = drugName;
        this.patient_email = patient_email;
        this.pos = pos;
    }

    @Override
    public SelectDiseaseAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.select_disease_list, parent, false);
        return new SelectDiseaseAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SelectDiseaseAdapter.SearchViewHolder holder, final int position) {
        holder.diseaseName.setText(diseaseNameList.get(position));

        holder.diseaseName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addDrug(drugName, patient_email, diseaseNameList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return diseaseNameList.size();
    }


    public void addDrug(final String drugName, final String patient_email, final String diseaseName) {

        if (!drugName.equals("")) {
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String patientEmail = snapshot.child("email").getValue(String.class);
                        String patientName = snapshot.child("name").getValue(String.class);
                        String patientSurname = snapshot.child("surname").getValue(String.class);

                        if (patient_email.toLowerCase().trim().equals(patientEmail.toLowerCase().trim())) {
                            DiseaseDrug diseaseDrug = new DiseaseDrug(patientEmail, patientName, patientSurname, diseaseName, drugName);
                            String id = diseaseDrugRef.push().getKey();
                            diseaseDrugRef.child(id).setValue(diseaseDrug);
                            Toast.makeText(context, drugName + " added to " + diseaseName + "!", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

}
