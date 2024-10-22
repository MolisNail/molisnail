package com.espparki.molisnail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

// Adaptador para las opciones del perfil
public class ProfileOptionsAdapter extends RecyclerView.Adapter<ProfileOptionsAdapter.OptionViewHolder> {

    // Lista de opciones
    private final List<String> optionsList;
    private final OnOptionClickListener listener;

    // Constructor del adaptador
    public ProfileOptionsAdapter(List<String> optionsList, OnOptionClickListener listener) {
        this.optionsList = optionsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout para cada opción
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_option_item, parent, false);
        return new OptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionViewHolder holder, int position) {
        // Establecer el título de la opción
        String optionTitle = optionsList.get(position);
        holder.optionTitleTextView.setText(optionTitle);

        // Configurar el click listener
        holder.itemView.setOnClickListener(v -> listener.onOptionClick(position));
    }

    @Override
    public int getItemCount() {
        return optionsList.size(); // Número de elementos en la lista
    }

    // Interfaz para manejar los clics en las opciones
    public interface OnOptionClickListener {
        void onOptionClick(int position);
    }

    // ViewHolder para las opciones del perfil
    public static class OptionViewHolder extends RecyclerView.ViewHolder {
        TextView optionTitleTextView;

        public OptionViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializar el TextView del título de la opción
            optionTitleTextView = itemView.findViewById(R.id.option_title);
        }
    }
}
