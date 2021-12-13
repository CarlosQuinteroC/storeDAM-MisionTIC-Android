package com.example.storedam;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.storedam.util.Utilidades;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistroActivity extends AppCompatActivity {
    private CheckBox chb_terminos;
    private Button btn_terminar;
    private TextView link_terminos;
    private EditText edt_contrasena;
    private EditText edt_correo;
    private EditText edt_nombre;
    private EditText edt_apellio;
    private final int ACTIVITY_TERMINOS = 1;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        chb_terminos = findViewById(R.id.chb_terminos);
        btn_terminar = findViewById(R.id.btn_terminar);
        link_terminos = findViewById(R.id.link_terminos);
        edt_contrasena = findViewById(R.id.edt_contrasena);
        edt_correo = findViewById(R.id.edt_correo);
        edt_nombre = findViewById(R.id.edt_nombre);
        edt_apellio = findViewById(R.id.edt_apellido);


        chb_terminos.setEnabled(false);
        btn_terminar.setEnabled(false);

        link_terminos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroActivity.this, TerminosActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();


        chb_terminos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                btn_terminar.setEnabled(isChecked);
            }
        });
        btn_terminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = edt_nombre.getText().toString();
                String apellido = edt_apellio.getText().toString();
                String correo = edt_correo.getText().toString();
                String contrasena = edt_contrasena.getText().toString();
                if(contrasena.length()<8 && !isValidPassword(contrasena)){
                    Toast.makeText(RegistroActivity.this, "CONTRASEÑA NO VALIDA", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(RegistroActivity.this, "CONTRASEÑA VALIDA", Toast.LENGTH_SHORT).show();

                   /* Intent resultIntent = new Intent();
                    resultIntent.putExtra("correo", edt_correo.getText().toString());
                    resultIntent.putExtra("contraseña", edt_contrasena.getText().toString());
                    setResult(Activity.RESULT_OK,resultIntent);
                    finish();*/
                    //registrarUsuarioFirebase(correo, contrasena );
                    registrarUsuarioFirestore(nombre,apellido,correo, Utilidades.md5(contrasena));
                }
            }
        });
    }

    public static boolean isValidPassword(final String password){
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

    public void registrarUsuarioFirestore(String nombre, String apellido, String correo, String contrasena){

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new user with a first and last name
        Map<String, Object> usuario = new HashMap<>();
        usuario.put("nombre", nombre);
        usuario.put("apellido", apellido);
        usuario.put("correo", correo);
        usuario.put("contrasena", contrasena);

// Add a new document with a generated ID
        /*db.collection("Usuarios")
                .add(usuario)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });*/

        db.collection("Usuarios")
                .document(correo)
                .set(usuario)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully written!");
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error writing document", e);
                    }
                });
    }

    public void registrarUsuarioFirebase(String correo, String contrasena) {
        mAuth.createUserWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            user.sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.e("TAG", "Email sent.");
                                            }
                                        }
                                    });

                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e("TAG", "createUserWithEmail:failure", task.getException());

                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ACTIVITY_TERMINOS){
            if (resultCode == Activity.RESULT_OK){
                String estado = data.getStringExtra("ESTADO");
                Toast.makeText(this, "Aceptó terminos", Toast.LENGTH_SHORT).show();
                chb_terminos.setChecked(true);
            }else{
                chb_terminos.setChecked(false);
                Toast.makeText(this, "No acepto terminos", Toast.LENGTH_SHORT).show();
            }
        }
     }
}