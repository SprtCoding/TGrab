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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class passenger_location extends AppCompatActivity {
    EditText location_edit;
    ImageView location_add;
    CardView next_btn;
    DatabaseReference userRef;
    FirebaseDatabase db;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ProgressDialog loadingBar;
    double longitude, langitude;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_location);
        Var();

        loadingBar = new ProgressDialog(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        userRef = db.getReference("Users");

        //get current location
        location_add.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(passenger_location.this
                    , Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                ActivityCompat.requestPermissions(passenger_location.this
                        , new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            }
        });

        //save location
        next_btn.setOnClickListener(v -> {
            loadingBar.setTitle("Saving location");
            loadingBar.setMessage("Please wait...");
            loadingBar.show();
            String myLocation = location_edit.getText().toString();

            if(TextUtils.isEmpty(myLocation)) {
                Toast.makeText(passenger_location.this, "location is required!", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }else {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("location", myLocation);
                hashMap.put("location_Longitude", longitude);
                hashMap.put("location_lang", langitude);
                userRef.child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Intent intent = new Intent(passenger_location.this, passenger_dashboard.class);
                        startActivity(intent);
                        finish();
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
                    Geocoder geocoder = new Geocoder(passenger_location.this, Locale.getDefault());

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
        location_edit = findViewById(R.id.location_edit);
        location_add = findViewById(R.id.location_add);
        next_btn = findViewById(R.id.next_btn);
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
}