package com.sprtech.t_grab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class passenger_otp_verification extends AppCompatActivity {
    EditText first_num, second_num, third_num, fourth_num, fifth_num, last_num;
    CardView submit_btn;
    TextView mobile;
    String getNumber, getBackendOTP;
    ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_otp_verification);
        Var();
        numberMove();
        loadingBar = new ProgressDialog(this);


        getNumber = getIntent().getStringExtra("mobile");
        getBackendOTP = getIntent().getStringExtra("backendOTP");

        mobile.setText(getNumber);

        submit_btn.setOnClickListener(v -> {
            loadingBar.setTitle("Verifying OTP");
            loadingBar.setMessage("Please wait..");
            loadingBar.show();

            String first = first_num.getText().toString().trim();
            String second = second_num.getText().toString().trim();
            String third = third_num.getText().toString().trim();
            String fourth = fourth_num.getText().toString().trim();
            String fifth = fifth_num.getText().toString().trim();
            String last = last_num.getText().toString().trim();

            if(TextUtils.isEmpty(first) && TextUtils.isEmpty(second) && TextUtils.isEmpty(third) && TextUtils.isEmpty(fourth)
                    && TextUtils.isEmpty(fifth) && TextUtils.isEmpty(last)) {
                Toast.makeText(passenger_otp_verification.this, "please enter otp number!", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            } else {
                String enterOTPCode = first_num.getText().toString() +
                        second_num.getText().toString() +
                        third_num.getText().toString() +
                        fourth_num.getText().toString() +
                        fifth_num.getText().toString() +
                        last_num.getText().toString();
                if(getBackendOTP != null) {
                    loadingBar.dismiss();
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                            getBackendOTP, enterOTPCode
                    );
                    FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                            .addOnCompleteListener(task -> {
                                if(task.isSuccessful()) {
                                    Intent intent = new Intent(passenger_otp_verification.this, passenger_location.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }else {
                                    Toast.makeText(passenger_otp_verification.this, "Enter correct OTP", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    private void numberMove() {
        first_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()) {
                    second_num.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        second_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()) {
                    third_num.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        third_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()) {
                    fourth_num.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        fourth_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()) {
                    fifth_num.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        fifth_num.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().trim().isEmpty()) {
                    last_num.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void Var() {
        first_num = findViewById(R.id.first_num);
        second_num = findViewById(R.id.second_num);
        third_num = findViewById(R.id.third_num);
        fourth_num = findViewById(R.id.fourth_num);
        fifth_num = findViewById(R.id.fifth_num);
        last_num = findViewById(R.id.last_num);
        mobile = findViewById(R.id.mobile);

        submit_btn = findViewById(R.id.submit_btn);
    }
}