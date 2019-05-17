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
import android.widget.ImageView;
import android.widget.TextView;

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

public class DoctorsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView resultList;

    private DatabaseReference usersRef;
    private DatabaseReference doctorsRef;

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private TextView drawerUserText;
    private ImageView drawerImageUser;
    NavigationView mNavigationView;

    private MenuItem patients;
    private MenuItem diseases;
    private MenuItem profile;
    private MenuItem home;

    ArrayList<String> doctorEmailList;
    ArrayList<String> doctorEmail_list;
    ArrayList<String> doctorNameList;

    DoctorAdapter doctorAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctors_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>My Doctors</font>"));

        doctorsRef = FirebaseDatabase.getInstance().getReference().child("DoctorPatient");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        resultList = (RecyclerView) findViewById(R.id.doctor_list);
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
        diseases = menu.findItem(R.id.diseases);
        profile = menu.findItem(R.id.userDetails);
        home = menu.findItem(R.id.home);

        View headerLayout = mNavigationView.getHeaderView(0);

        drawerImageUser = (ImageView) headerLayout.findViewById(R.id.drawerImageUser);
        drawerUserText = (TextView) headerLayout.findViewById(R.id.drawerUserName);

        doctorEmailList = new ArrayList<>();
        doctorEmail_list = new ArrayList<>();
        doctorNameList = new ArrayList<>();

        setMenuHeader();
        setAdapter("1");
    }

    //set right menu items and user's photo and details in menu
    public void setMenuHeader() {

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Person person = dataSnapshot1.getValue(Person.class);
                    home.setVisible(true);
                    if (person.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        diseases.setVisible(true);
                        patients.setVisible(false);
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

    //send intents for redirecting to corresponding activities when click on menu items
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);

        int id = item.getItemId();
        switch (id) {

            case R.id.home:
                Intent home = new Intent(DoctorsActivity.this, MainActivity.class);
                home.putExtra("userEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                startActivity(home);
                break;
            case R.id.userDetails:
                Intent myUserProfile = new Intent(DoctorsActivity.this, UserProfileActivity.class);
                myUserProfile.putExtra("userEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                startActivity(myUserProfile);
                break;
            case R.id.diseases:
                startActivity(new Intent(DoctorsActivity.this, DiseasesActivity.class));
                break;
            case R.id.action_logout:
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    AuthUI.getInstance()
                            .signOut(this)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    // user is now signed out
                                    startActivity(new Intent(DoctorsActivity.this, LoginActivity.class));
                                    finish();
                                }
                            });
                }

                break;
        }

        return true;
    }

    //return to MainActivity when press phone back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //display all doctors for the patient logged in when activity starts
    private void setAdapter(final String position) {

        doctorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                 * Clear the list for every new search
                 * */
                doctorEmailList.clear();
//                resultList.removeAllViews();

                /*
                 * Search all names for matching searched string
                 * */
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String patientName = snapshot.child("patientName").getValue(String.class);
                    String patientSurname = snapshot.child("patientSurname").getValue(String.class);
                    String doctorEmail = snapshot.child("doctorEmail").getValue(String.class);
                    String patientEmail = snapshot.child("patientEmail").getValue(String.class);

                    if (patientEmail.toLowerCase().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail().toLowerCase())) {
                        doctorEmailList.add(doctorEmail);
                    }
                }

                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        doctorNameList.clear();
                        resultList.removeAllViews();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String doctorEmail = snapshot.child("email").getValue(String.class);
                            String doctorName = snapshot.child("name").getValue(String.class);
                            String doctorSurname = snapshot.child("surname").getValue(String.class);
                            String doctorStatus = snapshot.child("status").getValue(String.class);

                            for (String email : doctorEmailList) {
                                if (email.toLowerCase().trim().equals(doctorEmail.toLowerCase().trim()) && doctorStatus.equals("D")) {
                                    doctorNameList.add(doctorSurname + doctorName);
                                    doctorEmail_list.add(doctorEmail);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                doctorAdapter = new DoctorAdapter(DoctorsActivity.this, doctorNameList, doctorEmail_list, position);
                resultList.setAdapter(doctorAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
