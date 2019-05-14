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

public class PatientDrugsAdapter extends RecyclerView.Adapter<PatientDrugsAdapter.SearchViewHolder> {

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
            deleteDrug.setVisibility(View.GONE);

            mView = itemView;
        }
    }

    public PatientDrugsAdapter(Context context, ArrayList<String> drugsNameList, String diseaseName, String pos) {
        this.context = context;
        this.drugsNameList = drugsNameList;
        this.diseaseName = diseaseName;
        this.pos = pos;
    }

    @Override
    public PatientDrugsAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.treatment_list_layout, parent, false);
        return new PatientDrugsAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PatientDrugsAdapter.SearchViewHolder holder, int position) {
        holder.drugName.setText(drugsNameList.get(position));

        holder.drugName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent drugs = new Intent(v.getContext(), DrugDetailsActivity.class);
                drugs.putExtra("drugName", drugsNameList.get(holder.getAdapterPosition()));
                v.getContext().startActivity(drugs);
            }
        });

    }

    @Override
    public int getItemCount() {
        return drugsNameList.size();
    }
}
