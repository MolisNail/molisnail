package com.espparki.molisnail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> productList;
    private OnProductClickListener listener;

    public ProductAdapter(List<Product> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public interface OnProductClickListener {
        void onAddToCartClick(Product product);
        void onFavoriteClick(Product product);
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productName, productPrice;
        private Button btnAddToCart, btnFavorite;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
            btnFavorite = itemView.findViewById(R.id.btn_favorite);
        }

        public void bind(Product product) {
            productName.setText(product.getNombre());
            productPrice.setText(product.getPrecio() + "€");

            Glide.with(itemView.getContext()).load(product.getImagenUrl()).into(productImage);

            btnAddToCart.setOnClickListener(v -> listener.onAddToCartClick(product));
            btnFavorite.setOnClickListener(v -> listener.onFavoriteClick(product));
        }
    }
}


