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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DiseaseAdapter extends RecyclerView.Adapter<DiseaseAdapter.SearchViewHolder> {

    Context context;
    ArrayList<String> diseaseNameList;
    String pos;

    private DatabaseReference diseasesRef = FirebaseDatabase.getInstance().getReference().child("PatientDisease");

    class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView diseaseName;
        ImageButton deleteDisease;

        public View mView;

        public SearchViewHolder(View itemView) {
            super(itemView);
            diseaseName = (TextView) itemView.findViewById(R.id.diseaseName);
            deleteDisease = (ImageButton) itemView.findViewById(R.id.delete_disease);

            mView = itemView;
        }
    }

    public DiseaseAdapter(Context context, ArrayList<String> diseaseNameList, String pos) {
        this.context = context;
        this.diseaseNameList = diseaseNameList;
        this.pos = pos;
    }

    @Override
    public DiseaseAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.diseases_list_layout, parent, false);
        return new DiseaseAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DiseaseAdapter.SearchViewHolder holder, int position) {
        holder.diseaseName.setText(diseaseNameList.get(position));
        //delete disease when click on X from disease name
        holder.deleteDisease.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                deleteDisease(diseaseNameList.get(holder.getAdapterPosition()));
                diseaseNameList.remove(holder.getAdapterPosition());  // remove the item from list
                notifyItemRemoved(holder.getAdapterPosition()); // notify the adapter about the removed item

            }
        });

        holder.diseaseName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent treatment = new Intent(v.getContext(), TreatmentActivity.class);
                treatment.putExtra("diseaseName", diseaseNameList.get(holder.getAdapterPosition()));
                treatment.putExtra("diseasePosition", holder.getAdapterPosition());
                v.getContext().startActivity(treatment);
            }
        });
    }

    //remove selected disease from firebase
    public void deleteDisease(final String disease_name) {
        diseasesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String patientEmail = snapshot.child("patientEmail").getValue(String.class);
                    String diseaseName = snapshot.child("diseaseName").getValue(String.class);

                    if (patientEmail.toLowerCase().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail().toLowerCase())) {
                        if (diseaseName.toLowerCase().trim().equals(disease_name.toLowerCase().trim())) {
//                            String key = diseasesRef.push().getKey();
                            diseasesRef.child(snapshot.getKey()).removeValue();
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return diseaseNameList.size();
    }
}
