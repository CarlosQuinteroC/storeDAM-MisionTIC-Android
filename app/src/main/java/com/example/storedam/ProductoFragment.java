package com.example.storedam;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.storedam.databinding.FragmentGalleryBinding;
import com.example.storedam.databinding.FragmentProductoBinding;
import com.example.storedam.ui.gallery.GalleryFragment;
import com.example.storedam.util.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.OnProgressListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    private Button btn_seleccionar_imagen;
    private ImageView imv_producto_imagen;
    private Button btn_cargar_imagen;
    final int OPEN_GALLERY = 1;
    Uri data1;
String urlImage;

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

        imv_producto_imagen = root.findViewById(R.id.imv_producto_imagen);

        btn_cancelar_agregar_producto = root.findViewById(R.id.btn_cancelar_Agregar_producto);
        btn_agregar_producto = root.findViewById(R.id.btn_agregar_producto);
        btn_seleccionar_imagen = root.findViewById(R.id.btn_seleccionar_imagen);
        btn_cargar_imagen = root.findViewById(R.id.btn_cargar_imagen);

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
                producto.put("imagen", urlImage);


                // Add a new document with a generated ID
                db.collection("Productos")
                        .add(producto)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                                Toast.makeText(getActivity(), "El producto ha sido agregado", Toast.LENGTH_SHORT).show();
                                edt_nombre_producto.getEditText().setText("");
                                edt_categoria_producto.getEditText().setText("");
                                edt_nombre_producto.getEditText().setText("");
                                chb_inStock_producto.setChecked(false);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("TAG", "Error adding document", e);
                                Toast.makeText(getActivity(), "Error creando producto", Toast.LENGTH_SHORT).show();
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

        btn_seleccionar_imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seleccione una imagen"), OPEN_GALLERY);
            }
        });

        btn_cargar_imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subirImagen();
            }
        });

        return root;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OPEN_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getActivity(), "Recepcion de imagen correcta", Toast.LENGTH_SHORT).show();

                 data1 = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data1);
                    imv_producto_imagen.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), "Error con la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void subirImagen() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        //if there is a file to upload
        if (data1 != null) {
            //displaying a progress dialog while upload is going on
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle("Subiendo");
            progressDialog.show();

            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String strDate = sdf.format(c.getTime());
            String nombreImagen = strDate + ".jpg";

            //mispreferencias = getActivity().getSharedPreferences(Constant.PREFERENCE, MODE_PRIVATE);

            String usuario = getActivity().getSharedPreferences(Constant.PREFERENCE, MODE_PRIVATE).getString("usuario", "NO HAY USUARIO");


            StorageReference riversRef = storageReference.child(usuario + "/" + nombreImagen);
            riversRef.putFile(data1)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying a success toast
                            Toast.makeText(getActivity(), "File Uploaded ", Toast.LENGTH_LONG).show();

                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    urlImage = uri.toString();
                                    Log.e("URL_IMAGE", urlImage);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successfull
                            //hiding the progress dialog
                            progressDialog.dismiss();

                            //and displaying error message
                            Toast.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                   /* .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            //displaying percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    })*/;
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }
}