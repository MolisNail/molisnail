package com.espparki.molisnail.admin.citas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.espparki.molisnail.R;

import java.util.List;

public class AdminCitasAdapter extends RecyclerView.Adapter<AdminCitasAdapter.CitaViewHolder> {

    private List<CitaCompleta> citasList;

    public AdminCitasAdapter(List<CitaCompleta> citasList) {
        this.citasList = citasList;
    }

    @NonNull
    @Override
    public CitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_cita, parent, false);
        return new CitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitaViewHolder holder, int position) {
        CitaCompleta cita = citasList.get(position);
        String citaInfo = String.format("Fecha: %s\nHora: %s\nServicio: %s\nCorreo: %s",
                cita.getFecha(), cita.getHora(), cita.getServicio(), cita.getCorreo());
        holder.tvCitaInfo.setText(citaInfo);
    }

    @Override
    public int getItemCount() {
        return citasList.size();
    }

    class CitaViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCitaInfo;

        public CitaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCitaInfo = itemView.findViewById(R.id.tvCitaInfo);
        }
    }
}
