package com.sprtech.t_grab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sprtech.t_grab.Adapters.NotificationAdapter;
import com.sprtech.t_grab.Adapters.TricycleAdapter;
import com.sprtech.t_grab.Model.acceptModel;
import com.sprtech.t_grab.Model.tricycleModel;

import java.util.ArrayList;
import java.util.Objects;

public class passenger_notification extends AppCompatActivity {
    Toolbar mToolbar;
    RecyclerView notification_view;
    LinearLayout no_available;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    FirebaseDatabase db;
    DatabaseReference ref;
    ArrayList<acceptModel> myModel;
    NotificationAdapter notificationAdapter;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_notification);
        Var();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        myModel =new ArrayList<>();

        db = FirebaseDatabase.getInstance();
        ref = db.getReference("acceptBooking");

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ref.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myModel.clear();
                if(snapshot.exists()) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        acceptModel model = dataSnapshot.getValue(acceptModel.class);
                        String who = "Driver";
                        assert model != null;
                        myModel.add(model);
                        notificationAdapter = new NotificationAdapter(myModel, getApplicationContext());
                        notification_view.setAdapter(notificationAdapter);
                    }
                    no_available.setVisibility(View.GONE);
                    notification_view.setVisibility(View.VISIBLE);
                }else {
                    no_available.setVisibility(View.VISIBLE);
                    notification_view.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(passenger_notification.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Var() {
        mToolbar = findViewById(R.id.main_page_toolbar);
        notification_view = findViewById(R.id.notification_view);
        no_available = findViewById(R.id.no_available);

        linearLayoutManager = new LinearLayoutManager(this);
        notification_view.setHasFixedSize(true);
        notification_view.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
}