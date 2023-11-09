package com.sprtech.t_grab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class registration_as extends AppCompatActivity {
    private TextView who_text_spinner;
    private Spinner who_spinner;
    private CardView next_btn;
    private DatabaseReference userRef;
    FirebaseDatabase db;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_as);
        Var();
        loadingBar = new ProgressDialog(this);

        whoList();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        db = FirebaseDatabase.getInstance();
        userRef = db.getReference("Users");

        next_btn.setOnClickListener(v -> {
            loadingBar.setTitle("Loading");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            if(who_text_spinner.getText().toString().equals("Select")) {
                Toast.makeText(getApplicationContext(), "please select..", Toast.LENGTH_SHORT).show();
                loadingBar.hide();
            }else {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("who", who_text_spinner.getText().toString());
                userRef.child(mUser.getUid()).updateChildren(hashMap);
                userRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            if(snapshot.hasChild("who")) {
                                String who = snapshot.child("who").getValue(String.class);
                                if(who != null) {
                                    if(who.equals("Driver")) {
                                        Intent intent = new Intent(registration_as.this, driver_information.class);
                                        startActivity(intent);
                                        finish();
                                        loadingBar.hide();
                                    }
                                    if(who.equals("Passenger")) {
                                        Intent intent = new Intent(registration_as.this, passenger_information.class);
                                        startActivity(intent);
                                        finish();
                                        loadingBar.hide();
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(registration_as.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void Var() {
        who_text_spinner = findViewById(R.id.who_text_spinner);
        who_spinner = findViewById(R.id.who_spinner);
        next_btn = findViewById(R.id.next_btn);
    }

    private void whoList() {
        ArrayList<String> whoTypeList = new ArrayList<>();

        whoTypeList.add("Select");
        whoTypeList.add("Driver");
        whoTypeList.add("Passenger");

        who_spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, whoTypeList));
        who_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                who_text_spinner.setText(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(registration_as.this, "Please select", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
}