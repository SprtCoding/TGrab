package com.sprtech.t_grab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class passenger_information extends AppCompatActivity {
    private EditText name;
    private TextView gender_text_spinner;
    private Spinner gender_spinner;
    private CardView next_btn;
    private DatabaseReference userRef;
    FirebaseDatabase db;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_information);
        Var();
        genderList();
        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        userRef = db.getReference("Users");

        next_btn.setOnClickListener(v -> {
            loadingBar.setTitle("Loading");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            String myName = name.getText().toString();
            String myGender = gender_text_spinner.getText().toString();

            if(TextUtils.isEmpty(myName)) {
                Toast.makeText(this, "Name is required.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }else if(myGender.equals("Select")) {
                Toast.makeText(this, "please select your gender.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }else {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("name", myName);
                hashMap.put("gender", myGender);
                userRef.child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Intent intent = new Intent(passenger_information.this, passenger_contact_info.class);
                        startActivity(intent);
                        finish();
                        loadingBar.dismiss();
                    }
                });
            }
        });
    }

    private void Var() {
        gender_text_spinner = findViewById(R.id.who_text_spinner);
        gender_spinner = findViewById(R.id.who_spinner);
        next_btn = findViewById(R.id.next_btn);
        name = findViewById(R.id.name);
    }

    private void genderList() {
        ArrayList<String> genderTypeList = new ArrayList<>();

        genderTypeList.add("Select");
        genderTypeList.add("Male");
        genderTypeList.add("Female");

        gender_spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genderTypeList));
        gender_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender_text_spinner.setText(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(passenger_information.this, "Please select", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
}