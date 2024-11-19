package com.espparki.molisnail;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class CitasFragment extends Fragment {

    private TextView tvProximaCita;
    private CalendarView calendarView;
    private Button btnPedirCita, btnVerMisCitas;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    // Lista de citas del usuario
    private List<Cita> citasList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_citas, container, false);

        tvProximaCita = view.findViewById(R.id.tvProximaCita);
        calendarView = view.findViewById(R.id.calendarView);
        btnPedirCita = view.findViewById(R.id.btnPedirCita);
        btnVerMisCitas = view.findViewById(R.id.btnVerMisCitas);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Configurar el botón para ver citas
        btnVerMisCitas.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MisCitasFragment())
                    .addToBackStack(null)
                    .commit();
        });

        // Cargar las citas del usuario desde Firestore
        loadCitas();

        // Configurar el botón para pedir citas
        btnPedirCita.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PedirCitaActivity.class);
            startActivity(intent);
        });

        // Mostrar citas en el calendario
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            String selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", dayOfMonth, month + 1, year);
            boolean found = false;

            for (Cita cita : citasList) {
                if (cita.getFecha().equals(selectedDate)) {
                    tvProximaCita.setText("Cita programada: " + cita.getFecha() + " a las " + cita.getHora());
                    found = true;
                    break;
                }
            }

            if (!found) {
                tvProximaCita.setText("No tienes citas programadas para este día.");
            }
        });

        return view;
    }

    private void loadCitas() {
        String userId = auth.getCurrentUser().getUid();
        CollectionReference citasRef = db.collection("citas");

        citasRef.whereEqualTo("userId", userId)
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

                    if (!citasList.isEmpty()) {
                        Collections.sort(citasList, Comparator.comparing(Cita::getFechaAsDate));
                        Cita proximaCita = citasList.get(0);
                        tvProximaCita.setText("Próxima cita: " + proximaCita.getFecha() + " a las " + proximaCita.getHora());
                    } else {
                        tvProximaCita.setText("No hay citas próximas.");
                    }
                })
                .addOnFailureListener(e -> tvProximaCita.setText("Error al cargar citas."));
    }
}
