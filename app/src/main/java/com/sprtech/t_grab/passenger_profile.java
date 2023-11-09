package com.sprtech.t_grab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class passenger_profile extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference ref;
    FirebaseDatabase db;
    TextView myName, email_text, location_text, phone_text, gender_text;
    ImageView bt_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_profile);
        Var();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("Users");

        bt_back.setOnClickListener(v -> {
            Intent intent = new Intent(passenger_profile.this, passenger_dashboard.class);
            startActivity(intent);
            finish();
        });

        ref.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("Email").getValue(String.class);
                    String loc = snapshot.child("location").getValue(String.class);
                    String phone = snapshot.child("phone_number").getValue(String.class);
                    String gender = snapshot.child("gender").getValue(String.class);
                    myName.setText(name);
                    email_text.setText(email);
                    location_text.setText(loc);
                    phone_text.setText(phone);
                    gender_text.setText(gender);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(passenger_profile.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Var() {
        myName = findViewById(R.id.name);
        email_text = findViewById(R.id.email_text);
        location_text = findViewById(R.id.location_text);
        phone_text = findViewById(R.id.phone_text);
        gender_text = findViewById(R.id.gender_text);
        bt_back = findViewById(R.id.bt_back);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
}