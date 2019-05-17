package com.example.ana.druginteraction;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.view.View.GONE;

public class DrugDetailsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private MenuItem patients;
    private MenuItem doctors;
    private MenuItem diseases;
    private MenuItem home;

    private EditText drugIdField;

    private RelativeLayout resultedDetails;

    private TextView resultedName;
    private TextView resultedDescription;
    private TextView resultedCategory;
    private TextView resultedFoodInteraction;

    private TextView userStatusTxt;
    private TextView userEmailTxt;

    private DatabaseReference drugsRef;
    private DatabaseReference categoryRef;
    private DatabaseReference foodInteractionRef;
    private DatabaseReference userRef;
    private DatabaseReference diseasesRef;

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private TextView drawerUserText;
    private ImageView drawerImageUser;

    NavigationView mNavigationView;

    private String drugId = "";
    private String drugName;

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.drug_details_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Drug Details</font>"));

        drugsRef = FirebaseDatabase.getInstance().getReference();
        categoryRef = FirebaseDatabase.getInstance().getReference();
        foodInteractionRef = FirebaseDatabase.getInstance().getReference();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        diseasesRef = FirebaseDatabase.getInstance().getReference().child("PatientDisease");

        drugIdField = (EditText) findViewById(R.id.drugIdDetails);

        resultedDetails = (RelativeLayout) findViewById(R.id.resultedDrugDetails);

        resultedName = (TextView) findViewById(R.id.resultedDrugDetailsName);
        resultedDescription = (TextView) findViewById(R.id.resultedDrugDetailsDescription);
        resultedCategory = (TextView) findViewById(R.id.resultedDrugDetailsCategory);
        resultedFoodInteraction = (TextView) findViewById(R.id.resultedFoodDetailsInteraction);

        userStatusTxt = (TextView) findViewById(R.id.userStatusDetails);
        userEmailTxt = (TextView) findViewById(R.id.userEmailDetails);

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
        home = menu.findItem(R.id.home);

        View headerLayout = mNavigationView.getHeaderView(0);

        drawerImageUser = (ImageView) headerLayout.findViewById(R.id.drawerImageUser);
        drawerUserText = (TextView) headerLayout.findViewById(R.id.drawerUserName);

        drugName = getIntent().getStringExtra("drugName");

        drugDetails(drugName);

        setMenuHeader();
    }

    //set corresponding menu items for patients/doctors and users photo and details
    public void setMenuHeader() {

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Person person = dataSnapshot1.getValue(Person.class);
                    home.setVisible(true);
                    if (person.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        userEmailTxt.setText(person.getEmail());
                        if (person.getStatus().equals("P")) {
                            userStatusTxt.setText("P");
                            patients.setVisible(false);
                            doctors.setVisible(true);
                            diseases.setVisible(true);
                            drawerUserText.setText(person.getSurname() + " " + person.getName());
                        } else if (person.getStatus().equals("D")) {
                            userStatusTxt.setText("D");
                            diseases.setVisible(false);
                            patients.setVisible(true);
                            drawerUserText.setText("Dr. " + person.getSurname() + " " + person.getName());
                        }
                        Glide.with(getApplicationContext()).load(person.getProfilePicUrl()).into(drawerImageUser);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //search in database for drug and display name and description
    public void drugDetails(final String drugNameToSearch) {

        drugsRef.child("drugs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                 * Search all drugs for matching searched string
                 * */
                String drug_description = "";
                String drug_name1 = "";

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String drug_name = snapshot.child("name").getValue(String.class);
                    String drug_id = snapshot.child("drugId").getValue(String.class);

                    if (drug_name.toLowerCase().equals(drugNameToSearch.toLowerCase())) {
                        drugId = drug_id;
                        drug_name1 = snapshot.child("name").getValue(String.class);
                        drug_description = snapshot.child("description").getValue(String.class);
                    }
                }
                resultedName.setText(drug_name1);
                resultedDescription.setText(drug_description);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //search in database for drug and display categories
        categoryRef.child("category").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                 * Search all drugs for matching searched string
                 * */
                String drug_category = "";
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String drug_id = snapshot.child("drugId").getValue(String.class);

                    if (drug_id.toLowerCase().equals(drugId.toLowerCase())) {
                        drug_category += snapshot.child("category").getValue(String.class) + "\n";
                    }
                }
                resultedCategory.setText(drug_category);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //search in database for drug and display food interaction
        foodInteractionRef.child("foodInteractions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                 * Search all drugs for matching searched string
                 * */
                String food_interaction = "";
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String drug_id = snapshot.child("drugId").getValue(String.class);

                    if (drug_id.toLowerCase().equals(drugId.toLowerCase())) {
                        if (!snapshot.child("interactions").getValue(String.class).equals("")) {
                            food_interaction += snapshot.child("interactions").getValue(String.class) + "\n";
                        }
                    }
                }
                if (food_interaction.equals("")) {
                    food_interaction = "This drug does not interact with food!";
                }

                resultedFoodInteraction.setText(food_interaction);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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

    //redirect to right activity when click on corresponding menu item
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);

        int id = item.getItemId();
        switch (id) {

            case R.id.home:
                Intent home = new Intent(DrugDetailsActivity.this, MainActivity.class);
                home.putExtra("userEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                startActivity(home);
                break;
            case R.id.userDetails:
                Intent myUserProfile = new Intent(DrugDetailsActivity.this, UserProfileActivity.class);
                myUserProfile.putExtra("userEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                startActivity(myUserProfile);
                break;
            case R.id.patients:
                Intent myPatients = new Intent(DrugDetailsActivity.this, PatientsActivity.class);
                myPatients.putExtra("userEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                startActivity(myPatients);
                break;
            case R.id.doctors:
                Intent doctors = new Intent(DrugDetailsActivity.this, DoctorsActivity.class);
                startActivity(doctors);
                break;
            case R.id.diseases:
                startActivity(new Intent(DrugDetailsActivity.this, DiseasesActivity.class));
                break;
            case R.id.action_logout:
                if (user != null) {
                    AuthUI.getInstance()
                            .signOut(this)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    // user is now signed out
                                    startActivity(new Intent(DrugDetailsActivity.this, LoginActivity.class));
                                    finish();
                                }
                            });
                }

                break;
        }

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //go to MainActivity when phone back button is pressed
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
