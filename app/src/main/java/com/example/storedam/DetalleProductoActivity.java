package com.example.storedam;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

public class DetalleProductoActivity extends AppCompatActivity {


    private ImageView imv_producto_detalle;
    private TextView tev_nombre_detalle;
    private TextView tev_categoria_detalle;
    private TextView tev_precio_detalle;
    private TextView tev_disponible_detalle;
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
}