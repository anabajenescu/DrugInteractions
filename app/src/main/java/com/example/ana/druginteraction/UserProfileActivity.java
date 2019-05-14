package com.example.ana.druginteraction;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.data.model.User;
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
import java.util.List;

public class UserProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DatabaseReference userRef;

    private String userEmail;
    private ImageView logoView;
    private String userID;
    private String name, surname, email, phone, age, gender, status, photo;
    private TextView textView;

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

    ListView listViewInfo;
    List<Person> userList;

    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>My Profile</font>"));

        listViewInfo = (ListView) findViewById(R.id.listViewInfo);
        logoView = (ImageView) findViewById(R.id.imageViewDrug);
        textView = (TextView) findViewById(R.id.textUpdate);
        userList = new ArrayList<>();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

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

        setMenuHeader();

        listViewInfo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Person user = userList.get(position);
                showUpdateDialog();
                return false;
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent() != null) {
            userEmail = getIntent().getStringExtra("userEmail");
        }
        if (!TextUtils.isEmpty(userEmail)) {
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
            ref.orderByChild("email").equalTo(userEmail).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        Person user = data.getValue(Person.class);
                        Glide.with(getApplicationContext()).load(user.getProfilePicUrl()).into(logoView);
                        if (user.getStatus() != null && user.getStatus().equals("D")) {
                            user.setStatus("Doctor");
                            status = "D";
                        } else {
                            user.setStatus("Patient");
                            status = "P";
                        }

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

                        userList.add(user);
                        userID = data.getKey();
                        name = user.getName();
                        surname = user.getSurname();
                        email = user.getEmail();
                        phone = user.getPhone();
                        age = user.getAge();
                        photo = user.getProfilePicUrl();

                    }
                    UserList adapter = new UserList(UserProfileActivity.this, userList);
                    listViewInfo.setAdapter(adapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void setMenuHeader() {

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Person person = dataSnapshot1.getValue(Person.class);
                    profile.setVisible(false);
                    home.setVisible(true);
                    if (person.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                        if (person.getStatus().equals("P")) {
                            patients.setVisible(false);
                            doctors.setVisible(true);
                            diseases.setVisible(true);
                            drawerUserText.setText(person.getSurname() + " " + person.getName());
                        } else if (person.getStatus().equals("D")) {
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

    private void showUpdateDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater layoutInflater = getLayoutInflater();

        final View dialogView = layoutInflater.inflate(R.layout.update_user_info, null);
        dialogBuilder.setView(dialogView);

        final TextView textViewName = (TextView) dialogView.findViewById(R.id.textViewEditName);
        final EditText editTextName = (EditText) dialogView.findViewById(R.id.newValueName);
        editTextName.setText(name);

        final TextView textViewSurname = (TextView) dialogView.findViewById(R.id.textViewEditSurname);
        final EditText editTextSurname = (EditText) dialogView.findViewById(R.id.newValueSurname);
        editTextSurname.setText(surname);

        final TextView textViewGender = (TextView) dialogView.findViewById(R.id.textViewEditGender);
        final EditText editTextGender = (EditText) dialogView.findViewById(R.id.newValueGender);
        editTextGender.setText(gender);

        final TextView textViewAge = (TextView) dialogView.findViewById(R.id.textViewEditAge);
        final EditText editTextAge = (EditText) dialogView.findViewById(R.id.newValueAge);
        editTextAge.setText(age);

        final TextView textViewPhone = (TextView) dialogView.findViewById(R.id.textViewEditPhone);
        final EditText editTextPhone = (EditText) dialogView.findViewById(R.id.newValuePhone);
        editTextPhone.setText(phone);

        final TextView textViewEmail = (TextView) dialogView.findViewById(R.id.textViewEditEmail);
        final EditText editTextEmail = (EditText) dialogView.findViewById(R.id.newValueEmail);
        editTextEmail.setText(email);

        final Button button = (Button) dialogView.findViewById(R.id.buttonUpdate);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = editTextName.getText().toString();
                String userSurname = editTextSurname.getText().toString();
                String userGender = editTextGender.getText().toString();
                String userAge = editTextAge.getText().toString();
                String userPhone = editTextPhone.getText().toString();
                String userEmail = editTextEmail.getText().toString();

                String regex = "^[+]?[0-9]{10,13}$";

                if (TextUtils.isEmpty(userName) && TextUtils.isEmpty(userSurname) && TextUtils.isEmpty(userGender)
                        && TextUtils.isEmpty(userAge) && TextUtils.isEmpty(userPhone) && TextUtils.isEmpty(userEmail)) {
                    Toast.makeText(UserProfileActivity.this, "Please complete the fields!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(userName)) {
                    userName = name;
                }
                if (TextUtils.isEmpty(userSurname)) {
                    userSurname = surname;
                }
                if (TextUtils.isEmpty(userGender)) {
                    userGender = gender;
                }
                if (TextUtils.isEmpty(userAge)) {
                    userAge = age;
                }
                if (!phone.matches(regex)) {
                    userPhone = phone;
                }
                if (TextUtils.isEmpty(userEmail)) {
                    userEmail = email;
                }

                updateInfo(userName, userSurname, status, userGender, userAge, userPhone, userEmail, photo);
            }

        });

    }

    private boolean updateInfo(String name, String surname, String status, String gender, String age, String phone, String email, String photo) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);
        Person user = new Person(name, surname, email, phone, age, gender, status, photo);
        userList.remove(0);
        userList.add(user);
        reference.setValue(user);
        this.recreate();
        Toast.makeText(this, "Update complete!", Toast.LENGTH_SHORT).show();
        return true;
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
                Intent home = new Intent(UserProfileActivity.this, MainActivity.class);
                home.putExtra("userEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                startActivity(home);
                break;
            case R.id.patients:
                Intent myPatients = new Intent(UserProfileActivity.this, PatientsActivity.class);
                myPatients.putExtra("userEmail", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                startActivity(myPatients);
                break;
            case R.id.diseases:
                startActivity(new Intent(UserProfileActivity.this, DiseasesActivity.class));
                break;
            case R.id.doctors:
                Intent doctors = new Intent(UserProfileActivity.this, DoctorsActivity.class);
                startActivity(doctors);
                break;
            case R.id.action_logout:
                if (user != null) {
                    AuthUI.getInstance()
                            .signOut(this)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                public void onComplete(@NonNull Task<Void> task) {
                                    // user is now signed out
                                    startActivity(new Intent(UserProfileActivity.this, LoginActivity.class));
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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}

