package com.sprtech.t_grab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference userRef;
    FirebaseDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        userRef = db.getReference("Users");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mUser == null) {
            Intent intent = new Intent(this, login_activity.class);
            startActivity(intent);
            finish();
        }else {
            userRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        if(!snapshot.hasChild("who")) {
                            Intent intent = new Intent(MainActivity.this, registration_as.class);
                            startActivity(intent);
                            finish();
                        }else {
                            String who = snapshot.child("who").getValue(String.class);
                            assert who != null;
                            if(who.equals("Driver")) {
                                if(!snapshot.hasChild("name") || !snapshot.hasChild("gender")) {
                                    Intent intent = new Intent(MainActivity.this, driver_information.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    Intent intent = new Intent(MainActivity.this, driver_dashboard.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                            if(who.equals("Passenger")) {
                                if(!snapshot.hasChild("name") || !snapshot.hasChild("gender")) {
                                    Intent intent = new Intent(MainActivity.this, passenger_information.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    if(!snapshot.hasChild("phone_number")) {
                                        Intent intent = new Intent(MainActivity.this, passenger_contact_info.class);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        if(!snapshot.hasChild("location")) {
                                            Intent intent = new Intent(MainActivity.this, passenger_location.class);
                                            startActivity(intent);
                                            finish();
                                        }else {
                                            Intent intent = new Intent(MainActivity.this, passenger_dashboard.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                }
                            }

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}