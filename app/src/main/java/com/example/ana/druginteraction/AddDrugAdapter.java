package com.example.ana.druginteraction;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddDrugAdapter extends RecyclerView.Adapter<AddDrugAdapter.SearchViewHolder> {

    Context context;
    ArrayList<String> diseaseNameList;
    String pos;
    String drugName;

    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
    private DatabaseReference diseaseDrugRef = FirebaseDatabase.getInstance().getReference().child("DiseaseDrug");

    class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView diseaseName;

        public SearchViewHolder(View itemView) {
            super(itemView);
            diseaseName = (TextView) itemView.findViewById(R.id.diseaseName);
        }
    }

    public AddDrugAdapter(Context context, ArrayList<String> diseaseNameList, String drugName, String pos) {
        this.context = context;
        this.diseaseNameList = diseaseNameList;
        this.drugName = drugName;
        this.pos = pos;
    }

    @Override
    public AddDrugAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_diseases_list, parent, false);
        return new AddDrugAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AddDrugAdapter.SearchViewHolder holder, int position) {
        holder.diseaseName.setText(diseaseNameList.get(position));

        holder.diseaseName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addDrug(drugName, diseaseNameList.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return diseaseNameList.size();
    }

    public void addDrug(final String drugName, final String diseaseName) {

        if (!drugName.equals("")) {
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String patientEmail = snapshot.child("email").getValue(String.class);
                        String patientName = snapshot.child("name").getValue(String.class);
                        String patientSurname = snapshot.child("surname").getValue(String.class);

                        if (FirebaseAuth.getInstance().getCurrentUser().getEmail().toLowerCase().trim().equals(patientEmail.toLowerCase().trim())) {
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
