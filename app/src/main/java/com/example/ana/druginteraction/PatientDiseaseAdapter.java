package com.example.ana.druginteraction;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

public class PatientDiseaseAdapter extends RecyclerView.Adapter<PatientDiseaseAdapter.SearchViewHolder> {

    Context context;
    ArrayList<String> diseaseNameList;
    String pos;
    String patientEmail;

    private DatabaseReference diseasesRef = FirebaseDatabase.getInstance().getReference().child("PatientDisease");

    class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView diseaseName;

        public SearchViewHolder(View itemView) {
            super(itemView);
            diseaseName = (TextView) itemView.findViewById(R.id.patientDiseaseName);
        }
    }

    public PatientDiseaseAdapter(Context context, ArrayList<String> diseaseNameList, String patientEmail, String pos) {
        this.context = context;
        this.diseaseNameList = diseaseNameList;
        this.patientEmail = patientEmail;
        this.pos = pos;
    }

    @Override
    public PatientDiseaseAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.patient_diseases_layout, parent, false);
        return new PatientDiseaseAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PatientDiseaseAdapter.SearchViewHolder holder, int position) {
        holder.diseaseName.setText(diseaseNameList.get(position));

        holder.diseaseName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent treatment = new Intent(v.getContext(), PatientTreatmentActivity.class);
                treatment.putExtra("diseaseName", diseaseNameList.get(holder.getAdapterPosition()));
                treatment.putExtra("diseasePosition", holder.getAdapterPosition());
                treatment.putExtra("patientEmail", patientEmail);
                v.getContext().startActivity(treatment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return diseaseNameList.size();
    }
}
