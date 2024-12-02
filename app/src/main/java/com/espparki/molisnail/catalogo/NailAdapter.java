package com.espparki.molisnail.catalogo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.espparki.molisnail.R;
import com.espparki.molisnail.admin.catalogo.Design;

import java.util.List;

public class NailAdapter extends RecyclerView.Adapter<NailAdapter.NailViewHolder> {

    private final List<Design> designList;

    public NailAdapter(List<Design> designList) {
        this.designList = designList;
    }

    @NonNull
    @Override
    public NailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.catalogo_item_nail, parent, false);
        return new NailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NailViewHolder holder, int position) {
        Log.d("NailAdapter", "Binding item at position: " + position);
        Design design = designList.get(position);
        holder.bind(design);
    }

    @Override
    public int getItemCount() {
        return designList.size();
    }

    static class NailViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivImagen;

        public NailViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagen = itemView.findViewById(R.id.ivNailImage);
        }

        public void bind(Design design) {
            String base64Image = design.getImagenBase64();

            if (base64Image != null && !base64Image.isEmpty()) {
                try {
                    byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

                    if (bitmap != null) {
                        ivImagen.setImageBitmap(bitmap);
                        Log.d("NailAdapter", "Imagen cargada correctamente para ID: " + design.getId());
                    } else {
                        Log.e("NailAdapter", "El bitmap es nulo para ID: " + design.getId());
                        ivImagen.setImageResource(R.drawable.garras_icon); // Placeholder
                    }
                } catch (IllegalArgumentException e) {
                    Log.e("NailAdapter", "Error al decodificar la imagen Base64: " + e.getMessage());
                    ivImagen.setImageResource(R.drawable.garras_icon); // Placeholder
                }
            } else {
                ivImagen.setImageResource(R.drawable.garras_icon); // Placeholder
            }
        }


    }
}
