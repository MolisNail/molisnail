package com.espparki.molisnail.admin.productos;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.espparki.molisnail.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProductsAdminActivity extends AppCompatActivity implements ProductAdminAdapter.OnEditProductListener {

    private RecyclerView recyclerView;
    private ProductAdminAdapter adapter;
    private List<ProductCompleto> productList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_products_admin);
        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.recyclerViewProducts);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        productList = new ArrayList<>();
        adapter = new ProductAdminAdapter(productList, this);
        recyclerView.setAdapter(adapter);
        loadProducts();
        findViewById(R.id.btnAddProduct).setOnClickListener(v -> {
            Intent intent = new Intent(ProductsAdminActivity.this, AddProduct.class);
            startActivity(intent);
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish();
        });
    }

    private void loadProducts() {
        db.collection("productos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    productList.clear();
                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d("ProductsAdmin", "No hay productos en la colección.");
                        adapter.notifyDataSetChanged();
                        return;
                    }

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getId();
                        String nombre = document.getString("nombre");
                        Double precio = document.getDouble("precio");
                        String imagenUrl = document.getString("imagenUrl");

                        if (nombre != null && precio != null && imagenUrl != null) {
                            productList.add(new ProductCompleto(id, nombre, precio, imagenUrl));
                        } else {
                            Log.e("ProductsAdmin", "Producto inválido con ID: " + id);
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("ProductsAdmin", "Error al cargar productos", e));
    }

    @Override
    public void onEdit(ProductCompleto product) {
        Intent intent = new Intent(this, EditProduct.class);
        intent.putExtra("productId", product.getId());
        intent.putExtra("nombre", product.getNombre());
        intent.putExtra("precio", product.getPrecio());
        intent.putExtra("imagenUrl", product.getImagenUrl());
        startActivity(intent);
    }

    @Override
    public void onDelete(ProductCompleto product) {
        db.collection("productos").document(product.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    productList.remove(product);
                    adapter.notifyDataSetChanged();
                    Log.d("ProductsAdmin", "Producto eliminado correctamente.");
                })
                .addOnFailureListener(e -> Log.e("ProductsAdmin", "Error al eliminar producto", e));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }
}
