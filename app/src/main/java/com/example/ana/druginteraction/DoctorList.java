package com.example.ana.druginteraction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

//adapter used to display doctors information
public class DoctorList extends ArrayAdapter {

    private Activity context;
    private List<Person> doctorList;

    public DoctorList(@NonNull Activity context, List<Person> doctorList) {
        super(context, R.layout.doctor_details_activity, doctorList);
        this.context = context;
        this.doctorList = doctorList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        @SuppressLint("ViewHolder") View listViewItem = inflater.inflate(R.layout.patient_profile_list, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.patientNameView);
        TextView textViewSurname = (TextView) listViewItem.findViewById(R.id.patientSurnameView);
        TextView textViewGender = (TextView) listViewItem.findViewById(R.id.patientGenderView);
        TextView textViewAge = (TextView) listViewItem.findViewById(R.id.patientAgeView);
        TextView textViewPhone = (TextView) listViewItem.findViewById(R.id.patientPhoneView);
        TextView textViewEmail = (TextView) listViewItem.findViewById(R.id.patientEmailView);

        Person user = doctorList.get(position);

        textViewName.setText("  Name:           " + user.getName());
        textViewSurname.setText("  Surname:      " + user.getSurname());
        textViewGender.setText("  Gender:         " + user.getGender());
        textViewAge.setText("  Age:               " + user.getAge());
        textViewPhone.setText("  Phone:          " + user.getPhone());
        textViewEmail.setText("  Email:            " + user.getEmail());

        return listViewItem;

    }
}
