package com.sprtech.t_grab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sprtech.t_grab.Adapters.TricycleAdapter;
import com.sprtech.t_grab.Model.tricycleModel;

import java.util.ArrayList;
import java.util.Objects;

public class list_of_tricycle extends AppCompatActivity {
    Toolbar mToolbar;
    DatabaseReference userRef;
    FirebaseDatabase database;
    FirebaseUser mUser;
    FirebaseAuth mAuth;
    RecyclerView list_of_tricycle;
    LinearLayoutManager linearLayoutManager;
    ArrayList<tricycleModel> myModel;
    TricycleAdapter myTricycleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_tricycle);

        list_of_tricycle = findViewById(R.id.list_of_tricycle);
        linearLayoutManager = new LinearLayoutManager(this);
        list_of_tricycle.setHasFixedSize(true);
        list_of_tricycle.setLayoutManager(linearLayoutManager);

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myModel =new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("Users");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myModel.clear();

                if(snapshot.exists()) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        tricycleModel model = dataSnapshot.getValue(tricycleModel.class);
                        String who = "Driver";
                        assert model != null;
                        if(who.equals(model.getWho())) {
                            myModel.add(model);
                        }
                        myTricycleAdapter = new TricycleAdapter(myModel, getApplicationContext());
                        list_of_tricycle.setAdapter(myTricycleAdapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(list_of_tricycle.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
}