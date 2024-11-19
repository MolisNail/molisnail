package com.espparki.molisnail;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TiendaFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private List<String> favoriteProductIds = new ArrayList<>();
    private List<Product> filteredList = new ArrayList<>();
    private ImageView cartIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tienda, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_products);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        cartIcon = view.findViewById(R.id.cart_icon);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        adapter = new ProductAdapter(filteredList, new ProductAdapter.OnProductClickListener() {
            @Override
            public void onAddToCartClick(Product product, View buttonView) {
                addToCart(product);
                playCartAnimation(buttonView);
            }

            @Override
            public void onFavoriteClick(Product product, boolean isFavorite) {
                handleFavoriteClick(product, isFavorite);
            }
        });

        recyclerView.setAdapter(adapter);

        // Botón de filtro
        view.findViewById(R.id.filter_button).setOnClickListener(this::showFilterMenu);

        // Botón del carrito
        cartIcon.setOnClickListener(v -> openCartActivity());

        loadProductsFromFirebase();
        loadFavoritesFromFirebase();

        return view;
    }

    private void loadProductsFromFirebase() {
        db.collection("productos").get().addOnSuccessListener(queryDocumentSnapshots -> {
            productList.clear();
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                Product product = document.toObject(Product.class);
                product.setId(document.getId());
                productList.add(product);
            }
            applyFilter("Todos");
        });
    }

    private void loadFavoritesFromFirebase() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("usuarios").document(userId).collection("favoritos")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    favoriteProductIds.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        favoriteProductIds.add(document.getId());
                    }
                    adapter.setFavorites(favoriteProductIds);
                });
    }

    private void handleFavoriteClick(Product product, boolean isFavorite) {
        String userId = auth.getCurrentUser().getUid();
        if (isFavorite) {
            db.collection("usuarios").document(userId).collection("favoritos").document(product.getId()).delete()
                    .addOnSuccessListener(aVoid -> {
                        favoriteProductIds.remove(product.getId());
                        adapter.updateFavorite(product.getId(), false);
                    });
        } else {
            db.collection("usuarios").document(userId).collection("favoritos").document(product.getId()).set(product)
                    .addOnSuccessListener(aVoid -> {
                        favoriteProductIds.add(product.getId());
                        adapter.updateFavorite(product.getId(), true);
                    });
        }
    }

    private void addToCart(Product product) {
        String userId = auth.getCurrentUser().getUid();
        DocumentReference cartRef = db.collection("usuarios").document(userId).collection("carrito").document(product.getId());

        cartRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Si ya existe, aumenta la cantidad
                int currentQuantity = documentSnapshot.getLong("quantity").intValue();
                cartRef.update("quantity", currentQuantity + 1);
            } else {
                // Si no existe, agrega con cantidad inicial 1
                CartItem newItem = new CartItem(product.getId(), product.getNombre(), product.getPrecio(), 1);
                cartRef.set(newItem);
            }
        });
    }

    private void playCartAnimation(View buttonView) {
        // Crear una vista "fantasma" que se mueva desde el botón al ícono del carrito
        final ImageView animView = new ImageView(requireContext());
        animView.setImageResource(R.drawable.ic_cart); // Icono que se moverá
        ((ViewGroup) requireView()).addView(animView);

        int[] startLocation = new int[2];
        int[] endLocation = new int[2];

        buttonView.getLocationOnScreen(startLocation);
        cartIcon.getLocationOnScreen(endLocation);

        animView.setX(startLocation[0]);
        animView.setY(startLocation[1]);

        animView.animate()
                .x(endLocation[0])
                .y(endLocation[1])
                .setDuration(500)
                .withEndAction(() -> {
                    ((ViewGroup) requireView()).removeView(animView);
                })
                .start();
    }

    private void openCartActivity() {
        Intent intent = new Intent(getContext(), CartActivity.class);
        startActivity(intent);
    }

    private void showFilterMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(getContext(), anchor);
        popupMenu.getMenu().add("Todos");
        popupMenu.getMenu().add("Nombre (A-Z)");
        popupMenu.getMenu().add("Precio (Menor a Mayor)");
        popupMenu.getMenu().add("Favoritos");

        popupMenu.setOnMenuItemClickListener(this::onFilterSelected);
        popupMenu.show();
    }

    private boolean onFilterSelected(MenuItem menuItem) {
        String filter = menuItem.getTitle().toString();
        applyFilter(filter);
        return true;
    }

    private void applyFilter(String filter) {
        filteredList.clear();
        switch (filter) {
            case "Todos":
                filteredList.addAll(productList);
                break;
            case "Nombre (A-Z)":
                filteredList.addAll(productList);
                Collections.sort(filteredList, Comparator.comparing(Product::getNombre));
                break;
            case "Precio (Menor a Mayor)":
                filteredList.addAll(productList);
                Collections.sort(filteredList, Comparator.comparingDouble(Product::getPrecio));
                break;
            case "Favoritos":
                for (Product product : productList) {
                    if (favoriteProductIds.contains(product.getId())) {
                        filteredList.add(product);
                    }
                }
                break;
        }
        adapter.notifyDataSetChanged();
    }
}
