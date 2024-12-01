package com.espparki.molisnail.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.espparki.molisnail.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;

    // Firebase Auth and Firestore instances
    private FirebaseAuth Auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity_register);

        // Inicializar los elementos de la interfaz
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // Inicializar FirebaseAuth y Firestore
        Auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Configurar el botón de registro
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    // Método para registrar al usuario
    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validaciones básicas
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Introduce un correo electrónico");
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Introduce un correo electrónico válido");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Introduce una contraseña");
            return;
        }
        if (password.length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            return;
        }
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Las contraseñas no coinciden");
            return;
        }

        // Registro del usuario en Firebase Authentication
        Auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Obtener el usuario recién registrado
                FirebaseUser firebaseUser = Auth.getCurrentUser();

                if (firebaseUser != null) {
                    // Crear el usuario en Firestore
                    createUserInFirestore(firebaseUser);

                    // Registro exitoso, redirigir al login o al main
                    Toast.makeText(RegisterActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterActivity.this, LogInActivity.class));
                    finish();
                }
            } else {
                // Error en el registro
                Toast.makeText(RegisterActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Método para crear el usuario en Firestore
// Método para crear el usuario en Firestore
    private void createUserInFirestore(FirebaseUser firebaseUser) {
        Map<String, Object> user = new HashMap<>();
        user.put("correo", firebaseUser.getEmail());
        user.put("foto_perfil", "");  // Puedes agregar una URL de foto de perfil si la tienes
        user.put("puntos", 0);  // Puntos iniciales en 0
        user.put("nivel", "bronze"); // Nivel inicial

        // Guardar los datos en Firestore
        db.collection("usuarios").document(firebaseUser.getUid())
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Usuario creado correctamente en Firestore");
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error al crear el usuario en Firestore", e);
                });
    }

}
