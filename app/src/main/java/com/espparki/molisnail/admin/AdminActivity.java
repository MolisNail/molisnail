package com.espparki.molisnail.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.espparki.molisnail.R;
import com.espparki.molisnail.admin.catalogo.CatalogoAdminActivity;
import com.espparki.molisnail.admin.citas.CitasAdminActivity;
import com.espparki.molisnail.admin.productos.ProductsAdminActivity;
import com.espparki.molisnail.admin.usuarios.UsersAdminActivity;
import com.espparki.molisnail.login.LogInActivity;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_admin);

        db = FirebaseFirestore.getInstance();

        findViewById(R.id.manageAppointmentsCard).setOnClickListener(v -> {
            startActivity(new Intent(this, CitasAdminActivity.class));
        });

        findViewById(R.id.manageProductsCard).setOnClickListener(v -> {
            startActivity(new Intent(this, ProductsAdminActivity.class));
        });

        findViewById(R.id.manageCatalogCard).setOnClickListener(v -> {
            startActivity(new Intent(this, CatalogoAdminActivity.class));
        });

        findViewById(R.id.manageUsersCard).setOnClickListener(v -> {
            startActivity(new Intent(this, UsersAdminActivity.class));
        });

        findViewById(R.id.backToLoginButton).setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, LogInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        findViewById(R.id.changeAdminPasswordButton).setOnClickListener(v -> showChangePasswordDialog());
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AdminActivity.this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showChangePasswordDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        final AlertDialog dialog = builder.create();
        dialog.setView(inflater.inflate(R.layout.admin_change_admin_password_modal, null));
        dialog.show();

        EditText currentPasswordEditText = dialog.findViewById(R.id.currentPasswordEditText);
        EditText newPasswordEditText = dialog.findViewById(R.id.newPasswordEditText);
        EditText confirmPasswordEditText = dialog.findViewById(R.id.confirmPasswordEditText);
        Button confirmButton = dialog.findViewById(R.id.btnConfirmChangePassword);

        confirmButton.setOnClickListener(v -> {
            String currentPassword = currentPasswordEditText.getText().toString().trim();
            String newPassword = newPasswordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty() || currentPassword.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Las contraseñas nuevas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            verifyAndUpdatePassword(currentPassword, newPassword, dialog);
        });
    }

    private void verifyAndUpdatePassword(String currentPassword, String newPassword, AlertDialog dialog) {
        db.collection("config").document("admin").get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String storedPassword = documentSnapshot.getString("admin_password");

                if (storedPassword != null && storedPassword.equals(currentPassword)) {
                    // Actualizar la contraseña en Firestore
                    db.collection("config").document("admin")
                            .update("admin_password", newPassword)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    Toast.makeText(this, "La contraseña actual no es correcta", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al obtener la configuración", Toast.LENGTH_SHORT).show();
        });
    }
}
