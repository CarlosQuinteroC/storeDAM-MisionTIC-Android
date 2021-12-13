package com.example.storedam.ui.gallery;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.storedam.DetalleProductoActivity;
import com.example.storedam.R;
import com.example.storedam.databinding.FragmentGalleryBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GalleryFragment extends Fragment {

    private TextView tev_galeria;
    private Spinner spn_categorias;

    private String productos = "[{\"nombre\":\"XBOX ONE\",\"categoria\":\"Consolas\",\"precio\":1550000,\"enStock\":true,\"imagen\":\"https://imgr.search.brave.com/Q5NlFEFSSnIy0UMzacTqli6h9Z-ZIdyDO61RxhJqBM8/fit/1200/681/ce/1/aHR0cHM6Ly92aWdu/ZXR0ZS53aWtpYS5u/b2Nvb2tpZS5uZXQv/Z2FtZXJzcGVkaWEv/aW1hZ2VzLzEvMTQv/WGJveF9PbmUucG5n/L3JldmlzaW9uL2xh/dGVzdD9jYj0yMDE3/MDgxMzIyMzIyMCZw/YXRoLXByZWZpeD1l/cw\"},{\"nombre\":\"Nintendo Switch\",\"categoria\":\"Consolas\",\"precio\":1649000,\"enStock\":true,\"imagen\":\"https://assets.nintendo.eu/image/upload/v1634255691/NAL/Nintendo%20Switch%20Family/Nintendo%20Switch%20Online/Online%20Service/nswitch_switch_home_menu_hardware_engb.png\"},{\"nombre\":\"PS4\",\"categoria\":\"Consolas\",\"precio\":1699000,\"enStock\":true,\"imagen\":\"https://imgr.search.brave.com/4zIijBKO1IgcJJgbzavPv7VZNnfFTny6k-Kd33qhpLk/fit/1200/1080/ce/1/aHR0cDovL3d3dy5w/bmdtYXJ0LmNvbS9m/aWxlcy83L1BTNC1Q/TkctSW1hZ2UucG5n\"},{\"nombre\":\"PS5\",\"categoria\":\"Consolas\",\"precio\":3769000,\"enStock\":true,\"imagen\":\"https://www.vodafone.cz/public/design/images/pages/playstation/ps5.png\"}]";

    private RecyclerView rev_productos;
    private RecyclerView.Adapter mAdapter;
    private ImageView imv_item_producto;

    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



//Inicio Spinner
        //tev_galeria = root.findViewById(R.id.tev_galeria);
        //tev_galeria.setText("hola mundo");

       // spn_categorias = root.findViewById(R.id.spn_categorias);

      /*  String[] categorias = new String[]{"Beisbol", "Atletismo", "Karate", "Salto triple"};

       ArrayAdapter<String> adaptador = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                categorias);
        spn_categorias.setAdapter(adaptador);*/


    /*    spn_categorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String categoria = spn_categorias.getSelectedItem().toString();
                tev_galeria.setText(categoria);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/// Fin Spinner

        rev_productos= root.findViewById(R.id.rev_productos);
        rev_productos.setLayoutManager(new LinearLayoutManager(getActivity()));
        //rev_productos.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        try {
            JSONArray jsonProductos = new JSONArray(productos);

           // mAdapter = new ProductosAdapter(jsonProductos, getActivity());
            //rev_productos.setAdapter(mAdapter);
            JSONObject producto0 = jsonProductos.getJSONObject(0);
            String nombre = producto0.getString("nombre");
            Toast.makeText(getActivity(), "Nombre: "+ nombre, Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Productos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            JSONArray productos = new JSONArray();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.e("TAG", document.getId() + " => " + document.getData());

                                String nombre = document.getData().get("nombre").toString();
                                String categoria = document.getData().get("categoria").toString();
                                int precio = Integer.parseInt(document.getData().get("precio").toString());
                                boolean enstock = Boolean.parseBoolean(document.getData().get("enStock").toString());
                                String imagen = document.getData().get("imagen").toString();
                                /*int cantidad = Integer.parseInt(document.getData().get("cantidad").toString());

                                Double latitud;
                                Double longitud;
                                try {
                                    latitud = Double.parseDouble(document.getData().get("latitud").toString());
                                    longitud = Double.parseDouble(document.getData().get("longitud").toString());
                                } catch (Exception e) {
                                    latitud = 0.0;
                                    longitud = 0.0;
                                }*/


                                JSONObject producto = new JSONObject();
                                try {
                                    producto.put("codigo", document.getId());
                                    producto.put("nombre", nombre);
                                    producto.put("categoria", categoria);
                                    producto.put("precio", precio);
                                    producto.put("enStock", enstock);
                                    producto.put("imagen", imagen);
                                    /*producto.put("latitud", latitud);
                                    producto.put("longitud", longitud);
                                    producto.put("cantidad", cantidad);*/

                                    productos.put(producto);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }

                            mAdapter = new ProductosAdapter(productos, getActivity());

                            rev_productos.setAdapter(mAdapter);




                        } else {
                            Log.e("TAG", "Error getting documents: ", task.getException());
                        }
                    }
                });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ViewHolder> {

    private JSONArray productos;
    private Activity miActividad;
    private ViewHolder holder;
    private int position;

    public ProductosAdapter(JSONArray productos, Activity miActividad) {
        this.productos = productos;
        this.miActividad = miActividad;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_productos, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        try {
            Log.e("POSICION", "POS: " + position);
            String nombre = this.productos.getJSONObject(position).getString("nombre");
            String categoria = this.productos.getJSONObject(position).getString("categoria");
            int precio = this.productos.getJSONObject(position).getInt("precio");
            String imagen = this.productos.getJSONObject(position).getString("imagen");

            holder.tev_item_name.setText(nombre);
            holder.tev_item_categoria.setText(categoria);
            holder.tev_item_precio.setText("$" + precio);

            //Glide.with(miActividad).load("imagen").into(holder.imv_item_producto);

            if (imagen.equals("picasso")) {

                holder.imv_item_producto.setImageResource(miActividad.getResources().getIdentifier(imagen, "drawable", miActividad.getPackageName()));

                //holder.imv_item_producto.setImageDrawable(miActividad.getDrawable(R.drawable.picasso));
            } else {
                Glide.with(miActividad)
                        .load(imagen)
                        .placeholder(new ColorDrawable(Color.BLACK))
                        .into(holder.imv_item_producto);
            }



           /* holder.btn_item_favorito.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(miActividad, "Agregado a favoritos", Toast.LENGTH_SHORT).show();
                }
            });



            holder.btn_item_carrito.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(miActividad, "Agregado al Carrito", Toast.LENGTH_SHORT).show();
                }
            });*/



            ////VISTA DE DETALLE
            holder.imv_item_producto.setOnClickListener(new View.OnClickListener() {
                int position = holder.getAdapterPosition();
                @Override
                public void onClick(View v) {
                    try {
                        //Log.e("PRODUCTOS: ", productos.getJSONObject(position).toString());
                       Intent intent = new Intent(miActividad, DetalleProductoActivity.class);
                       intent.putExtra("producto",productos.getJSONObject(position).toString());
                        miActividad.startActivity(intent);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            holder.btn_item_favorito.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Log.e("PRODUCTO_FAVORITO", productos.getJSONObject(position).toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

        } catch (JSONException e) {
            holder.tev_item_name.setText("error");
        }

    }

    @Override
    public int getItemCount() {
        Log.e("CANTIDAD_PRODUCTOS", "" + this.productos.length());
        return this.productos.length();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tev_item_name;
        private TextView tev_item_categoria;
        private TextView tev_item_precio;
        private ImageView imv_item_producto;
        private ImageButton btn_item_favorito;
        private ImageButton btn_item_carrito;
//        //private ImageButton btn_item_favorito;
        //private ImageButton btn_item_carrito;
        public ViewHolder(View v) {
            super(v);
            tev_item_name = v.findViewById(R.id.tev_item_name);
            tev_item_categoria = v.findViewById(R.id.tev_item_categoria);
            tev_item_precio = v.findViewById(R.id.tev_item_precio);
            imv_item_producto = v.findViewById(R.id.imv_item_producto);
            btn_item_favorito = v.findViewById(R.id.btn_item_favorito);
            btn_item_carrito = v.findViewById(R.id.btn_item_carrito);

        }
    }
}
