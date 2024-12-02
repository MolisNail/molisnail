package com.espparki.molisnail.admin.citas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.espparki.molisnail.R;

import java.util.List;

public class CitasCompletaAdapter extends RecyclerView.Adapter<CitasCompletaAdapter.CitaCompletaViewHolder> {

    private final List<CitaCompleta> citasList;
    private final OnDeleteCitaListener deleteListener;
    public interface OnDeleteCitaListener {
        void onDelete(CitaCompleta cita);}
    public CitasCompletaAdapter(List<CitaCompleta> citasList, OnDeleteCitaListener deleteListener) {
        this.citasList = citasList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public CitaCompletaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_cita, parent, false);
        return new CitaCompletaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitaCompletaViewHolder holder, int position) {
        CitaCompleta cita = citasList.get(position);
        String citaInfo = String.format("Fecha: %s\nHora: %s\nServicio: %s\nUsuario: %s",
                cita.getFecha(), cita.getHora(), cita.getServicio(), cita.getCorreo());
        holder.tvCitaInfo.setText(citaInfo);
        holder.btnDeleteCita.setOnClickListener(v -> deleteListener.onDelete(cita));
    }

    @Override
    public int getItemCount() {
        return citasList.size();
    }
    static class CitaCompletaViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCitaInfo;
        private final Button btnDeleteCita;

        public CitaCompletaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCitaInfo = itemView.findViewById(R.id.tvAdminCitaInfo);
            btnDeleteCita = itemView.findViewById(R.id.btnAdminDeleteCita);
        }
    }
}
