package com.sprtech.t_grab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class login_activity extends AppCompatActivity {
    TextView signup_btn;
    FirebaseAuth mAuth;
    DatabaseReference userRef;
    FirebaseDatabase db;
    ProgressDialog loadingBar;
    CardView login_btn;
    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myVar();

        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        userRef = db.getReference("Users");

        signup_btn.setOnClickListener(v -> {
            Intent register = new Intent(this, register_activity.class);
            startActivity(register);
        });

        login_btn.setOnClickListener(v -> {
            loadingBar.setTitle("Logging In");
            loadingBar.setMessage("Please wait...");
            loadingBar.show();

            String myEmail = email.getText().toString().trim();
            String myPass = password.getText().toString().trim();

            if(TextUtils.isEmpty(myEmail) && TextUtils.isEmpty(myPass)) {
                Toast.makeText(login_activity.this, "Email and Password is required!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(myEmail)) {
                Toast.makeText(login_activity.this, "Email is required!", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(myPass)) {
                Toast.makeText(login_activity.this, "Password is required!", Toast.LENGTH_SHORT).show();
            }else {
                mAuth.signInWithEmailAndPassword(myEmail, myPass).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        assert user != null;
                        userRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()) {
                                    String who = snapshot.child("who").getValue(String.class);
                                    assert who != null;
                                    if(who.equals("Passenger")) {
                                        Intent intent = new Intent(login_activity.this, passenger_dashboard.class);
                                        startActivity(intent);
                                        finish();
                                        loadingBar.dismiss();
                                    }
                                    if(who.equals("Driver")) {
                                        Intent intent = new Intent(login_activity.this, driver_dashboard.class);
                                        startActivity(intent);
                                        finish();
                                        loadingBar.dismiss();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(login_activity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        });
                    }else {
                        Toast.makeText(login_activity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                });
            }

        });
    }

    private void myVar() {
        signup_btn = findViewById(R.id.signup_btn);
        login_btn = findViewById(R.id.login_btn);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
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

        alertDialogBuilder.setNegativeButton("No", (dialog, which) -> Toast.makeText(login_activity.this, "Exit cancel!", Toast.LENGTH_SHORT).show());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}