package com.espparki.molisnail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MisCitasFragment extends Fragment {

    private RecyclerView recyclerView;
    private CitasAdapter adapter;
    private List<Cita> citasList = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mis_citas, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewMisCitas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        loadCitas();

        return view;
    }

    private void loadCitas() {
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
                        String userIdFromDocument = document.getString("userId");
                        citasList.add(new Cita(fecha, hora, servicio, id, userIdFromDocument));
                    }

                    adapter = new CitasAdapter(citasList, this::deleteCita);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al cargar citas", Toast.LENGTH_SHORT).show());
    }
    private void deleteCita(Cita cita) {
        db.collection("citas").document(cita.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    int puntosARestar = getPuntosPorServicio(cita.getServicio());

                    // Actualiza los puntos en Firestore
                    db.collection("usuarios").document(cita.getUserId())
                            .update("puntos", FieldValue.increment(-puntosARestar))
                            .addOnSuccessListener(aVoid2 -> {
                                citasList.remove(cita);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "Cita eliminada. Puntos descontados.", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al actualizar los puntos", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al eliminar cita", Toast.LENGTH_SHORT).show());
    }

    private int getPuntosPorServicio(String servicio) {
        switch (servicio) {
            case "Manicura":
                return 7;
            case "Pedicura":
                return 6;
            case "Uñas Acrílicas":
                return 8;
            case "Uñas de Gel":
                return 9;
            case "Decoración de Uñas":
                return 3;
            default:
                return 0;
        }
    }

    private void restarPuntosUsuario(String userId, int puntos) {
        db.collection("usuarios").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Long puntosActuales = documentSnapshot.getLong("puntos");
                    if (puntosActuales == null) puntosActuales = 0L;

                    db.collection("usuarios").document(userId)
                            .update("puntos", Math.max(0, puntosActuales - puntos)) // No permite negativos
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Puntos actualizados", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al actualizar puntos", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al obtener puntos actuales", Toast.LENGTH_SHORT).show());
    }

}
