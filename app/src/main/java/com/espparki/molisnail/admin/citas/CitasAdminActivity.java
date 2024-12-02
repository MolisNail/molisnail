package com.espparki.molisnail.admin.citas;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.espparki.molisnail.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CitasAdminActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CitasCompletaAdapter citasAdapter;
    private List<CitaCompleta> citasList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_citas_admin);
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerViewCitas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        citasList = new ArrayList<>();
        citasAdapter = new CitasCompletaAdapter(citasList, this::deleteCita);
        recyclerView.setAdapter(citasAdapter);
        Button backToHubButton = findViewById(R.id.backToHubButton);
        backToHubButton.setOnClickListener(v -> finish());
        loadCitas();
    }

    private void loadCitas() {
        db.collection("citas")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    citasList.clear();
                    Date today = new Date(); // Fecha actual

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getId();
                        String userId = document.getString("userId");
                        String servicio = document.getString("servicio");
                        String fecha = document.getString("fecha");
                        String hora = document.getString("hora");
                        Date citaDate = parseDate(fecha);

                        // Filtrar citas futuras
                        if (citaDate != null && !citaDate.before(today)) {
                            if (userId != null && !userId.isEmpty()) {
                                db.collection("usuarios").document(userId)
                                        .get()
                                        .addOnSuccessListener(userDoc -> {
                                            if (userDoc.exists()) {
                                                String email = userDoc.getString("email");
                                                citasList.add(new CitaCompleta(id, userId, servicio, fecha, hora, email));

                                                // Ordenar la lista por fecha
                                                Collections.sort(citasList, Comparator.comparing(c -> parseDate(c.getFecha())));
                                                citasAdapter.notifyDataSetChanged();
                                            }
                                        })
                                        .addOnFailureListener(e -> Log.e("CitasAdmin", "Error al obtener usuario", e));
                            } else {
                                Log.e("CitasAdmin", "Cita con userId nulo o vacío: " + id);
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("CitasAdmin", "Error al cargar citas", e));
    }

    private void deleteCita(CitaCompleta cita) {
        db.collection("citas").document(cita.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    citasList.remove(cita);
                    citasAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "Cita eliminada correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error al eliminar la cita", Toast.LENGTH_SHORT).show());
    }

    private Date parseDate(String dateStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            Log.e("CitasAdmin", "Error al parsear la fecha: " + dateStr, e);
            return null;
        }
    }
}
