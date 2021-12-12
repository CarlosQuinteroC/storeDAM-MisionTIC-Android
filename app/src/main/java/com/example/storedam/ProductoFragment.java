package com.example.storedam;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.storedam.databinding.FragmentGalleryBinding;
import com.example.storedam.databinding.FragmentProductoBinding;
import com.example.storedam.ui.gallery.GalleryFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.xmlpull.v1.XmlPullParser;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductoFragment extends Fragment {
    private Button btn_cancelar_agregar_producto;
    private Button btn_agregar_producto;
    private FragmentProductoBinding binding;
    private TextInputLayout edt_nombre_producto;
    private TextInputLayout edt_categoria_producto;
    private TextInputLayout edt_precio_producto;
    private CheckBox chb_inStock_producto;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProductoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductoFragment newInstance(String param1, String param2) {
        ProductoFragment fragment = new ProductoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_producto, container, false);

        binding = FragmentProductoBinding.inflate(inflater, container, false);
        //View myView = inflater.inflate(R.layout.fragment_producto, container, false);
        View root = binding.getRoot();

        edt_nombre_producto = root.findViewById(R.id.edt_nombre_producto);
        edt_categoria_producto = root.findViewById(R.id.edt_categoria_producto);
        edt_precio_producto = root.findViewById(R.id.edt_precio_producto);
        chb_inStock_producto = root.findViewById(R.id.chb_inStock_producto);

        btn_cancelar_agregar_producto = root.findViewById(R.id.btn_cancelar_Agregar_producto);
        btn_agregar_producto = root.findViewById(R.id.btn_agregar_producto);

        btn_agregar_producto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Access a Cloud Firestore instance from your Activity
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                String nombre = edt_nombre_producto.getEditText().getText().toString();
                String categoria = edt_categoria_producto.getEditText().getText().toString();
                int precio = Integer.parseInt(edt_precio_producto.getEditText().getText().toString());
                boolean inStock = chb_inStock_producto.isChecked();

                // Create a new user with a first and last name
                Map<String, Object> producto = new HashMap<>();
                producto.put("nombre", nombre);
                producto.put("categoria", categoria);
                producto.put("precio", precio);
                producto.put("enStock", inStock);
                producto.put("imagen", "");


                // Add a new document with a generated ID
                db.collection("Productos")
                        .add(producto)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TAG", "Error adding document", e);
                            }
                        });
            }
        });


        btn_cancelar_agregar_producto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_menu).navigate(R.id.nav_Producto);
            }
        });

        return root;

    }
}