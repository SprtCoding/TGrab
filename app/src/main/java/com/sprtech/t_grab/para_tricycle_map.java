package com.sprtech.t_grab;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class para_tricycle_map extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    GoogleMap mMap;
    GoogleApiClient googleApiClient;
    Location mLastLocation;
    LocationRequest locationRequest;
    Button logout_btn, mRequest, bt_back;
    LatLng pickUpLocation;
    private Boolean requestBol = false;
    private Marker pickUpMarker;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_para_tricycle_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        logout_btn = findViewById(R.id.logout_btn);
        mRequest = findViewById(R.id.request);
        bt_back = findViewById(R.id.bt_back);
        bt_back.setOnClickListener(v -> {
            Intent intent = new Intent(para_tricycle_map.this, passenger_dashboard.class);
            startActivity(intent);
        });
        mRequest.setOnClickListener(v -> {
            if(requestBol) {
                requestBol = false;
                geoQuery.removeAllListeners();
                tricycleLocationRef.removeEventListener(tricycleLocationRefListener);

                if(driverFoundID != null) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(driverFoundID);
                    ref.child("passengerRideID").removeValue();
                    driverFoundID = null;
                }
                driverFound = false;
                radius = 1;
                String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PassengerRequest");
                GeoFire geoFire = new GeoFire(ref);
                geoFire.removeLocation(userID);

                if(pickUpMarker != null) {
                    pickUpMarker.remove();
                }
            }else {
                requestBol = true;
                String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("PassengerRequest");
                GeoFire geoFire = new GeoFire(ref);
                geoFire.setLocation(userID, new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()));

                pickUpLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                pickUpMarker = mMap.addMarker(new MarkerOptions().position(pickUpLocation).title("Pickup here!"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(pickUpLocation));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pickUpLocation, 15));

                mRequest.setText("Getting your Driver. Please wait....");

                getClosestDriver();
            }
        });
        logout_btn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(para_tricycle_map.this, login_activity.class);
            startActivity(intent);
            finish();
        });
    }

    private int radius = 1;
    private Boolean driverFound = false;
    private String driverFoundID;
    GeoQuery geoQuery;
    private void getClosestDriver() {
        DatabaseReference driverLocation = FirebaseDatabase.getInstance().getReference("DriversAvailable");
        GeoFire geoFire = new GeoFire(driverLocation);
        geoQuery = geoFire.queryAtLocation(new GeoLocation(pickUpLocation.latitude, pickUpLocation.longitude), radius);
        geoQuery.removeAllListeners();


        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(!driverFound && requestBol) {
                    driverFound = true;
                    driverFoundID = key;

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(driverFoundID);
                    String passengerID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

                    ref.addValueEventListener(new ValueEventListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()) {
                                if(snapshot.hasChild("who")) {
                                    String who = snapshot.child("who").getValue(String.class);
                                    assert who != null;
                                    if(who.equals("Driver")) {
                                        HashMap<String, Object> map = new HashMap<>();
                                        map.put("passengerRideID", passengerID);
                                        ref.updateChildren(map);

                                        getDriverLocation();
                                        mRequest.setText("Looking for Drivers location..");
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(para_tricycle_map.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                if(!driverFound) {
                    radius++;
                    getClosestDriver();
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Toast.makeText(para_tricycle_map.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Marker marker;
    private DatabaseReference tricycleLocationRef;
    private ValueEventListener tricycleLocationRefListener;
    private void getDriverLocation() {
        tricycleLocationRef = FirebaseDatabase.getInstance().getReference("DriversWorking").child(driverFoundID).child("l");
        tricycleLocationRefListener = tricycleLocationRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && requestBol) {
                    List<Object> map = (List<Object>) snapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    mRequest.setText("Driver Found!");
                    assert map != null;
                    if(map.get(0) != null) {
                        locationLat = Double.parseDouble(map.get(0).toString());
                    }
                    if(map.get(1) != null) {
                        locationLng = Double.parseDouble(map.get(1).toString());
                    }

                    LatLng latLng = new LatLng(locationLat, locationLng);
                    if(marker != null) {
                        marker.remove();
                    }
                    Location loc1 = new Location("");
                    loc1.setLatitude(pickUpLocation.latitude);
                    loc1.setLongitude(pickUpLocation.longitude);

                    Location loc2 = new Location("");
                    loc2.setLatitude(latLng.latitude);
                    loc2.setLongitude(latLng.longitude);

                    float distance = loc1.distanceTo(loc2);

                    if(distance < 100) {
                        mRequest.setText("Tricycle's Here!");
                    }else {
                        mRequest.setText("Tricycle Found: " + String.valueOf(distance));
                    }
                    mRequest.setText("Tricycle found: " + String.valueOf(distance));
                    marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Your Driver!"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(para_tricycle_map.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection Lost.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        mLastLocation = location;

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}