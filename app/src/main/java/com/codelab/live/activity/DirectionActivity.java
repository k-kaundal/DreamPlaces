package com.codelab.live.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codelab.live.LoadingDialog;
import com.codelab.live.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class DirectionActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private boolean isLocationPermissionOk, isTrafficEnable;
    private BottomSheetBehavior<RelativeLayout> bottomSheetBehavior;
    private LoadingDialog loadingDialog;
    private Location currentLocation;
    private Double endLat, endLng;
    private String placeId;
    private int currentMode;
    private FloatingActionButton enableTraffic;
    private Chip btnChipDriver, btnChipWalking, btnChipBike, btnChipTrain;
   // private DirectionStepAdapter adapter;
    private ChipGroup travelMode;
    private TextView txtStartLocation,txtEndLocation,txtSheetTime,txtSheetDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direction);
enableTraffic = findViewById(R.id.enableTraffic);
btnChipDriver= findViewById(R.id.btnChipDriving);
btnChipBike= findViewById(R.id.btnChipBike);
btnChipWalking = findViewById(R.id.btnChipWalking);
btnChipTrain= findViewById(R.id.btnChipTrain);
enableTraffic= findViewById(R.id.enableTraffic);
travelMode= findViewById(R.id.travelMode);
txtStartLocation= findViewById(R.id.txtStartLocation);
txtEndLocation=findViewById(R.id.txtEndLocation);
txtSheetTime= findViewById(R.id.txtSheetTime);
txtSheetDistance= findViewById(R.id.txtSheetDistance);
        endLat = getIntent().getDoubleExtra("lat", 0.0);
        endLng = getIntent().getDoubleExtra("lng", 0.0);
        placeId = getIntent().getStringExtra("placeId");

       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadingDialog = new LoadingDialog(this);



//        bottomSheetLayoutBinding = binding.bottomSheet;
//        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayoutBinding.getRoot());
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//
//        adapter = new DirectionStepAdapter();
//
//        bottomSheetLayoutBinding.stepRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        bottomSheetLayoutBinding.stepRecyclerView.setAdapter(adapter);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.directionMap);

        mapFragment.getMapAsync(this);


        enableTraffic.setOnClickListener(view -> {
            if (isTrafficEnable) {
                if (mGoogleMap != null) {
                    mGoogleMap.setTrafficEnabled(false);
                    isTrafficEnable = false;
                }
            } else {
                if (mGoogleMap != null) {
                    mGoogleMap.setTrafficEnabled(true);
                    isTrafficEnable = true;
                }
            }
        });

        travelMode.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                if (checkedId != -1) {
                    switch (checkedId) {
                        case R.id.btnChipDriving:
                         //   getDirection("driving");
                            break;
                        case R.id.btnChipWalking:
                         //   getDirection("walking");
                            break;
                        case R.id.btnChipBike:
                          //  getDirection("bicycling");
                            break;
                        case R.id.btnChipTrain:
                         //   getDirection("transit");
                            break;
                    }
                }
            }
        });

    }


    private void clearUI() {

        mGoogleMap.clear();
        txtStartLocation.setText("");
        txtEndLocation.setText("");
    //    getSupportActionBar().setTitle("");
        txtSheetDistance.setText("");
        txtSheetTime.setText("");

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;


            setupGoogleMap();

    }

    private void setupGoogleMap() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setCompassEnabled(false);

        getCurrentLocation();
    }

    private void getCurrentLocation() {

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;



                } else {
                    Toast.makeText(DirectionActivity.this, "Location Not Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else
            super.onBackPressed();
    }

    private List<com.google.maps.model.LatLng> decode(String points) {

        int len = points.length();

        final List<com.google.maps.model.LatLng> path = new ArrayList<>(len / 2);
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = points.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = points.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new com.google.maps.model.LatLng(lat * 1e-5, lng * 1e-5));
        }

        return path;

    }
}