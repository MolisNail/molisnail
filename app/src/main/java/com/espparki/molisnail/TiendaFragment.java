package com.espparki.molisnail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class TiendaFragment extends Fragment {
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tienda, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_products);
        // Cambiar a GridLayoutManager con 2 columnas
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        db = FirebaseFirestore.getInstance();
        loadProductsFromFirebase();

        adapter = new ProductAdapter(productList, new ProductAdapter.OnProductClickListener() {
            @Override
            public void onAddToCartClick(Product product) {
                // Añadir lógica para agregar al carrito
            }

            @Override
            public void onFavoriteClick(Product product) {
                // Añadir lógica para marcar como favorito
            }
        });
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void loadProductsFromFirebase() {
        db.collection("productos").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                Product product = document.toObject(Product.class);
                productList.add(product);
            }
            adapter.notifyDataSetChanged();
        });
    }
}
