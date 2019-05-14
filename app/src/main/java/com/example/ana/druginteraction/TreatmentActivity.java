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

public class TreatmentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView diseaseNameTxt;
    private RecyclerView drugList;

    private DatabaseReference diseaseDrugRef;
    private DatabaseReference usersRef;

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private TextView drawerUserText;
    private ImageView drawerImageUser;
    NavigationView mNavigationView;

    private MenuItem patients;
    private MenuItem diseases;
    private MenuItem profile;
    private MenuItem home;
    private MenuItem doctors;

    ArrayList<String> drugsNameList;

    private String disease_name;
    private String diseasePosition;

    DrugsAdapter drugsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.treatment_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>My Treatment</font>"));


        diseaseNameTxt = (TextView) findViewById(R.id.disease_name);

        diseaseDrugRef = FirebaseDatabase.getInstance().getReference().child("DiseaseDrug");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        drugList = (RecyclerView) findViewById(R.id.drug_list);
        drugList.setHasFixedSize(true);
        drugList.setLayoutManager(new LinearLayoutManager(this));
        drugList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

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
        doctors = menu.findItem(R.id.doctors);

        View headerLayout = mNavigationView.getHeaderView(0);

        drawerImageUser = (ImageView) headerLayout.findViewById(R.id.drawerImageUser);
        drawerUserText = (TextView) headerLayout.findViewById(R.id.drawerUserName);

        drugsNameList = new ArrayList<>();

        disease_name = getIntent().getStringExtra("diseaseName");
        diseasePosition = getIntent().getStringExtra("diseasePosition");

        setMenuHeader();
        setAdapter("1");
    }

    public void setMenuHeader() {

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Person person = dataSnapshot1.getValue(Person.class);
                    home.setVisible(true);
                    if (person.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        diseases.setVisible(true);
                        doctors.setVisible(true);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);

        int id = item.getItemId();
        switch (id) {

            case R.id.home:
                Intent home = new Intent(TreatmentActivity.this, MainActivity.class);
                home.putExtra("userEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                startActivity(home);
                break;
            case R.id.userDetails:
                Intent myUserProfile = new Intent(TreatmentActivity.this, UserProfileActivity.class);
                myUserProfile.putExtra("userEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                startActivity(myUserProfile);
                break;
            case R.id.diseases:
                startActivity(new Intent(TreatmentActivity.this, DiseasesActivity.class));
                break;
            case R.id.doctors:
                Intent doctors = new Intent(TreatmentActivity.this, DoctorsActivity.class);
                startActivity(doctors);
                break;
            case R.id.action_logout:
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    AuthUI.getInstance()
                            .signOut(this)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    // user is now signed out
                                    startActivity(new Intent(TreatmentActivity.this, LoginActivity.class));
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
        Intent intent = new Intent(this, DiseasesActivity.class);
        startActivity(intent);
    }

    private void setAdapter(final String position) {

        diseaseDrugRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                 * Clear the list for every new search
                 * */
                drugsNameList.clear();
                drugList.removeAllViews();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String userName = snapshot.child("userName").getValue(String.class);
                    String userSurname = snapshot.child("userSurname").getValue(String.class);
                    String userEmail = snapshot.child("userEmail").getValue(String.class);
                    String diseaseName = snapshot.child("diseaseName").getValue(String.class);
                    String drugName = snapshot.child("drugName").getValue(String.class);

                    if (userEmail.toLowerCase().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail().toLowerCase())) {
                        if (diseaseName.toLowerCase().trim().equals(disease_name.toLowerCase().trim())) {
                            diseaseNameTxt.setText(diseaseName + ":");
                            drugsNameList.add(drugName);
                        }

                    }
                }

                drugsAdapter = new DrugsAdapter(TreatmentActivity.this, drugsNameList, disease_name, position);
                drugList.setAdapter(drugsAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
