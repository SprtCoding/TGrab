package com.sprtech.t_grab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Objects;

public class register_activity extends AppCompatActivity {
    EditText email, password, confirm_password;
    CardView signup_btn;
    TextView back_to_login;
    DatabaseReference userRef;
    FirebaseDatabase db;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Var();
        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        userRef = db.getReference("Users");

        back_to_login.setOnClickListener(v -> {
            Intent login = new Intent(this, login_activity.class);
            startActivity(login);
        });

        signup_btn.setOnClickListener(v -> {
            String myEmail = email.getText().toString().trim();
            String myPassword = password.getText().toString().trim();
            String conPass = confirm_password.getText().toString().trim();
            loadingBar.setTitle("Creating Account!");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();


            if(TextUtils.isEmpty(myEmail)) {
                Toast.makeText(this, "Email is required.", Toast.LENGTH_SHORT).show();
                loadingBar.hide();
            } else if(TextUtils.isEmpty(myPassword)) {
                Toast.makeText(this, "Password is required.", Toast.LENGTH_SHORT).show();
                loadingBar.hide();
            }else if(TextUtils.isEmpty(conPass)) {
                Toast.makeText(this, "Password confirmation is required.", Toast.LENGTH_SHORT).show();
                loadingBar.hide();
            }else {
                if(!myPassword.equals(conPass)) {
                    Toast.makeText(this, "Password not match. Try Again!", Toast.LENGTH_SHORT).show();
                    loadingBar.hide();
                }else {
                    mAuth.createUserWithEmailAndPassword(myEmail, myPassword).addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            assert currentUser != null;
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("Email", myEmail);
                            hashMap.put("uid", currentUser.getUid());
                            userRef.child(currentUser.getUid()).setValue(hashMap).addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Account Created Successfully.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(this, registration_as.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    Toast.makeText(getApplicationContext(), "Account not created.", Toast.LENGTH_SHORT).show();
                                }
                                loadingBar.hide();
                            });
                        }else {
                            Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            loadingBar.hide();
                        }
                    });
                }
            }
        });
    }

    private void Var() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        signup_btn = findViewById(R.id.signup_btn);
        back_to_login = findViewById(R.id.login_btn);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
}