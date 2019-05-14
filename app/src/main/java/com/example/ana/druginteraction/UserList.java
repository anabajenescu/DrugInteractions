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

import org.w3c.dom.Text;

import java.util.List;

public class UserList extends ArrayAdapter {

    private Activity context;
    private List<Person> userList;

    public UserList(@NonNull Activity context, List<Person> userList) {
        super(context, R.layout.user_profile_activity, userList);
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        @SuppressLint("ViewHolder") View listViewItem = inflater.inflate(R.layout.user_info_list, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.userNameView);
        TextView textViewSurname = (TextView) listViewItem.findViewById(R.id.userSurnameView);
        TextView textViewStatus = (TextView) listViewItem.findViewById(R.id.userStatusView);
        TextView textViewGender = (TextView) listViewItem.findViewById(R.id.userGenderView);
        TextView textViewAge = (TextView) listViewItem.findViewById(R.id.userAgeView);
        TextView textViewPhone = (TextView) listViewItem.findViewById(R.id.userPhoneView);
        TextView textViewEmail = (TextView) listViewItem.findViewById(R.id.userEmailView);

        Person user = userList.get(position);

        textViewName.setText("Name: \n" + user.getName());
        textViewSurname.setText("Surname: \n" + user.getSurname());
        textViewStatus.setText("Status: \n" + user.getStatus());
        textViewGender.setText("Gender: \n" + user.getGender());
        textViewAge.setText("Age: \n" + user.getAge());
        textViewPhone.setText("Phone Number: \n" + user.getPhone());
        textViewEmail.setText("Email address: \n" + user.getEmail());

        return listViewItem;

    }

}
