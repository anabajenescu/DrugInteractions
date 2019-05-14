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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.List;


public class PatientDetailsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<String> diseasesNameList;

    private DatabaseReference usersRef;
    private DatabaseReference diseasesRef;

    private RecyclerView resultDiseaseList;

    private ImageView logoView;

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private TextView drawerUserText;
    private ImageView drawerImageUser;
    NavigationView mNavigationView;

    private MenuItem patients;
    private MenuItem diseases;
    private MenuItem profile;
    private MenuItem home;

    ListView listViewPatientDetails;
    List<Person> patientList;

    private String patientFullName;
    private String patient_email;
    private String patientPosition;
    private String userID;
    private String name, surname, email, phone, age, gender, photo;

    Intent intent;

    PatientDiseaseAdapter patientDiseaseAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_details_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Patient's Profile</font>"));

//        intent = new Intent("patientDetails");

        listViewPatientDetails = (ListView) findViewById(R.id.listViewPatientDetails);
        patientList = new ArrayList<>();

        logoView = (ImageView) findViewById(R.id.imageViewDrug);

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        diseasesRef = FirebaseDatabase.getInstance().getReference().child("PatientDisease");

        resultDiseaseList = (RecyclerView) findViewById(R.id.result_disease_list);
        resultDiseaseList.setHasFixedSize(true);
        resultDiseaseList.setLayoutManager(new LinearLayoutManager(this));
        resultDiseaseList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        diseasesNameList = new ArrayList<>();

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

//        Intent intent = getIntent();
        patientFullName = getIntent().getStringExtra("patientName");
        patientPosition = getIntent().getStringExtra("patientPosition");

        patient_email = "";

        setAdapter("1");
        setMenuHeader();
    }

    public void setMenuHeader() {

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Person person = dataSnapshot1.getValue(Person.class);
                    if (person.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        diseases.setVisible(false);
                        profile.setVisible(true);
                        patients.setVisible(true);
                        home.setVisible(true);
                        drawerUserText.setText("Dr. " + person.getSurname() + " " + person.getName());
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);

        int id = item.getItemId();
        switch (id) {

            case R.id.home:
                Intent home = new Intent(PatientDetailsActivity.this, MainActivity.class);
                home.putExtra("userEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                startActivity(home);
                break;
            case R.id.userDetails:
                Intent myUserProfile = new Intent(PatientDetailsActivity.this, UserProfileActivity.class);
                myUserProfile.putExtra("userEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                startActivity(myUserProfile);
                break;
            case R.id.patients:
                Intent myPatients = new Intent(PatientDetailsActivity.this, PatientsActivity.class);
                myPatients.putExtra("userEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                startActivity(myPatients);
                break;
            case R.id.diseases:
                startActivity(new Intent(PatientDetailsActivity.this, DiseasesActivity.class));
                break;
            case R.id.action_logout:
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    AuthUI.getInstance()
                            .signOut(this)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    // user is now signed out
                                    startActivity(new Intent(PatientDetailsActivity.this, LoginActivity.class));
                                    finish();
                                }
                            });
                }

                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, PatientsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!TextUtils.isEmpty(patientFullName)) {
            usersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Person user = data.getValue(Person.class);
                        if ((user.getSurname() + " " + user.getName()).trim().toLowerCase().equals(patientFullName.trim().toLowerCase())) {

                            if (user.getGender() != null && user.getGender().equals("M")) {
                                user.setGender("Male");
                                gender = "M";
                            } else if (user.getGender() != null && user.getGender().equals("F")) {
                                user.setGender("Female");
                                gender = "F";
                            } else {
                                user.setGender("Other");
                                gender = "O";
                            }

                            patientList.add(user);
                            userID = data.getKey();
                            name = user.getName();
                            surname = user.getSurname();
                            email = user.getEmail();
                            phone = user.getPhone();
                            age = user.getAge();
                            photo = user.getProfilePicUrl();
                            Glide.with(getApplicationContext()).load(photo).into(logoView);
                        }
                    }
                    PatientList adapter = new PatientList(PatientDetailsActivity.this, patientList);
                    listViewPatientDetails.setAdapter(adapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void setAdapter(final String position) {

        diseasesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                 * Clear the list for every new search
                 * */
                diseasesNameList.clear();
                resultDiseaseList.removeAllViews();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String patientName = snapshot.child("patientName").getValue(String.class);
                    String patientSurname = snapshot.child("patientSurname").getValue(String.class);
                    String patientEmail = snapshot.child("patientEmail").getValue(String.class);
                    String diseaseName = snapshot.child("diseaseName").getValue(String.class);

                    if (patientFullName.trim().toLowerCase().equals((patientSurname + " " + patientName).trim().toLowerCase())) {
                        diseasesNameList.add(diseaseName);
                        patient_email = patientEmail;
                    }
                }

                patientDiseaseAdapter = new PatientDiseaseAdapter(PatientDetailsActivity.this, diseasesNameList, patient_email, position);
                resultDiseaseList.setAdapter(patientDiseaseAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
