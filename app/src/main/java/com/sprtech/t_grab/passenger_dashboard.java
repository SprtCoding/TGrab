package com.sprtech.t_grab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class passenger_dashboard extends AppCompatActivity {
    TextView username;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    CardView para_tricycle, para_booking, para_emergency, notification;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle mToogle;
    Toolbar mToolbar;
    DatabaseReference userRef;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_dashboard);
        Var();
        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");

        mToogle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(mToogle);
        mToogle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("Users");

        userRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String consumer_name = snapshot.child("name").getValue(String.class);
                    username.setText(consumer_name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(passenger_dashboard.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        para_tricycle.setOnClickListener(v -> gotoParaTricycle());

        para_booking.setOnClickListener(v -> gotoParaBooking());

        para_emergency.setOnClickListener(v -> gotoParaEmergency());

        notification.setOnClickListener(v -> gotoNotification());

        navigationView.setNavigationItemSelectedListener(item -> {
            UserMenuSelector(item);
            return false;
        });
    }

    //navigation buttons functions
    @SuppressLint("NonConstantResourceId")
    private void UserMenuSelector(MenuItem item) {

        switch (item.getItemId()){
            case R.id.profile:
                Intent profile = new Intent(passenger_dashboard.this, passenger_profile.class);
                startActivity(profile);
                break;

            case R.id.overview:
                Toast.makeText(this, "history", Toast.LENGTH_SHORT).show();
                break;

            case R.id.developers:
                Intent about = new Intent(passenger_dashboard.this, about.class);
                startActivity(about);
                break;

            case R.id.sign_out:
                mAuth.signOut();
                Intent sign_out = new Intent(passenger_dashboard.this, login_activity.class);
                startActivity(sign_out);
                finish();
                break;

            default:
                Toast.makeText(getApplicationContext(), "No one selected!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(mToogle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void gotoNotification() {
        Intent intent = new Intent(passenger_dashboard.this, passenger_notification.class);
        startActivity(intent);
    }

    private void gotoParaTricycle() {
        Intent intent = new Intent(passenger_dashboard.this, para_tricycle_map.class);
        startActivity(intent);
    }

    private void gotoParaBooking() {
        Intent intent = new Intent(passenger_dashboard.this, list_of_tricycle.class);
        startActivity(intent);
    }

    private void gotoParaEmergency() {
        Intent intent = new Intent(passenger_dashboard.this, emergency_activity.class);
        startActivity(intent);
    }

    private void Var() {
        para_tricycle = findViewById(R.id.para_tricycle);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_list);
        username = findViewById(R.id.name);
        para_booking = findViewById(R.id.para_booking);
        para_emergency = findViewById(R.id.para_emergency);
        notification = findViewById(R.id.notification);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(Html.fromHtml("<font color='#000000'>Are you sure you want to exit?</font>"));
        alertDialogBuilder.setPositiveButton("Yes",
                (arg0, arg1) -> {
                    finishAffinity(); //Force Exit
                });

        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> Toast.makeText(passenger_dashboard.this, "Exit cancel!", Toast.LENGTH_SHORT).show());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}