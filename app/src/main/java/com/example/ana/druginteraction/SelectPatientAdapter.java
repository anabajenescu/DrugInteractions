package com.example.ana.druginteraction;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
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

public class SelectPatientAdapter extends RecyclerView.Adapter<SelectPatientAdapter.SearchViewHolder> {

    Context context;
    ArrayList<String> patientNameList;
    String drugName;
    String pos;
    private DatabaseReference patientsRef = FirebaseDatabase.getInstance().getReference().child("DoctorPatient");

    class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView patientName;

        public SearchViewHolder(View itemView) {
            super(itemView);
            patientName = (TextView) itemView.findViewById(R.id.patientName);
        }
    }

    public SelectPatientAdapter(Context context, ArrayList<String> patientNameList, String drugName, String pos) {
        this.context = context;
        this.patientNameList = patientNameList;
        this.drugName = drugName;
        this.pos = pos;
    }

    @Override
    public SelectPatientAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.select_patient_list, parent, false);
        return new SelectPatientAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SelectPatientAdapter.SearchViewHolder holder, int position) {
        holder.patientName.setText(patientNameList.get(position));

        holder.patientName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent disease = new Intent(v.getContext(), SelectDiseaseActivity.class);
                disease.putExtra("patientName", patientNameList.get(holder.getAdapterPosition()));
                disease.putExtra("drugName", drugName);
                v.getContext().startActivity(disease);
            }
        });
    }

    @Override
    public int getItemCount() {
        return patientNameList.size();
    }

}
