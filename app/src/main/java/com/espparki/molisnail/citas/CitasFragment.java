package com.espparki.molisnail.citas;

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

import com.espparki.molisnail.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CitasFragment extends Fragment {

    private TextView tvProximaCita;
    private CalendarView calendarView;
    private Button btnPedirCita, btnVerMisCitas;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private List<Cita> citasList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.citas_fragment_citas, container, false);

        tvProximaCita = view.findViewById(R.id.tvProximaCita);
        calendarView = view.findViewById(R.id.calendarView);
        btnPedirCita = view.findViewById(R.id.btnPedirCita);
        btnVerMisCitas = view.findViewById(R.id.btnVerMisCitas);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        btnVerMisCitas.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MisCitasFragment())
                    .addToBackStack(null)
                    .commit();
        });

        loadCitas();

        btnPedirCita.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PedirCitaActivity.class);
            startActivity(intent);
        });

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
                    Date currentDate = new Date();
                    Cita proximaCita = null;

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String fecha = document.getString("fecha");
                        String hora = document.getString("hora");
                        String servicio = document.getString("servicio");
                        String id = document.getId();

                        Cita cita = new Cita(fecha, hora, servicio, id, userId);
                        citasList.add(cita);

                        // Compara las fechas y selecciona la más próxima al día actual
                        Date citaDate = cita.getFechaAsDate();
                        if (citaDate != null && citaDate.after(currentDate)) {
                            if (proximaCita == null || citaDate.before(proximaCita.getFechaAsDate())) {
                                proximaCita = cita;
                            }
                        }
                    }

                    if (proximaCita != null) {
                        tvProximaCita.setText("Próxima cita: " + proximaCita.getFecha() + " a las " + proximaCita.getHora());
                    } else {
                        tvProximaCita.setText("No hay citas próximas.");
                    }
                })
                .addOnFailureListener(e -> tvProximaCita.setText("Error al cargar citas."));
    }
}
