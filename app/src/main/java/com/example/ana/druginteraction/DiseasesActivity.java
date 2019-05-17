package com.example.ana.druginteraction;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DiseasesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private RecyclerView resultList;

    private EditText diseaseNameTxt;
    private ImageButton addDisease;

    private DatabaseReference usersRef;
    private DatabaseReference diseasesRef;

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private TextView drawerUserText;
    private ImageView drawerImageUser;
    NavigationView mNavigationView;

    private MenuItem patients;
    private MenuItem diseases;
    private MenuItem profile;
    private MenuItem doctors;
    private MenuItem home;

    ArrayList<String> diseasesNameList;

    DiseaseAdapter diseaseAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diseases_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>My Diseases</font>"));


        diseaseNameTxt = (EditText) findViewById(R.id.disease_search);
        addDisease = (ImageButton) findViewById(R.id.addDisease);

        diseasesRef = FirebaseDatabase.getInstance().getReference().child("PatientDisease");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        resultList = (RecyclerView) findViewById(R.id.disease_list);
        resultList.setHasFixedSize(true);
        resultList.setLayoutManager(new LinearLayoutManager(this));
        resultList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        mDrawer = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.open_drawer, R.string.close_drawer);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.navigation);
        mNavigationView.bringToFront();
        mNavigationView.setNavigationItemSelectedListener(this);

        Menu menu = mNavigationView.getMenu();

        patients = menu.findItem(R.id.patients);
        doctors = menu.findItem(R.id.doctors);
        diseases = menu.findItem(R.id.diseases);
        profile = menu.findItem(R.id.userDetails);
        home = menu.findItem(R.id.home);

        View headerLayout = mNavigationView.getHeaderView(0);

        drawerImageUser = (ImageView) headerLayout.findViewById(R.id.drawerImageUser);
        drawerUserText = (TextView) headerLayout.findViewById(R.id.drawerUserName);

        diseasesNameList = new ArrayList<>();

        setMenuHeader();
        setAdapter("1");
        addDisease.setOnClickListener(this);
    }

    //enable menu options for patients and upload patient photo and details
    public void setMenuHeader() {

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Person person = dataSnapshot1.getValue(Person.class);
                    home.setVisible(true);
                    if (person.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        diseases.setVisible(false);
                        patients.setVisible(false);
                        doctors.setVisible(true);
                        home.setVisible(true);
                        drawerUserText.setText(person.getSurname() + " " + person.getName());
                        Glide.with(getApplicationContext()).load(person.getProfilePicUrl()).into(drawerImageUser);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //add disease when click on add image button
            case R.id.addDisease:
                String disease = diseaseNameTxt.getText().toString();
                addDisease(disease);
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);

        //create intents for each menu entry in order to redirect to corresponding activity
        int id = item.getItemId();
        switch (id) {

            case R.id.home:
                Intent home = new Intent(DiseasesActivity.this, MainActivity.class);
                home.putExtra("userEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                startActivity(home);
                break;
            case R.id.doctors:
                Intent doctors = new Intent(DiseasesActivity.this, DoctorsActivity.class);
                startActivity(doctors);
                break;
            case R.id.userDetails:
                Intent myUserProfile = new Intent(DiseasesActivity.this, UserProfileActivity.class);
                myUserProfile.putExtra("userEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                startActivity(myUserProfile);
                break;
            case R.id.action_logout:
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    AuthUI.getInstance()
                            .signOut(this)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    // user is now signed out
                                    startActivity(new Intent(DiseasesActivity.this, LoginActivity.class));
                                    finish();
                                }
                            });
                }

                break;
        }

        return true;
    }

    //return to MainActivity when click on phone back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //create patient-disease object in order to upload data in firebase
    public void addDisease(final String name) {

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String patientEmail = snapshot.child("email").getValue(String.class);
                    String patientName = snapshot.child("name").getValue(String.class);
                    String patientSurname = snapshot.child("surname").getValue(String.class);

                    if (patientEmail.trim().toLowerCase().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail().trim().toLowerCase())) {
                        PatientDisease patientDisease = new PatientDisease(patientEmail, patientName, patientSurname, name);
                        String id = diseasesRef.push().getKey();
                        diseasesRef.child(id).setValue(patientDisease);
                        Toast.makeText(DiseasesActivity.this, name + " added!", Toast.LENGTH_SHORT).show();
                        diseaseNameTxt.setText("");
                        DiseasesActivity.this.recreate();
                        return;
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //display a patient's diseases when activity starts
    private void setAdapter(final String position) {

        diseasesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                 * Clear the list for every new search
                 * */
                diseasesNameList.clear();
                resultList.removeAllViews();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String patientName = snapshot.child("patientName").getValue(String.class);
                    String patientSurname = snapshot.child("patientSurname").getValue(String.class);
                    String patientEmail = snapshot.child("patientEmail").getValue(String.class);
                    String diseaseName = snapshot.child("diseaseName").getValue(String.class);

                    if (patientEmail.toLowerCase().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail().toLowerCase())) {
                        diseasesNameList.add(diseaseName);
                    }
                }

                diseaseAdapter = new DiseaseAdapter(DiseasesActivity.this, diseasesNameList, position);
                resultList.setAdapter(diseaseAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
