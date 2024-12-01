package com.espparki.molisnail.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.espparki.molisnail.R;
import com.espparki.molisnail.login.LogInActivity;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_admin);

        // Botón para gestionar citas
        findViewById(R.id.manageAppointmentsCard).setOnClickListener(v -> {
            // Lógica para ir a la gestión de citas
            // startActivity(new Intent(this, ManageAppointmentsActivity.class));
        });

        // Botón para gestionar productos
        findViewById(R.id.manageProductsCard).setOnClickListener(v -> {
            // Lógica para ir a la gestión de productos
            // startActivity(new Intent(this, ManageProductsActivity.class));
        });

        // Botón para gestionar catálogo
        findViewById(R.id.manageCatalogCard).setOnClickListener(v -> {
            // Lógica para ir a la gestión del catálogo
            // startActivity(new Intent(this, ManageCatalogActivity.class));
        });

        // Botón para gestionar usuarios
        findViewById(R.id.manageUsersCard).setOnClickListener(v -> {
            // Lógica para ir a la gestión de usuarios
            // startActivity(new Intent(this, ManageUsersActivity.class));
        });

        // Botón para volver al inicio de sesión
        findViewById(R.id.backToLoginButton).setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, LogInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
