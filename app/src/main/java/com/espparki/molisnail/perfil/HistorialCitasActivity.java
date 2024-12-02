package com.espparki.molisnail.perfil;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.espparki.molisnail.R;
import com.espparki.molisnail.citas.Cita;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistorialCitasActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistorialCitasAdapter adapter;
    private List<Cita> citasList = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil_activity_historial_citas);

        recyclerView = findViewById(R.id.recyclerViewHistorial);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadHistorialCitas();
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
        });
    }

    private void loadHistorialCitas() {
        String userId = auth.getCurrentUser().getUid();

        db.collection("citas")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    citasList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String fecha = document.getString("fecha");
                        String hora = document.getString("hora");
                        String servicio = document.getString("servicio");
                        String id = document.getId();

                        citasList.add(new Cita(fecha, hora, servicio, id, userId));
                    }

                    adapter = new HistorialCitasAdapter(citasList);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                });
    }
}
