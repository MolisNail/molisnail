package com.espparki.molisnail.perfil;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.espparki.molisnail.R;

import java.util.List;

public class ProfileOptionsAdapter extends RecyclerView.Adapter<ProfileOptionsAdapter.OptionViewHolder> {

    private final List<String> optionsList;
    private final OnOptionClickListener listener;
    public ProfileOptionsAdapter(List<String> optionsList, OnOptionClickListener listener) {
        this.optionsList = optionsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.perfil_profile_option_item, parent, false);
        return new OptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionViewHolder holder, int position) {
        String optionTitle = optionsList.get(position);
        holder.optionTitleTextView.setText(optionTitle);
        holder.itemView.setOnClickListener(v -> listener.onOptionClick(position));
    }

    @Override
    public int getItemCount() {
        return optionsList.size();
    }

    public interface OnOptionClickListener {
        void onOptionClick(int position);
    }
    public static class OptionViewHolder extends RecyclerView.ViewHolder {
        TextView optionTitleTextView;

        public OptionViewHolder(@NonNull View itemView) {
            super(itemView);
            optionTitleTextView = itemView.findViewById(R.id.option_title);
        }
    }
}
