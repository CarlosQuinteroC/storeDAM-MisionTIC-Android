package com.example.storedam;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.storedam.util.Constant;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.storedam.databinding.ActivityMenuBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMenuBinding binding;
    private SharedPreferences misPreferencias;
    private Activity miactividad;
    final int OPEN_GALLERY = 1;

    Uri data1;
    String urlImage;

    ImageView navImage;
    String usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        miactividad = this;

        setSupportActionBar(binding.appBarMenu.toolbar);
        binding.appBarMenu.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                     //   .setAction("Action", null).show();
                Navigation.findNavController(MenuActivity.this, R.id.nav_host_fragment_content_menu).navigate(R.id.nav_Producto);

            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_Producto)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id=menuItem.getItemId();
                //it's possible to do more actions on several items, if there is a large amount of items I prefer switch(){case} instead of if()
                if (id == R.id.nav_salir){

                    SharedPreferences.Editor editor = misPreferencias.edit();

                    editor.putString("usuario", "");
                    editor.putString("contrasena", "");
                    editor.putString("nombre", "");
                    editor.putString("imagen", "");
                    editor.commit();

                    //editor.clear();

                    Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                    startActivity(intent);

                    finish();
                    return true;

                }
                //This is for maintaining the behavior of the Navigation view
                NavigationUI.onNavDestinationSelected(menuItem,navController);
                //This is for closing the drawer after acting on it
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        //recibo parametros enviados desde MainActivity
       Bundle bundle = getIntent().getExtras();
       if(bundle != null){
           Log.e("Bundle_usuario", (String) bundle.get("usuario"));
           Log.e("Bundle_contraseña", bundle.getString("contraseña") );
           //Log.e("Bundle_value", bundle.getString("value") );
       }

       misPreferencias = getSharedPreferences(Constant.PREFERENCE, MODE_PRIVATE);
        String usuario = misPreferencias.getString("usuario", "NO HAY USUARIO");
        String contrasena = misPreferencias.getString("contraseña", "NO HAY CONTRASEÑA");
        String nombre = misPreferencias.getString("nombre", "NO HAY NOMBRE");
        String imagen = misPreferencias.getString("imagen", "");

        View headerView = navigationView.getHeaderView(0);
        TextView navNombre = headerView.findViewById(R.id.tev_header_nombre);
        TextView navCorreo = headerView.findViewById(R.id.tev_header_correo);

        //ImageView navImage = headerView.findViewById(R.id.imv_header_imagen);
        navImage = headerView.findViewById(R.id.imv_header_imagen);
        navImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Seleccione una imagen"), OPEN_GALLERY);

            }
        });


        Glide.with(miactividad)
                .load(imagen)
                .placeholder(new ColorDrawable(Color.BLACK))
                .into(navImage);

        navNombre.setText(nombre);
        navCorreo.setText(usuario);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_menu);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OPEN_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {

                data1 = data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data1);
                    navImage.setImageBitmap(bitmap);

                   subirImagen();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                Toast.makeText(this, "ERROR CON LA IMAGEN", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void subirImagen() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        //if there is a file to upload
        if (data1 != null) {
            //displaying a progress dialog while upload is going on

            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            String strDate = sdf.format(c.getTime());
            String nombreImagen = strDate + ".jpg";

            misPreferencias = getSharedPreferences(Constant.PREFERENCE, MODE_PRIVATE);

            String usuario = misPreferencias.getString("usuario", "NO HAY USUARIO");


            StorageReference riversRef = storageReference.child(usuario + "/" + nombreImagen);
            riversRef.putFile(data1)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //if the upload is successfull
                            //hiding the progress dialog

                            //and displaying a success toast

                            riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    urlImage = uri.toString();
                                    Log.e("URL_IMAGE", urlImage);

                                    SharedPreferences.Editor editor = misPreferencias.edit();
                                    editor.putString("imagen", urlImage);
                                    editor.commit();

                                    Map<String, Object> data = new HashMap<>();
                                    data.put("imagen", urlImage);

                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    db.collection("Usuarios").document(usuario)
                                            .set(data, SetOptions.merge());


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


                            //and displaying error message
                            Toast.makeText(miactividad, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
        //if there is not any file
        else {
            //you can display an error toast
        }
    }
}