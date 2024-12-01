package com.espparki.molisnail.perfil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.espparki.molisnail.R;
import com.espparki.molisnail.citas.Cita;

import java.util.List;

public class HistorialCitasAdapter extends RecyclerView.Adapter<HistorialCitasAdapter.HistorialCitasViewHolder> {

    private List<Cita> citasList;

    public HistorialCitasAdapter(List<Cita> citasList) {
        this.citasList = citasList;
    }

    @NonNull
    @Override
    public HistorialCitasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.perfil_item_cita_historial, parent, false);
        return new HistorialCitasViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialCitasViewHolder holder, int position) {
        Cita cita = citasList.get(position);
        holder.bind(cita);
    }

    @Override
    public int getItemCount() {
        return citasList.size();
    }

    static class HistorialCitasViewHolder extends RecyclerView.ViewHolder {

        private TextView fechaTextView, horaTextView, servicioTextView;

        public HistorialCitasViewHolder(@NonNull View itemView) {
            super(itemView);
            fechaTextView = itemView.findViewById(R.id.fechaTextView);
            horaTextView = itemView.findViewById(R.id.horaTextView);
            servicioTextView = itemView.findViewById(R.id.servicioTextView);
        }

        public void bind(Cita cita) {
            fechaTextView.setText("Fecha: " + cita.getFecha());
            horaTextView.setText("Hora: " + cita.getHora());
            servicioTextView.setText("Servicio: " + cita.getServicio());
        }
    }
}
