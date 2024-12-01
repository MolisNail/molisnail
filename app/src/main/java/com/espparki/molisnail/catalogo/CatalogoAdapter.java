package com.espparki.molisnail.catalogo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.espparki.molisnail.R;

import java.util.List;

public class CatalogoAdapter extends RecyclerView.Adapter<CatalogoAdapter.CatalogoViewHolder> {

    private List<Nail> catalogoList;
    private Context context;
    private OnInspirarmeClickListener listener;

    public interface OnInspirarmeClickListener {
        void onInspirarmeClick(Nail nail);
    }

    // Constructor
    public CatalogoAdapter(List<Nail> catalogoList, OnInspirarmeClickListener listener) {
        this.catalogoList = catalogoList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CatalogoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.catalogo_item_catalogo, parent, false);
        return new CatalogoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CatalogoViewHolder holder, int position) {
        Nail nail = catalogoList.get(position);

        // Configura la imagen usando Glide
        Glide.with(context)
                .load(nail.getImagenUrl()) // Cambiado a getImagenUrl()
                .into(holder.productImage);

    }

    @Override
    public int getItemCount() {
        return catalogoList.size();
    }

    public static class CatalogoViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        CardView cardView;

        public CatalogoViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            cardView = (CardView) itemView;
        }
    }
}
