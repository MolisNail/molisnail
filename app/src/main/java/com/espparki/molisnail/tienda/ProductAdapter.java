package com.espparki.molisnail.tienda;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.espparki.molisnail.R;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private List<String> favoriteProductIds = new ArrayList<>();
    private OnProductClickListener listener;

    public ProductAdapter(List<Product> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    public void setFavorites(List<String> favorites) {
        this.favoriteProductIds.clear();
        if (favorites != null) {
            this.favoriteProductIds.addAll(favorites);
        }
        notifyDataSetChanged();
    }

    public void updateFavorite(String productId, boolean isFavorite) {
        if (isFavorite) {
            favoriteProductIds.add(productId);
        } else {
            favoriteProductIds.remove(productId);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tienda_item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        boolean isFavorite = favoriteProductIds.contains(product.getId());
        holder.bind(product, isFavorite);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public interface OnProductClickListener {
        void onAddToCartClick(Product product, View buttonView);
        void onFavoriteClick(Product product, boolean isFavorite);
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage, btnFavorite;
        private TextView productName, productPrice, btnAddToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
        }

        public void bind(Product product, boolean isFavorite) {
            productName.setText(product.getNombre());
            productPrice.setText(product.getPrecio() + "€");

            Glide.with(itemView.getContext()).load(product.getImagenUrl()).into(productImage);

            // Actualizar icono del botón de favorito
            btnFavorite.setImageResource(isFavorite ? R.drawable.ic_heart_selected : R.drawable.ic_heart_unselected);

            btnFavorite.setOnClickListener(v -> {
                listener.onFavoriteClick(product, isFavorite);
                boolean newFavoriteState = !isFavorite;
                btnFavorite.setImageResource(newFavoriteState ? R.drawable.ic_heart_selected : R.drawable.ic_heart_unselected);
            });

            btnAddToCart.setOnClickListener(v -> listener.onAddToCartClick(product, btnAddToCart));
        }
    }

}
