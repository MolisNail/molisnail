package com.espparki.molisnail.admin.productos;

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

public class ProductAdminAdapter extends RecyclerView.Adapter<ProductAdminAdapter.ProductAdminViewHolder> {

    private List<ProductCompleto> productList;
    private OnEditProductListener listener;

    public interface OnEditProductListener {
        void onEdit(ProductCompleto product);
        void onDelete(ProductCompleto product);
    }

    public ProductAdminAdapter(List<ProductCompleto> productList, OnEditProductListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_product, parent, false);
        return new ProductAdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdminViewHolder holder, int position) {
        ProductCompleto product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductAdminViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNombre;
        private TextView tvPrecio;
        private ImageView ivImagen;
        private Button btnEdit;
        private Button btnDelete;

        public ProductAdminViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvProductName); // ID del TextView en admin_item_product.xml
            tvPrecio = itemView.findViewById(R.id.tvProductPrice); // ID del TextView en admin_item_product.xml
            ivImagen = itemView.findViewById(R.id.ivProductImage); // ID del ImageView en admin_item_product.xml
            btnEdit = itemView.findViewById(R.id.btnEditProduct); // ID del botón de edición
            btnDelete = itemView.findViewById(R.id.btnDeleteProduct); // ID del botón de eliminación
        }

        public void bind(ProductCompleto product) {
            tvNombre.setText(product.getNombre());
            tvPrecio.setText(String.format("%.2f €", product.getPrecio()));
            Glide.with(itemView.getContext()).load(product.getImagenUrl()).into(ivImagen);

            btnEdit.setOnClickListener(v -> listener.onEdit(product));
            btnDelete.setOnClickListener(v -> listener.onDelete(product));
        }
    }
}
