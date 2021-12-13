package com.example.storedam;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.storedam.databinding.ActivityMapaBinding;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapaActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapaBinding binding;

    private Double latitud;
    private Double longitud;

    private FusedLocationProviderClient fusedLocationClient;
    private Button btn_aceptar_ubicacion;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btn_aceptar_ubicacion = findViewById(R.id.btn_aceptar_ubicacion);

        btn_aceptar_ubicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (latitud != null && longitud != null) {

                    Intent intent = new Intent();
                    intent.putExtra("latitud", latitud);
                    intent.putExtra("longitud", longitud);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(MapaActivity.this, "Por favor seleccione una ubicación", Toast.LENGTH_SHORT).show();
                }

            }
        });

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 123);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);


      /*  // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/


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
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            LatLng miubicacion = new LatLng(location.getLatitude(), location.getLongitude());

                            // Move the camera instantly to Sydney with a zoom of 15.
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miubicacion, 15));

// Zoom in, animating the camera.
                            mMap.animateCamera(CameraUpdateFactory.zoomIn());

// Zoom out to zoom level 10, animating with a duration of 2 seconds.
                            mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);

// Construct a CameraPosition focusing on Mountain View and animate the camera to that position.
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(miubicacion)      // Sets the center of the map to Mountain View
                                    .zoom(17)                   // Sets the zoom
                                    //.bearing(90)                // Sets the orientation of the camera to east
                                    //.tilt(30)                   // Sets the tilt of the camera to 30 degrees
                                    .build();                   // Creates a CameraPosition from the builder
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                        }
                    }
                });


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
                mMap.clear();
                latitud = latLng.latitude;
                longitud = latLng.longitude;
                btn_aceptar_ubicacion.setEnabled(true);
                mMap.addMarker(new MarkerOptions().position(latLng).title("Mi ubicación"));

            }
        });

    }
}