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

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class passenger_contact_info extends AppCompatActivity {
    CountryCodePicker ccp;
    private EditText phone_number;
    private CardView next_btn;
    private TextView name;
    DatabaseReference userRef;
    FirebaseDatabase db;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_contact_info);
        Var();

        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        userRef = db.getReference("Users");

        userRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String myName = snapshot.child("name").getValue(String.class);
                    name.setText(myName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(passenger_contact_info.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        next_btn.setOnClickListener(v -> {
            String number = phone_number.getText().toString().trim();

            if(TextUtils.isEmpty(number)) {
                Toast.makeText(passenger_contact_info.this, "number is required!", Toast.LENGTH_SHORT).show();
            }else {
                if(number.length() == 10) {
                    loadingBar.setTitle("Loading");
                    loadingBar.setMessage("Please wait...");
                    loadingBar.show();
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("phone_number", ccp.getSelectedCountryCodeWithPlus() + number);
                    userRef.child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(task1 -> {
                        loadingBar.dismiss();
                        Intent intent = new Intent(passenger_contact_info.this, passenger_location.class);
                        startActivity(intent);
                        finish();
                    });
//                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                            ccp.getSelectedCountryCodeWithPlus() + number,
//                            60,
//                            TimeUnit.SECONDS,
//                            passenger_contact_info.this,
//                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                                @Override
//                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                                    loadingBar.dismiss();
//                                }
//
//                                @Override
//                                public void onVerificationFailed(@NonNull FirebaseException e) {
//                                    Toast.makeText(passenger_contact_info.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                                    loadingBar.dismiss();
//                                }
//
//                                @Override
//                                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                                    super.onCodeSent(s, forceResendingToken);
//
//                                }
//                            }
//                    );
                }else {
                    Toast.makeText(passenger_contact_info.this, "Enter correct number!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void Var() {
        ccp = findViewById(R.id.ccp);
        phone_number = findViewById(R.id.phone_number);
        next_btn = findViewById(R.id.next_btn);
        name = findViewById(R.id.name);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
}