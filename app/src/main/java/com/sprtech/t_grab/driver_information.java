package com.sprtech.t_grab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class driver_information extends AppCompatActivity {
    private EditText name, location_edit;
    private TextView gender_text_spinner;
    private Spinner gender_spinner;
    private CardView next_btn;
    private DatabaseReference userRef;
    private ImageView location_add;
    FirebaseDatabase db;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    private ProgressDialog loadingBar;
    double longitude, langitude;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_information);
        Var();
        genderList();
        loadingBar = new ProgressDialog(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //get current location
        location_add.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(driver_information.this
                    , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                ActivityCompat.requestPermissions(driver_information.this
                        , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        userRef = db.getReference("Users");

        next_btn.setOnClickListener(v -> {
            loadingBar.setTitle("Loading");
            loadingBar.setMessage("Please wait...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            String myName = name.getText().toString();
            String myGender = gender_text_spinner.getText().toString();
            String myLocation = location_edit.getText().toString();
            String select = "Select";

            if(TextUtils.isEmpty(myName)) {
                Toast.makeText(this, "Name is required.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }else if(select.equals(myGender)) {
                Toast.makeText(this, "please select your gender.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }else if(TextUtils.isEmpty(myLocation)) {
                Toast.makeText(this, "Location is required.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }else {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("name", myName);
                hashMap.put("gender", myGender);
                hashMap.put("location", myLocation);
                hashMap.put("lat", langitude);
                hashMap.put("lon", longitude);
                userRef.child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Intent intent = new Intent(driver_information.this, driver_dashboard.class);
                        startActivity(intent);
                        finish();
                        loadingBar.dismiss();
                    }
                });
            }
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            if (location != null) {
                try {
                    Geocoder geocoder = new Geocoder(driver_information.this, Locale.getDefault());

                    List<Address> addresses = geocoder.getFromLocation(
                            location.getLatitude(), location.getLongitude(), 1
                    );

                    longitude = addresses.get(0).getLongitude();
                    langitude = addresses.get(0).getLatitude();
                    location_edit.setText(addresses.get(0).getAddressLine(0));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void Var() {
        gender_text_spinner = findViewById(R.id.who_text_spinner);
        gender_spinner = findViewById(R.id.who_spinner);
        next_btn = findViewById(R.id.next_btn);
        name = findViewById(R.id.name);
        location_edit = findViewById(R.id.location_edit);
        location_add = findViewById(R.id.location_add);
    }

    private void genderList() {
        ArrayList<String> genderTypeList = new ArrayList<>();

        genderTypeList.add("Select");
        genderTypeList.add("Male");
        genderTypeList.add("Female");

        gender_spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, genderTypeList));
        gender_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender_text_spinner.setText(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(driver_information.this, "Please select", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
}