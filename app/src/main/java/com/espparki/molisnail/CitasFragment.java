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

import java.util.HashMap;
import java.util.Map;

public class CitasFragment extends Fragment {

    private TextView tvProximaCita;
    private CalendarView calendarView;
    private Button btnPedirCita;

    // Simulación de citas para el mes actual (formato: día -> fecha)
    private Map<Integer, String> citasMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_citas, container, false);

        tvProximaCita = view.findViewById(R.id.tvProximaCita);
        calendarView = view.findViewById(R.id.calendarView);
        btnPedirCita = view.findViewById(R.id.btnPedirCita);

        // Lógica para mostrar la próxima cita o un mensaje si no hay citas
        loadProximaCita();

        // Configurar el botón para pedir citas
        btnPedirCita.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PedirCitaActivity.class);
            startActivity(intent);
        });

        // Lógica para colorear días con citas en el calendario
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            if (citasMap.containsKey(dayOfMonth)) {
                tvProximaCita.setText("Cita programada para el " + citasMap.get(dayOfMonth));
            } else {
                tvProximaCita.setText("No tienes citas para este día.");
            }
        });

        return view;
    }

    private void loadProximaCita() {
        // Simulación de citas
        citasMap.put(10, "10 de Noviembre a las 10:00");
        citasMap.put(15, "15 de Noviembre a las 14:00");

        // Determinar y mostrar la próxima cita o un mensaje si no hay citas
        if (!citasMap.isEmpty()) {
            tvProximaCita.setText("Próxima cita: " + citasMap.get(10));
        } else {
            tvProximaCita.setText("No tienes citas actualmente");
        }
    }
}
