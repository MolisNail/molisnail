package com.espparki.molisnail.admin.catalogo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.espparki.molisnail.R;
import com.espparki.molisnail.admin.AdminActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CatalogoAdminActivity extends AppCompatActivity implements DesignAdminAdapter.OnDesignActionListener {

    private RecyclerView recyclerView;
    private DesignAdminAdapter adapter;
    private List<Design> designList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_catalogo_admin);
        recyclerView = findViewById(R.id.recyclerViewDesigns);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        designList = new ArrayList<>();
        adapter = new DesignAdminAdapter(designList, this);
        recyclerView.setAdapter(adapter);
        db = FirebaseFirestore.getInstance();

        loadDesigns();
        Button btnAddDesign = findViewById(R.id.btnAddDesign);
        btnAddDesign.setOnClickListener(v -> {
            Intent intent = new Intent(CatalogoAdminActivity.this, AddDesignActivity.class);
            startActivity(intent);
        });
        Button btnBackToAdmin = findViewById(R.id.btnBackToAdmin);
        btnBackToAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(CatalogoAdminActivity.this, AdminActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadDesigns() {
        db.collection("designs")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    designList.clear();
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d("CatalogoAdmin", "No hay diseños en la colección.");
                        adapter.notifyDataSetChanged();
                        return;
                    }
                    queryDocumentSnapshots.forEach(document -> {
                        String id = document.getId();
                        String nombre = document.getString("nombre");
                        String imagenBase64 = document.getString("imagenBase64");

                        if (nombre != null && imagenBase64 != null) {
                            designList.add(new Design(id, nombre, imagenBase64));
                        } else {
                            Log.e("CatalogoAdmin", "Diseño inválido con ID: " + id);
                        }
                    });

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("CatalogoAdmin", "Error al cargar diseños", e));
    }
    @Override
    public void onDelete(Design design) {
        db.collection("designs").document(design.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    designList.remove(design);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Diseño eliminado correctamente.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al eliminar diseño.", Toast.LENGTH_SHORT).show();
                    Log.e("CatalogoAdmin", "Error al eliminar diseño", e);
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDesigns();
    }

}
