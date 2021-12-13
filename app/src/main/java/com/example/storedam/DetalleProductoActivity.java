package com.example.storedam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class DetalleProductoActivity extends AppCompatActivity implements OnMapReadyCallback {


    private ImageView imv_producto_detalle;
    private TextView tev_nombre_detalle;
    private TextView tev_categoria_detalle;
    private TextView tev_precio_detalle;
    private TextView tev_disponible_detalle;
    private GoogleMap mMap;

    Double latitud;
    Double longitud;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_producto);

        /*Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            Log.e("PRODUCTO RECIBIDO", (String) bundle.get("producto"));}*/


        imv_producto_detalle = findViewById(R.id.imv_producto_detalle);
        tev_nombre_detalle = findViewById(R.id.tev_nombre_detalle);
        tev_categoria_detalle = findViewById(R.id.tev_categoria_detalle);
        tev_precio_detalle = findViewById(R.id.tev_precio_detalle);
        tev_disponible_detalle = findViewById(R.id.tev_disponible_detalle);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getView().setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            Log.e("PRODUCTO RECIBIDO", (String) bundle.get("producto"));
            try {
                JSONObject producto = new JSONObject(bundle.getString("producto"));
                Log.e("NOMBRE ELEGIDO", producto.getString("nombre"));


                String nombre = producto.getString("nombre");
                String categoria = producto.getString("categoria");
                String precio = producto.getString("precio");
                boolean enStock = producto.getBoolean("enStock");
                String imagen = producto.getString("imagen");

                latitud = producto.getDouble("latitud");
                longitud = producto.getDouble("longitud");

                if (latitud != 0.0 && longitud != 0.0) {
                    mapFragment.getView().setVisibility(View.VISIBLE);
                    mapFragment.getMapAsync(this);
                }

                tev_nombre_detalle.setText(nombre);
                tev_categoria_detalle.setText(categoria);
                tev_precio_detalle.setText(precio);

                if(enStock){
                    tev_disponible_detalle.setText("Producto disponible");
                }else{
                    tev_disponible_detalle.setText("Producto Agotado");
                }

                Glide.with(this)
                        .load(imagen)
                        .placeholder(new ColorDrawable(Color.BLACK))
                        .into(imv_producto_detalle);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        LatLng miubicacion = new LatLng(latitud, longitud);

        mMap.addMarker(new MarkerOptions().position(miubicacion).title("Mi ubicación"));

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