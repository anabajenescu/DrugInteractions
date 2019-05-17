package com.example.ana.druginteraction;

import android.content.Context;
import android.content.Intent;
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

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.SearchViewHolder> {

    Context context;
    ArrayList<String> doctorNameList;
    ArrayList<String> doctorEmailList;
    String pos;
    private DatabaseReference doctorsRef = FirebaseDatabase.getInstance().getReference().child("DoctorPatient");

    class SearchViewHolder extends RecyclerView.ViewHolder {
        TextView doctorName;
        ImageButton deleteDoctor;

        public View mView;

        public SearchViewHolder(View itemView) {
            super(itemView);
            doctorName = (TextView) itemView.findViewById(R.id.doctorName);
            deleteDoctor = (ImageButton) itemView.findViewById(R.id.delete_doctor);

            mView = itemView;
        }
    }

    public DoctorAdapter(Context context, ArrayList<String> doctorNameList, ArrayList<String> doctorEmailList, String pos) {
        this.context = context;
        this.doctorNameList = doctorNameList;
        this.doctorEmailList = doctorEmailList;
        this.pos = pos;
    }

    @Override
    public DoctorAdapter.SearchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.doctor_list_layout, parent, false);
        return new DoctorAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DoctorAdapter.SearchViewHolder holder, int position) {
        holder.doctorName.setText(doctorNameList.get(position));

        //delete doctor when click on X image button from doctor's name
        holder.deleteDoctor.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                deleteDoctor(doctorEmailList.get(holder.getAdapterPosition()));
                doctorNameList.remove(holder.getAdapterPosition());  // remove the item from list
                notifyItemRemoved(holder.getAdapterPosition()); // notify the adapter about the removed item

            }
        });

        //redirect to corresponding DoctorDetailsActivity when click on doctor's name
        holder.doctorName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent doctorDetails = new Intent(v.getContext(), DoctorDetailsActivity.class);
                doctorDetails.putExtra("doctorName", doctorNameList.get(holder.getAdapterPosition()));
                doctorDetails.putExtra("doctorEmail", doctorEmailList.get(holder.getAdapterPosition()));
                doctorDetails.putExtra("doctorPosition", holder.getAdapterPosition());
                v.getContext().startActivity(doctorDetails);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorNameList.size();
    }

    //find the doctors of the logged in patients and remove them from database
    public void deleteDoctor(final String doctor_email) {
        doctorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String patientEmail = snapshot.child("patientEmail").getValue(String.class);
                    String doctorEmail = snapshot.child("doctorEmail").getValue(String.class);

                    if (patientEmail.toLowerCase().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail().toLowerCase())) {
                        if (doctor_email.toLowerCase().trim().equals(doctorEmail.toLowerCase().trim())) {
//                            String key = patientsRef.push().getKey();
                            doctorsRef.child(snapshot.getKey()).removeValue();
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
