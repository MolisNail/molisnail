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

import java.util.List;

public class PaymentResultAdapter extends RecyclerView.Adapter<PaymentResultAdapter.PaymentResultViewHolder> {

    private List<CartItem> cartItems;

    public PaymentResultAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public PaymentResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tienda_item_payment_result, parent, false);
        return new PaymentResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaymentResultViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class PaymentResultViewHolder extends RecyclerView.ViewHolder {
        private TextView productName, productQuantity;
        private ImageView productImage;

        public PaymentResultViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.product_name);
            productQuantity = itemView.findViewById(R.id.product_quantity);
            productImage = itemView.findViewById(R.id.product_image);
        }

        public void bind(CartItem item) {
            productName.setText(item.getNombre());
            productQuantity.setText(String.format("Cantidad: %d", item.getQuantity()));

            // Cargar la imagen usando Glide
            Glide.with(itemView.getContext())
                    .load(item.getImagen())
                    .into(productImage);
        }
    }
}
