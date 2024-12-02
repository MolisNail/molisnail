package com.espparki.molisnail.admin.catalogo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.espparki.molisnail.R;

import java.util.List;

public class DesignAdminAdapter extends RecyclerView.Adapter<DesignAdminAdapter.DesignViewHolder> {

    public interface OnDesignActionListener {
        void onDelete(Design design);
    }

    private final List<Design> designList;
    private final OnDesignActionListener listener;

    public DesignAdminAdapter(List<Design> designList, OnDesignActionListener listener) {
        this.designList = designList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DesignViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_design, parent, false);
        return new DesignViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DesignViewHolder holder, int position) {
        Design design = designList.get(position);
        holder.bind(design);
    }

    @Override
    public int getItemCount() {
        return designList.size();
    }

    class DesignViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNombre;
        private final ImageView ivImagen;
        private final Button btnDelete;

        public DesignViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvDesignName);
            ivImagen = itemView.findViewById(R.id.ivDesignImage);
            btnDelete = itemView.findViewById(R.id.btnDeleteDesign);
        }

        public void bind(Design design) {
            tvNombre.setText(design.getNombre());

            // Decodificar imagen Base64
            String base64Image = design.getImagenBase64();
            if (base64Image != null) {
                byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                ivImagen.setImageBitmap(bitmap);
            } else {
                ivImagen.setImageResource(R.drawable.garras_icon);
            }

            btnDelete.setOnClickListener(v -> listener.onDelete(design));
        }
    }
}
