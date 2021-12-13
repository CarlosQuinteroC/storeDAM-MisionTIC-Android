package com.example.storedam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.storedam.util.Constant;
import com.example.storedam.util.Utilidades;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btn_registro;
    private Button btn_iniciar;
    private TextInputLayout edt_usuario;
    private TextInputLayout edt_contrasena;
    private FirebaseAuth mAuth;

    private final int ACTIVITY_REGISTRO = 1;

    private SharedPreferences misPreferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        btn_registro = findViewById(R.id.btn_registro);
        btn_iniciar = findViewById(R.id.btn_iniciar);
        edt_usuario = findViewById(R.id.edt_usuario);

        edt_contrasena = findViewById(R.id.edt_contrasena);

        btn_registro.setOnClickListener(this);
        btn_iniciar.setOnClickListener(this);

        misPreferencias = getSharedPreferences(Constant.PREFERENCE, MODE_PRIVATE);
        String usuario = misPreferencias.getString("usuario", "");
        String contrasena = misPreferencias.getString("contraseña", "");
        String nombre = misPreferencias.getString("nombre", "");
        String imagen = misPreferencias.getString("imagen", "");

        if(!usuario.equals("") && !contrasena.equals("")){
            toLogin(usuario, contrasena, nombre,imagen);
        }

        mAuth = FirebaseAuth.getInstance();



    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_iniciar:
                String usuario = edt_usuario.getEditText().getText().toString();
                String contrasena = edt_contrasena.getEditText().getText().toString();


                Log.e("USUARIO", usuario);
                Log.e("CONTRASEÑA", contrasena);

                if (usuario.equals("admin@admin.com") && contrasena.equals("admin")) {
                    toLogin(usuario, contrasena, "admin", "");
                } else {
                    //Toast.makeText(this, "Error iniciando sesion", Toast.LENGTH_SHORT).show();
                    //inicioSesionFirebase(usuario, contrasena);
                    inicioSesionFireStore(usuario, Utilidades.md5(contrasena));
                }

                break;
            case R.id.btn_registro:
                Intent intent = new Intent(this, RegistroActivity.class);
                startActivityForResult(intent,ACTIVITY_REGISTRO);
                break;
        }
    }

    public void inicioSesionFireStore(String correo, String contrasena){

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("Usuarios").document(correo);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.e("TAG", "DocumentSnapshot data: " + document.getData());

                        String userContrasena = document.getData().get("contrasena").toString();
                        String userNombre = document.getData().get("nombre").toString();
                        //String userImagen = document.getData().get("imagen").toString();
                        if (contrasena.equals(userContrasena)){
                            toLogin(correo,contrasena, userNombre, "");
                        }else {
                            Toast.makeText(MainActivity.this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Log.e("TAG", "No such document");
                        Toast.makeText(MainActivity.this, "Correo o contraseña incorrecto", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("TAG", "get failed with ", task.getException());
                }
            }
        });
    }

    public void inicioSesionFirebase(String usuario, String contrasena) {
        mAuth.signInWithEmailAndPassword(usuario, contrasena)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("TAG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            toLogin(usuario, contrasena, "", "");

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("TAG", "signInWithEmail:failure", task.getException());

                        }
                    }
                });
    }

    public void toLogin(String usuario, String contrasena, String nombre, String imagen){
        Toast.makeText(this, "Se ha inciado Sesion", Toast.LENGTH_SHORT).show();

        SharedPreferences.Editor editor = misPreferencias.edit();
        editor.putString("usuario", usuario);
        editor.putString("contraseña", contrasena);
        editor.putString("nombre", nombre);
        editor.putString("imagen", imagen);
        editor.commit();

        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtra("usuario", usuario);
        intent.putExtra("contraseña", contrasena);
        intent.putExtra("number", 1);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (ACTIVITY_REGISTRO == requestCode){
                if (resultCode == Activity.RESULT_OK){
                    String usuario = data.getStringExtra("correo");
                    String contrasena = data.getStringExtra("contraseña");
                    toLogin(usuario, contrasena, "", "");
                }
            }
    }
}