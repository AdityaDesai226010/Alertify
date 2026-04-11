package com.alertify.ui.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.alertify.R;
import com.alertify.location.LocationHelper;
import com.alertify.location.SafeZoneManager;
import com.alertify.utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class SafeZoneActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng selectedLatLng;
    private Marker selectedMarker;
    private Circle selectedCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_zone);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Button btnSetCurrent = findViewById(R.id.btnSetCurrentLocation);
        Button btnSave = findViewById(R.id.btnSaveHome);

        btnSetCurrent.setOnClickListener(v -> setCurrentLocationAsHome());
        btnSave.setOnClickListener(v -> saveHomeLocation());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Load existing home location if any
        double[] home = SafeZoneManager.getHomeLocation(this);
        if (home[0] != 0 || home[1] != 0) {
            selectedLatLng = new LatLng(home[0], home[1]);
            updateMapMarker(selectedLatLng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15f));
        }

        mMap.setOnMapClickListener(latLng -> {
            selectedLatLng = latLng;
            updateMapMarker(selectedLatLng);
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void updateMapMarker(LatLng latLng) {
        if (selectedMarker != null) selectedMarker.remove();
        if (selectedCircle != null) selectedCircle.remove();

        selectedMarker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Home Location"));

        selectedCircle = mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(Constants.DEFAULT_SAFE_RADIUS)
                .strokeColor(Color.BLUE)
                .fillColor(0x220000FF)
                .strokeWidth(5));
    }

    private void setCurrentLocationAsHome() {
        LocationHelper.fetchLocation(this, new LocationHelper.LocationCallback() {
            @Override
            public void onLocationResult(double lat, double lng, String locationLink) {
                selectedLatLng = new LatLng(lat, lng);
                updateMapMarker(selectedLatLng);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15f));
                Toast.makeText(SafeZoneActivity.this, "Current location selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLocationError(String error) {
                Toast.makeText(SafeZoneActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveHomeLocation() {
        if (selectedLatLng != null) {
            SafeZoneManager.setHomeLocation(this, selectedLatLng.latitude, selectedLatLng.longitude);
            Toast.makeText(this, "Home Location Saved!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Please select a location first", Toast.LENGTH_SHORT).show();
        }
    }
}
