package com.example.ana.druginteraction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CHOOSE_IMAGE = 102;
    private Button registerUserButton;
    private EditText emailText;
    private EditText passwordText, passwordText2;
    private EditText nameText, surnameText, ageText, phoneText;
    private RadioGroup status;
    private RadioGroup gender;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userRef;

    private String email;
    private String name, surname;
    private String age;
    private String phone;
    private String radioStatus;
    private String radioGender;
    private ImageView imageViewCamera;
    private Uri uriProfileImage;
    private String profileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>Register</font>"));

        firebaseAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");


        progressDialog = new ProgressDialog(this);
        registerUserButton = (Button) findViewById(R.id.nextButton);

        emailText = (EditText) findViewById(R.id.emailText);
        phoneText = (EditText) findViewById(R.id.phoneText);
        passwordText = (EditText) findViewById(R.id.passwordText);
        passwordText2 = (EditText) findViewById(R.id.passwordText2);
        nameText = (EditText) findViewById(R.id.nameUser);
        surnameText = (EditText) findViewById(R.id.surnameUser);
        ageText = (EditText) findViewById(R.id.ageUser);
        status = (RadioGroup) findViewById(R.id.statusRadio);
        gender = (RadioGroup) findViewById(R.id.genderRadio);

        registerUserButton.setOnClickListener(this);
        imageViewCamera = (ImageView) findViewById(R.id.imageViewCamera);
        imageViewCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

    }

    public void onRadioGenderClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_male:
                if (checked)
                    radioGender = "M";
                break;
            case R.id.radio_female:
                if (checked)
                    radioGender = "F";
                break;
            case R.id.radio_other:
                if (checked)
                    radioGender = "O";
                break;
        }
    }

    public void onRadioStatusClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_doctor:
                if (checked)
                    radioStatus = "D";
                break;
            case R.id.radio_patient:
                if (checked)
                    radioStatus = "P";
                break;
        }
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), CHOOSE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imageViewCamera.setImageBitmap(bitmap);
                uploadImageToFirebase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebase() {
        StorageReference profileImgRef = FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");

        if (uriProfileImage != null) {

            profileImgRef.putFile(uriProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            profileImageUrl = uri.toString();
                        }
                    });

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to upload image!", Toast.LENGTH_SHORT).show();

                        }
                    });
        }

    }

    @Override
    public void onClick(View view) {
        if (view == registerUserButton) {
            registerUser();
        }

    }

    private void registerUser() {

        String regex = "^[+]?[0-9]{10,13}$";

        email = emailText.getText().toString().trim();
        phone = phoneText.getText().toString().trim();
        String password = passwordText.getText().toString().trim();
        String password2 = passwordText2.getText().toString().trim();

        name = nameText.getText().toString().trim();
        surname = surnameText.getText().toString().trim();
        age = ageText.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please enter your name!", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(surname)) {
            Toast.makeText(this, "Please enter your surname!", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(age)) {
            Toast.makeText(this, "Please enter your age!", Toast.LENGTH_LONG).show();
            return;
        }

        if (!phone.matches(regex)) {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter email!", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!password.equals(password2)) {
            Toast.makeText(this, "Passwords don't match!", Toast.LENGTH_LONG).show();
            return;
        }

        if (gender.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select your gender!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (status.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select Status (doctor/patient)!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Please wait....");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            insertUser();
                            Toast.makeText(RegisterActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                            finish();
                            Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(loginIntent);
                        } else {
                            Toast.makeText(RegisterActivity.this, "Account already created!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void insertUser() {
        String id = userRef.push().getKey();
        Person user = new Person(name, surname, email, phone, age, radioGender, radioStatus, profileImageUrl);
        userRef.child(id).setValue(user);
    }

//    private String generateUniqueToken() {
//
//        final DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference().child("Tokens");
//        String token = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
//        token += "_" + sdf.format(Calendar.getInstance().getTime());
//
//        String id = tokenRef.push().getKey();
//        tokenRef.child(id).setValue(token);
//        tokenRef.orderByChild("token").equalTo(token)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if(dataSnapshot.exists()){
//                            generateUniqueToken();
//                        } else {
//                            String id = tokenRef.push().getKey();
//                            tokenRef.child(id).setValue(token);
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
//
//        return token;
//    }

}
