package com.example.ana.druginteraction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private MenuItem patients;
    private MenuItem doctors;
    private MenuItem diseases;

    private EditText drugIdField;
    private EditText drugIdField2;

    private EditText searchField;
    private EditText searchField2;

    private ImageButton searchBtn;
    private ImageButton searchBtn2;
    private ImageButton addBtn;
    private ImageButton minimizeBtn;
    private ImageButton addDrug;
    private ImageButton addDrug1;
    private ImageButton addDrug2;

    private RecyclerView resultList;

    private RelativeLayout resultedDetails;
    private RelativeLayout resultedDetails2;

    private TextView resultedName;
    private TextView resultedDescription;
    private TextView resultedCategory;
    private TextView resultedFoodInteraction;

    private TextView resultedName1;
    private TextView resultedDescription1;
    private TextView resultedCategory1;
    private TextView resultedFoodInteraction1;

    private TextView resultedName2;
    private TextView resultedDescription2;
    private TextView resultedCategory2;
    private TextView resultedFoodInteraction2;

    private TextView resultedDrugInteraction;

    private TextView userStatusTxt;
    private TextView userEmailTxt;

    private DatabaseReference drugsRef;
    private DatabaseReference categoryRef;
    private DatabaseReference drugInteractionRef;
    private DatabaseReference foodInteractionRef;
    private DatabaseReference userRef;
    private DatabaseReference diseasesRef;

    private DrawerLayout mDrawer;
    private ActionBarDrawerToggle mToggle;
    private TextView drawerUserText;
    private ImageView drawerImageUser;

    ArrayList<String> diseasesNameList;
    ArrayList<String> drugNameList;
    ArrayList<String> drugIdList;

    PatientDiseaseAdapter patientDiseaseAdapter;
    SearchAdapter searchAdapter;
    NavigationView mNavigationView;

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Drug Interaction</font>"));

        drugsRef = FirebaseDatabase.getInstance().getReference();
        categoryRef = FirebaseDatabase.getInstance().getReference();
        drugInteractionRef = FirebaseDatabase.getInstance().getReference();
        foodInteractionRef = FirebaseDatabase.getInstance().getReference();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        diseasesRef = FirebaseDatabase.getInstance().getReference().child("PatientDisease");

        drugIdField = (EditText) findViewById(R.id.drugId);
        drugIdField2 = (EditText) findViewById(R.id.drugId2);

        searchField = (EditText) findViewById(R.id.search_field);
        searchField2 = (EditText) findViewById(R.id.search_field2);

        searchBtn = (ImageButton) findViewById(R.id.search_btn);
        searchBtn2 = (ImageButton) findViewById(R.id.search_btn2);
        addBtn = (ImageButton) findViewById(R.id.add_drug_btn);
        minimizeBtn = (ImageButton) findViewById(R.id.minimize_btn);
        addDrug = (ImageButton) findViewById(R.id.add_drug);
        addDrug1 = (ImageButton) findViewById(R.id.add_drug1);
        addDrug2 = (ImageButton) findViewById(R.id.add_drug2);

        resultList = (RecyclerView) findViewById(R.id.result_list);
        resultList.setHasFixedSize(true);
        resultList.setLayoutManager(new LinearLayoutManager(this));
        resultList.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        resultedDetails = (RelativeLayout) findViewById(R.id.resultedDetails);
        resultedDetails2 = (RelativeLayout) findViewById(R.id.resultedDetails2);

        resultedName = (TextView) findViewById(R.id.resultedDrugName);
        resultedDescription = (TextView) findViewById(R.id.resultedDrugDescription);
        resultedCategory = (TextView) findViewById(R.id.resultedDrugCategory);
        resultedFoodInteraction = (TextView) findViewById(R.id.resultedFoodInteraction);

        resultedName1 = (TextView) findViewById(R.id.resultedDrugName1);
        resultedDescription1 = (TextView) findViewById(R.id.resultedDrugDescription1);
        resultedCategory1 = (TextView) findViewById(R.id.resultedDrugCategory1);
        resultedFoodInteraction1 = (TextView) findViewById(R.id.resultedFoodInteraction1);

        resultedName2 = (TextView) findViewById(R.id.resultedDrugName2);
        resultedDescription2 = (TextView) findViewById(R.id.resultedDrugDescription2);
        resultedCategory2 = (TextView) findViewById(R.id.resultedDrugCategory2);
        resultedFoodInteraction2 = (TextView) findViewById(R.id.resultedFoodInteraction2);

        resultedDrugInteraction = (TextView) findViewById(R.id.resultedDrugInteraction);
        userStatusTxt = (TextView) findViewById(R.id.userStatus);
        userEmailTxt = (TextView) findViewById(R.id.userEmail);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawer, R.string.open_drawer, R.string.close_drawer);
        mDrawer.addDrawerListener(mToggle);
        mToggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.navigation);
        mNavigationView.bringToFront();
        mNavigationView.setNavigationItemSelectedListener(this);

        Menu menu = mNavigationView.getMenu();
//        getMenuInflater().inflate(R.menu.menu, menu);

        patients = menu.findItem(R.id.patients);
        doctors = menu.findItem(R.id.doctors);
        diseases = menu.findItem(R.id.diseases);

        View headerLayout = mNavigationView.getHeaderView(0);

        drawerImageUser = (ImageView) headerLayout.findViewById(R.id.drawerImageUser);
        drawerUserText = (TextView) headerLayout.findViewById(R.id.drawerUserName);

        drugNameList = new ArrayList<>();
        drugIdList = new ArrayList<>();
        diseasesNameList = new ArrayList<>();

        setDiseasesAdapter("1");

        //show drugs from database that match inserted string
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    setAdapter(s.toString(), "1");
                } else {
                    /*
                     * Clear the list when editText is empty
                     * */
                    drugNameList.clear();
                    drugIdList.clear();
                    resultList.removeAllViews();
                }
            }
        });

        //get intent from adapter using broadcast receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("searchDrug"));

        //show drugs from database that match inserted string
        searchField2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    setAdapter(s.toString(), "2");
                } else {
                    /*
                     * Clear the list when editText is empty
                     * */
                    drugNameList.clear();
                    drugIdList.clear();
                    resultList.removeAllViews();
                }
            }
        });

        //get intent from adapter using broadcast receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("searchDrug"));

        //open search field 2 when button pressed
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchBtn.setVisibility(GONE);
                addBtn.setVisibility(GONE);
                searchField2.setVisibility(View.VISIBLE);
                searchBtn2.setVisibility(View.VISIBLE);
                minimizeBtn.setVisibility(View.VISIBLE);
            }
        });

        //close search field 2 when button pressed
        minimizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                searchBtn.setVisibility(View.VISIBLE);
                addBtn.setVisibility(View.VISIBLE);
                searchField2.setVisibility(GONE);
                searchBtn2.setVisibility(GONE);
                minimizeBtn.setVisibility(GONE);
                searchField2.setText("");
                resultedDetails2.setVisibility(GONE);
            }
        });

        //when pressed call method in order to display searched drug details
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String drugNameToSearch = searchField.getText().toString();
                String drugIdToSearch = drugIdField.getText().toString();
                resultList.setVisibility(GONE);

                if (!drugNameToSearch.trim().equals("")) {
                    resultedDetails.setVisibility(View.VISIBLE);
                    drugDetails(drugNameToSearch, drugIdToSearch, "", "");
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter drug name!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //when pressed call method in order to display searched drug details
        searchBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String drugNameToSearch1 = searchField.getText().toString();
                String drugIdToSearch1 = drugIdField.getText().toString();
                String drugNameToSearch2 = searchField2.getText().toString();
                String drugIdToSearch2 = drugIdField2.getText().toString();

                resultList.setVisibility(GONE);
                resultedDetails.setVisibility(GONE);
                if (drugNameToSearch2.trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter second drug!", Toast.LENGTH_LONG).show();
                } else {
                    if (!drugNameToSearch1.trim().equals("")) {
                        resultedDetails2.setVisibility(View.VISIBLE);
                        drugDetails(drugNameToSearch1, drugIdToSearch1, drugNameToSearch2, drugIdToSearch2);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please enter first drug!", Toast.LENGTH_LONG).show();
                    }

                }

            }
        });

        //add drug to patient/current user disease
        addDrug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String drugNameToAdd = searchField.getText().toString();
                if (userStatusTxt.getText().toString().equals("P")) {
                    addDrug(drugNameToAdd, "1");
                } else if (userStatusTxt.getText().toString().equals("D")) {
                    Intent drug = new Intent(MainActivity.this, SelectPatientActivity.class);
                    drug.putExtra("drugName", drugNameToAdd);
                    startActivity(drug);
                }
            }
        });

        //add drug to patient/current user disease
        addDrug1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String drugNameToAdd = searchField.getText().toString();
                if (userStatusTxt.getText().toString().equals("P")) {
                    addDrug(drugNameToAdd, "1");
                } else if (userStatusTxt.getText().toString().equals("D")) {
                    Intent drug = new Intent(MainActivity.this, SelectPatientActivity.class);
                    drug.putExtra("drugName", drugNameToAdd);
                    startActivity(drug);
                }
            }
        });

        //add drug to patient/current user disease
        addDrug2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String drugNameToAdd = searchField2.getText().toString();
                if (userStatusTxt.getText().toString().equals("P")) {
                    addDrug(drugNameToAdd, "1");
                } else if (userStatusTxt.getText().toString().equals("D")) {
                    Intent drug = new Intent(MainActivity.this, SelectPatientActivity.class);
                    drug.putExtra("drugName", drugNameToAdd);
                    startActivity(drug);
                }
            }
        });

        setMenuHeader();
    }

    //set right menu items for doctor/patient and set user details and photo
    public void setMenuHeader() {

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Person person = dataSnapshot1.getValue(Person.class);
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

    //adapter used in order to display drugs when user inserts string in search field
    private void setAdapter(final String searchedString, final String position) {

        resultList.setVisibility(View.VISIBLE);
        drugsRef.child("drugs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                 * Clear the list for every new search
                 * */
                drugNameList.clear();
                drugIdList.clear();
                resultList.removeAllViews();

                /*
                 * Search all drugs for matching searched string
                 * */
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String drug_name = snapshot.child("name").getValue(String.class);
                    String drug_id = snapshot.child("drugId").getValue(String.class);

                    if (drug_name.toLowerCase().contains(searchedString.toLowerCase())) {
                        drugNameList.add(drug_name);
                        drugIdList.add(drug_id);
                    }
                }

                searchAdapter = new SearchAdapter(MainActivity.this, drugNameList, drugIdList, position);
                resultList.setAdapter(searchAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //get data from adapter through broadcast receiver
    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String drugName = intent.getStringExtra("drugName");
            String drugId = intent.getStringExtra("drugId");
            String position = intent.getStringExtra("position");

            if (position.equals("1")) {
                searchField.setText(drugName);
                drugIdField.setText(drugId);
            } else if (position.equals("2")) {
                searchField2.setText(drugName);
                drugIdField2.setText(drugId);
            }


//            drugNameList.clear();
//            resultList.removeAllViews();
            resultList.setVisibility(GONE);
//            Toast.makeText(MainActivity.this,drugName ,Toast.LENGTH_SHORT).show();
        }
    };

    //retrieve data from firebase if searched drug matches drug from database
    public void drugDetails(final String drugNameToSearch, final String drugIdToSearch,
                            final String drugNameToSearch2, final String drugIdToSearch2) {

        drugsRef.child("drugs").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                 * Search all drugs for matching searched string
                 * */
                String drug_description = "";
                String drug_description2 = "";

                String drug_name1 = "";
                String drug_name2 = "";
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    String drug_name = snapshot.child("name").getValue(String.class);
                    String drug_id = snapshot.child("drugId").getValue(String.class);

//                    if (drug_name.toLowerCase().equals(drugNameToSearch.toLowerCase())) {
//                        drug_description = snapshot.child("description").getValue(String.class);
//                    }
                    if (drug_id.toLowerCase().equals(drugIdToSearch.toLowerCase())) {
                        drug_name1 = snapshot.child("name").getValue(String.class);
                        drug_description = snapshot.child("description").getValue(String.class);
                    }
                    if (!drugNameToSearch2.equals("")) {
//                        if (drug_name.toLowerCase().equals(drugNameToSearch2.toLowerCase())) {
//                            drug_description2 = snapshot.child("description").getValue(String.class);
//                        }
                        if (drug_id.toLowerCase().equals(drugIdToSearch2.toLowerCase())) {
                            drug_name2 = snapshot.child("name").getValue(String.class);
                            drug_description2 = snapshot.child("description").getValue(String.class);
                        }
                    }
                }
                if (drugNameToSearch2.equals("")) {
                    resultedName.setText(drug_name1);
                    resultedDescription.setText(drug_description);
                } else {
                    resultedName1.setText(drug_name1);
                    resultedDescription1.setText(drug_description);
                    resultedName2.setText(drug_name2);
                    resultedDescription2.setText(drug_description2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //retrieve data from firebase if searched drug matches drug from database
        categoryRef.child("category").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                 * Search all drugs for matching searched string
                 * */
                String drug_category = "";
                String drug_category2 = "";
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String drug_id = snapshot.child("drugId").getValue(String.class);

                    if (drug_id.toLowerCase().equals(drugIdToSearch.toLowerCase())) {
                        drug_category += snapshot.child("category").getValue(String.class) + "\n";
                    }
                    if (!drugIdToSearch2.equals("")) {
                        if (drug_id.toLowerCase().equals(drugIdToSearch2.toLowerCase())) {
                            drug_category2 += snapshot.child("category").getValue(String.class) + "\n";
                        }
                    }
                }
                if (drugIdToSearch2.equals("")) {
                    resultedCategory.setText(drug_category);
                } else {
                    resultedCategory1.setText(drug_category);
                    resultedCategory2.setText(drug_category2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //retrieve data from firebase if searched drug matches drug from database
        foodInteractionRef.child("foodInteractions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                 * Search all drugs for matching searched string
                 * */
                String food_interaction = "";
                String food_interaction2 = "";
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String drug_id = snapshot.child("drugId").getValue(String.class);

                    if (drug_id.toLowerCase().equals(drugIdToSearch.toLowerCase())) {
                        if (!snapshot.child("interactions").getValue(String.class).equals("")) {
                            food_interaction += snapshot.child("interactions").getValue(String.class) + "\n";
                        }
                    }
                    if (!drugIdToSearch2.equals("")) {
                        if (drug_id.toLowerCase().equals(drugIdToSearch2.toLowerCase())) {
                            if (!snapshot.child("interactions").getValue(String.class).equals("")) {
                                food_interaction2 += snapshot.child("interactions").getValue(String.class) + "\n";
                            }
                        }
                    }
                }
                if (food_interaction.equals("")) {
                    food_interaction = "This drug does not interact with food!";
                }
                if (food_interaction2.equals("")) {
                    food_interaction2 = "This drug does not interact with food!";
                }
                if (drugIdToSearch2.equals("")) {
                    resultedFoodInteraction.setText(food_interaction);
                } else {
                    resultedFoodInteraction1.setText(food_interaction);
                    resultedFoodInteraction2.setText(food_interaction2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //retrieve data from firebase if searched drug matches drug from database
        drugInteractionRef.child("drugInteractions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                 * Search all drugs for matching searched string
                 * */
                if (!drugIdToSearch2.equals("")) {
                    String drug_interaction = "";
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String drug_id1 = snapshot.child("drug1Id").getValue(String.class);
                        String drug_id2 = snapshot.child("drug2Id").getValue(String.class);

                        if ((drug_id1.toLowerCase().equals(drugIdToSearch.toLowerCase()) &&
                                drug_id2.toLowerCase().equals(drugIdToSearch2.toLowerCase())) ||
                                (drug_id1.toLowerCase().equals(drugIdToSearch2.toLowerCase()) &&
                                        drug_id2.toLowerCase().equals(drugIdToSearch.toLowerCase()))) {
                            drug_interaction = snapshot.child("interaction").getValue(String.class);
                        }
                    }
//                    if(drug_interaction.equals("")) {
//                        drug_interaction = "There is no interaction between the two drugs!";
//                    }
                    resultedDrugInteraction.setText(drug_interaction);
                }
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

    //send intents to right activities when item from menu is pressed
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
        drawer.closeDrawer(GravityCompat.START);

        int id = item.getItemId();
        switch (id) {

            case R.id.userDetails:
                Intent myUserProfile = new Intent(MainActivity.this, UserProfileActivity.class);
                myUserProfile.putExtra("userEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                startActivity(myUserProfile);
                break;
            case R.id.patients:
                Intent myPatients = new Intent(MainActivity.this, PatientsActivity.class);
                myPatients.putExtra("userEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                startActivity(myPatients);
                break;
            case R.id.doctors:
                Intent doctors = new Intent(MainActivity.this, DoctorsActivity.class);
                startActivity(doctors);
                break;
            case R.id.diseases:
                startActivity(new Intent(MainActivity.this, DiseasesActivity.class));
                break;
            case R.id.action_logout:
                if (user != null) {
                    AuthUI.getInstance()
                            .signOut(this)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    // user is now signed out
                                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //insert drug to a disease/patient's disease
    public void addDrug(String drugName, final String position) {
        //add drug to current patient's diseases
        //select disease

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = (View) inflater.inflate(R.layout.dialog_diseases_layout, null);

        RecyclerView rv = (RecyclerView) dialogView.findViewById(R.id.dialog_disease_list);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        AddDrugAdapter adapter = new AddDrugAdapter(MainActivity.this, diseasesNameList, drugName, position);
        rv.setAdapter(adapter);

        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        dialog.show();

    }

    //set disease list using adapter in order to use it when adding a drug to a disease
    private void setDiseasesAdapter(final String position) {

        diseasesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                 * Clear the list for every new search
                 * */
                diseasesNameList.clear();
//                resultDiseaseList.removeAllViews();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String patientName = snapshot.child("patientName").getValue(String.class);
                    String patientSurname = snapshot.child("patientSurname").getValue(String.class);
                    String patientEmail = snapshot.child("patientEmail").getValue(String.class);
                    String diseaseName = snapshot.child("diseaseName").getValue(String.class);

                    if (patientEmail.trim().toLowerCase().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail().trim().toLowerCase())) {
                        diseasesNameList.add(diseaseName);
                    }
                }
//
//                patientDiseaseAdapter = new PatientDiseaseAdapter(PatientDetailsActivity.this, diseasesNameList, position);
//                resultDiseaseList.setAdapter(patientDiseaseAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
