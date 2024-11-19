package com.espparki.molisnail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText nameEditText, shippingAddressEditText;
    private TextView accountCreatedDateTextView;
    private Button changeProfilePhotoButton, resetProfilePhotoButton, saveButton;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inicializar vistas
        nameEditText = findViewById(R.id.nameEditText);
        shippingAddressEditText = findViewById(R.id.shippingAddressEditText);
        accountCreatedDateTextView = findViewById(R.id.accountCreatedDateTextView);
        changeProfilePhotoButton = findViewById(R.id.changeProfilePhotoButton);
        resetProfilePhotoButton = findViewById(R.id.resetProfilePhotoButton);
        saveButton = findViewById(R.id.saveButton);

        // Cargar datos de usuario
        loadUserData();

        // Cambiar foto de perfil
        changeProfilePhotoButton.setOnClickListener(v -> openImagePicker());

        // Resetear foto de perfil
        resetProfilePhotoButton.setOnClickListener(v -> resetProfilePhoto());

        // Guardar cambios
        saveButton.setOnClickListener(v -> saveUserData());
    }

    private void loadUserData() {
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            db.collection("usuarios").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String name = documentSnapshot.getString("name");
                            String address = documentSnapshot.getString("direccion");
                            String createdDate = documentSnapshot.getString("fecha_creacion");
                            String photoUrl = documentSnapshot.getString("photo");

                            nameEditText.setText(name != null ? name : "");
                            shippingAddressEditText.setText(address != null ? address : "");
                            accountCreatedDateTextView.setText("Fecha de creación: " + (createdDate != null ? createdDate : "Desconocida"));
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show());
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            Toast.makeText(this, "Imagen seleccionada", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetProfilePhoto() {
        FirebaseUser user = auth.getCurrentUser();  
        if (user != null) {
            db.collection("usuarios").document(user.getUid())
                    .update("photo", "@drawable/ic_profile_picture")
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Foto de perfil reseteada", Toast.LENGTH_SHORT).show();
                        updateProfileInMainAndProfileFragment("");
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al resetear foto", Toast.LENGTH_SHORT).show());
        }
    }

    private void saveUserData() {
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            String name = nameEditText.getText().toString().trim();
            String address = shippingAddressEditText.getText().toString().trim();
            Map<String, Object> updates = new HashMap<>();
            updates.put("name", name);
            updates.put("direccion", address);

            if (selectedImageUri != null) {
                updates.put("photo", selectedImageUri.toString());
            }

            db.collection("usuarios").document(user.getUid())
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show();
                        updateProfileInMainAndProfileFragment(selectedImageUri != null ? selectedImageUri.toString() : null);
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar datos", Toast.LENGTH_SHORT).show());
        }
    }

    private void updateProfileInMainAndProfileFragment(String photoUrl) {
        if (getApplicationContext() instanceof MainActivity) {
            ((MainActivity) getApplicationContext()).updateProfilePhoto(photoUrl);
        }
    }
}
