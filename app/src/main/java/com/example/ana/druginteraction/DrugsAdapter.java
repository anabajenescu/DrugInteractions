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

public class DrugsAdapter extends RecyclerView.Adapter<DrugsAdapter.SearchViewHolder> {

    Context context;
    ArrayList<String> drugsNameList;
    String diseaseName;
    String pos;

    private DatabaseReference drugsRef = FirebaseDatabase.getInstance().getReference().child("DiseaseDrug");

    class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView drugName;
        ImageButton deleteDrug;

        public View mView;

        public SearchViewHolder(View itemView) {
            super(itemView);
            drugName = (TextView) itemView.findViewById(R.id.drugName);
            deleteDrug = (ImageButton) itemView.findViewById(R.id.delete_drug);

            mView = itemView;
        }
    }

    public DrugsAdapter(Context context, ArrayList<String> drugsNameList, String diseaseName, String pos) {
        this.context = context;
        this.drugsNameList = drugsNameList;
        this.diseaseName = diseaseName;
        this.pos = pos;
    }

    @Override
    public DrugsAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.treatment_list_layout, parent, false);
        return new DrugsAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DrugsAdapter.SearchViewHolder holder, int position) {
        holder.drugName.setText(drugsNameList.get(position));
        holder.deleteDrug.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                deleteDrug(drugsNameList.get(holder.getAdapterPosition()), diseaseName);
                drugsNameList.remove(holder.getAdapterPosition());  // remove the item from list
                notifyItemRemoved(holder.getAdapterPosition()); // notify the adapter about the removed item

            }
        });

        holder.drugName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent drugs = new Intent(v.getContext(), DrugDetailsActivity.class);
                drugs.putExtra("drugName", drugsNameList.get(holder.getAdapterPosition()));
                v.getContext().startActivity(drugs);
            }
        });
    }

    public void deleteDrug(final String drug_name, final String disease_name) {
        drugsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userEmail = snapshot.child("userEmail").getValue(String.class);
                    String drugName = snapshot.child("drugName").getValue(String.class);
                    String diseaseName = snapshot.child("diseaseName").getValue(String.class);

                    if (userEmail.toLowerCase().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail().toLowerCase())) {
                        if ((diseaseName.toLowerCase().trim().equals(disease_name.toLowerCase().trim())) &&
                                drugName.toLowerCase().trim().equals(drug_name.toLowerCase().trim())) {
                            drugsRef.child(snapshot.getKey()).removeValue();
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
        return drugsNameList.size();
    }
}
