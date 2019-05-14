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

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.SearchViewHolder> {

    Context context;
    ArrayList<String> patientNameList;
    String pos;
    private DatabaseReference patientsRef = FirebaseDatabase.getInstance().getReference().child("DoctorPatient");

    class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView patientName;
        ImageButton deletePatient;

        public View mView;

        public SearchViewHolder(View itemView) {
            super(itemView);
            patientName = (TextView) itemView.findViewById(R.id.patientName);
            deletePatient = (ImageButton) itemView.findViewById(R.id.delete_patient);

            mView = itemView;
        }
    }

    public PatientAdapter(Context context, ArrayList<String> patientNameList, String pos) {
        this.context = context;
        this.patientNameList = patientNameList;
        this.pos = pos;
    }

    @Override
    public PatientAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.patient_list_layout, parent, false);
        return new PatientAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PatientAdapter.SearchViewHolder holder, int position) {
        holder.patientName.setText(patientNameList.get(position));

        holder.deletePatient.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                deletePatient(patientNameList.get(holder.getAdapterPosition()));
                patientNameList.remove(holder.getAdapterPosition());  // remove the item from list
                notifyItemRemoved(holder.getAdapterPosition()); // notify the adapter about the removed item

            }
        });

        holder.patientName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent patientDetails = new Intent(v.getContext(), PatientDetailsActivity.class);
                patientDetails.putExtra("patientName", patientNameList.get(holder.getAdapterPosition()));
                patientDetails.putExtra("patientPosition", holder.getAdapterPosition());
                v.getContext().startActivity(patientDetails);
            }
        });
    }

    @Override
    public int getItemCount() {
        return patientNameList.size();
    }

    public void deletePatient(final String patient_name) {
        patientsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String patientName = snapshot.child("patientName").getValue(String.class);
                    String patientSurname = snapshot.child("patientSurname").getValue(String.class);
                    String doctorEmail = snapshot.child("doctorEmail").getValue(String.class);

                    String fullName = patientSurname + " " + patientName;

                    if (doctorEmail.toLowerCase().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail().toLowerCase())) {
                        if (patient_name.toLowerCase().trim().equals(fullName.toLowerCase().trim())) {
//                            String key = patientsRef.push().getKey();
                            patientsRef.child(snapshot.getKey()).removeValue();
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
}
