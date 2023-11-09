package com.sprtech.t_grab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class booking_activity extends AppCompatActivity {
    EditText name_input, phone_input, destination_input, date_input;
    CardView book_btn;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference ref;
    FirebaseDatabase db;
    String DriverUID;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        Var();

        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DriverUID = getIntent().getExtras().get("visit_user_id").toString();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        ref = db.getReference("booking");

        book_btn.setOnClickListener(v -> {
            String name = name_input.getText().toString();
            String phone = phone_input.getText().toString();
            String destination = destination_input.getText().toString();
            String date = date_input.getText().toString();

            if(TextUtils.isEmpty(name) && TextUtils.isEmpty(phone) && TextUtils.isEmpty(destination) && TextUtils.isEmpty(date)) {
                Toast.makeText(this, "Field is empty!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(name)) {
                Toast.makeText(this, "Name is required!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(phone)) {
                Toast.makeText(this, "Phone is required!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(destination)) {
                Toast.makeText(this, "Destination is required!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(date)) {
                Toast.makeText(this, "Date is required!", Toast.LENGTH_SHORT).show();
            }else {
                HashMap<String, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("phone", phone);
                map.put("destination", destination);
                map.put("date", date);
                map.put("passenger_uid", mUser.getUid());
                ref.child(DriverUID).child(mUser.getUid()).setValue(map).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Toast.makeText(this, "Book Successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(booking_activity.this, list_of_tricycle.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }

    private void Var() {
        name_input = findViewById(R.id.name_input);
        phone_input = findViewById(R.id.phone_input);
        destination_input = findViewById(R.id.destination_input);
        date_input = findViewById(R.id.date_input);
        book_btn = findViewById(R.id.book_btn);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
}