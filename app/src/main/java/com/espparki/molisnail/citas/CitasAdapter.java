package com.espparki.molisnail.citas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.espparki.molisnail.R;

import java.util.List;

public class CitasAdapter extends RecyclerView.Adapter<CitasAdapter.CitaViewHolder> {

    private List<Cita> citasList;
    private OnDeleteCitaListener deleteListener;

    public interface OnDeleteCitaListener {
        void onDelete(Cita cita);
    }

    public CitasAdapter(List<Cita> citasList, OnDeleteCitaListener deleteListener) {
        this.citasList = citasList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public CitaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.citas_item_cita, parent, false);
        return new CitaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitaViewHolder holder, int position) {
        Cita cita = citasList.get(position);
        holder.bind(cita);
    }

    @Override
    public int getItemCount() {
        return citasList.size();
    }

    class CitaViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCitaInfo;
        private Button btnDeleteCita;

        public CitaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCitaInfo = itemView.findViewById(R.id.tvCitaInfo);
            btnDeleteCita = itemView.findViewById(R.id.btnDeleteCita);
        }

        public void bind(Cita cita) {
            tvCitaInfo.setText(String.format("Fecha: %s\nHora: %s\nServicio: %s", cita.getFecha(), cita.getHora(), cita.getServicio()));

            btnDeleteCita.setOnClickListener(v -> deleteListener.onDelete(cita));
        }
    }
}
