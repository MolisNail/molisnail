package com.espparki.molisnail.perfil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.espparki.molisnail.MainActivity;
import com.espparki.molisnail.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ConfigurationActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText nameEditText, shippingAddressEditText;
    private Button changeProfilePhotoButton, resetProfilePhotoButton, saveButton, viewHistoryButton;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_activity_configuration);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        nameEditText = findViewById(R.id.nameEditText);
        shippingAddressEditText = findViewById(R.id.shippingAddressEditText);
        changeProfilePhotoButton = findViewById(R.id.changeProfilePhotoButton);
        resetProfilePhotoButton = findViewById(R.id.resetProfilePhotoButton);
        saveButton = findViewById(R.id.saveButton);
        viewHistoryButton = findViewById(R.id.viewHistoryButton);
        loadUserData();
        changeProfilePhotoButton.setOnClickListener(v -> openImagePicker());
        resetProfilePhotoButton.setOnClickListener(v -> resetProfilePhoto());
        saveButton.setOnClickListener(v -> saveUserData());
        viewHistoryButton.setOnClickListener(v -> openHistorialCitas());

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
        });

    }

    private void loadUserData() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        nameEditText.setText(documentSnapshot.getString("name"));
                        shippingAddressEditText.setText(documentSnapshot.getString("direccion"));
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show());
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
            Uri selectedImageUri = data.getData();
            updateProfilePhoto(selectedImageUri.toString()); // Actualiza directamente la foto de perfil
        }
    }

    private void resetProfilePhoto() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String googlePhotoUrl = documentSnapshot.getString("Google_photo");
                        String photoToSet;

                        if (googlePhotoUrl != null && !googlePhotoUrl.isEmpty()) {
                            photoToSet = googlePhotoUrl;
                        } else {
                            photoToSet = "android.resource://" + getPackageName() + "/" + R.drawable.ic_default_profile_picture;
                        }
                        db.collection("usuarios").document(userId)
                                .update("photo", photoToSet)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Foto de perfil reseteada", Toast.LENGTH_SHORT).show();
                                    updateProfileFragments(photoToSet);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error al resetear foto de perfil", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al obtener datos de usuario", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveUserData() {
        String userId = auth.getCurrentUser().getUid();
        String name = nameEditText.getText().toString().trim();
        String address = shippingAddressEditText.getText().toString().trim();

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("direccion", address);

        db.collection("usuarios").document(userId)
                .update(updates)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Error al guardar datos", Toast.LENGTH_SHORT).show());
    }

    private void updateProfilePhoto(String photoUrl) {
        String userId = auth.getCurrentUser().getUid();
        db.collection("usuarios").document(userId)
                .update("photo", photoUrl)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Foto de perfil actualizada", Toast.LENGTH_SHORT).show();
                    updateProfileFragments(photoUrl); // Notifica el cambio a los fragmentos relacionados
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar foto de perfil", Toast.LENGTH_SHORT).show());
    }

    private void updateProfileFragments(String photoUrl) {
        if (getParent() instanceof MainActivity) {
            ((MainActivity) getParent()).updateProfilePhoto(photoUrl);
        }
    }

    private void openHistorialCitas() {
        Intent intent = new Intent(ConfigurationActivity.this, HistorialCitasActivity.class);
        startActivity(intent);
    }
}
