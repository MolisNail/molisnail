package com.espparki.molisnail.tienda;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.espparki.molisnail.R;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private OnCartItemChangeListener listener;

    public CartAdapter(List<CartItem> cartItems, OnCartItemChangeListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tienda_item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public interface OnCartItemChangeListener {
        void onQuantityChange(CartItem item, int newQuantity);
        void onRemoveItem(CartItem item);
        void onUpdateTotalPrice();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private TextView productName, productPrice, productQuantity;
        private ImageView productImage;
        private Button btnIncrease, btnDecrease, btnRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            productImage = itemView.findViewById(R.id.product_image);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            btnRemove = itemView.findViewById(R.id.btn_remove);
        }

        public void bind(CartItem item) {
            productName.setText(item.getNombre());
            productPrice.setText(String.format("%.2f€", item.getPrecio()));
            productQuantity.setText(String.valueOf(item.getQuantity()));

            // Cargar la imagen del producto usando Glide
            Glide.with(itemView.getContext())
                    .load(item.getImagen())
                    .into(productImage);

            // Aumentar cantidad
            btnIncrease.setOnClickListener(v -> {
                int newQuantity = item.getQuantity() + 1;
                item.setQuantity(newQuantity);
                productQuantity.setText(String.valueOf(newQuantity));
                listener.onQuantityChange(item, newQuantity);
                listener.onUpdateTotalPrice();
            });

            // Disminuir cantidad
            btnDecrease.setOnClickListener(v -> {
                if (item.getQuantity() > 1) {
                    int newQuantity = item.getQuantity() - 1;
                    item.setQuantity(newQuantity);
                    productQuantity.setText(String.valueOf(newQuantity));
                    listener.onQuantityChange(item, newQuantity);
                    listener.onUpdateTotalPrice();
                }
            });

            // Eliminar producto del carrito
            btnRemove.setOnClickListener(v -> {
                listener.onRemoveItem(item);
                int position = getAdapterPosition();
                cartItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartItems.size());
                listener.onUpdateTotalPrice();
            });
        }
    }
}
