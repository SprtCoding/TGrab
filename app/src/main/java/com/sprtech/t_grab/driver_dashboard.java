package com.sprtech.t_grab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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
import com.sprtech.t_grab.Adapters.BookingAdapter;
import com.sprtech.t_grab.Model.BookModel;

import java.util.ArrayList;
import java.util.Objects;

public class driver_dashboard extends AppCompatActivity {
    LinearLayout bt_ready;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle mToogle;
    Toolbar mToolbar;
    TextView username;
    DatabaseReference userRef, bookingRef;
    FirebaseDatabase database;
    ArrayList<BookModel> myModel;
    BookingAdapter bookingAdapter;
    RecyclerView booking;
    LinearLayoutManager linearLayoutManager;
    LinearLayout no_available;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_dashboard);
        Var();

        myModel =new ArrayList<>();

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
        bookingRef = database.getReference("booking");

        bookingRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myModel.clear();

                if(snapshot.exists()) {
                    no_available.setVisibility(View.GONE);
                    booking.setVisibility(View.VISIBLE);
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        BookModel model = dataSnapshot.getValue(BookModel.class);
                        assert model != null;
                        myModel.add(model);
                        bookingAdapter = new BookingAdapter(myModel, getApplicationContext());
                        booking.setAdapter(bookingAdapter);
                    }
                }else {
                    no_available.setVisibility(View.VISIBLE);
                    booking.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(driver_dashboard.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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
                Toast.makeText(driver_dashboard.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            UserMenuSelector(item);
            return false;
        });

        bt_ready.setOnClickListener(v -> {
            Intent intent = new Intent(driver_dashboard.this, driver_map.class);
            startActivity(intent);
        });
    }

    //navigation buttons functions
    @SuppressLint("NonConstantResourceId")
    private void UserMenuSelector(MenuItem item) {

        switch (item.getItemId()){
            case R.id.profile:
                Toast.makeText(this, "profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.overview:
                Toast.makeText(this, "history", Toast.LENGTH_SHORT).show();
                break;

            case R.id.developers:
                Intent about = new Intent(driver_dashboard.this, about.class);
                startActivity(about);
                break;

            case R.id.sign_out:
                mAuth.signOut();
                Intent sign_out = new Intent(driver_dashboard.this, login_activity.class);
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

    private void Var() {
        bt_ready = findViewById(R.id.bt_ready);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_list);
        username = findViewById(R.id.name);
        no_available = findViewById(R.id.no_available);

        booking = findViewById(R.id.booking);
        linearLayoutManager = new LinearLayoutManager(this);
        booking.setHasFixedSize(true);
        booking.setLayoutManager(linearLayoutManager);
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

        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> Toast.makeText(driver_dashboard.this, "Exit cancel!", Toast.LENGTH_SHORT).show());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}