package com.espparki.molisnail;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private List<CartItem> cartItems = new ArrayList<>();
    private TextView totalPriceTextView;
    private double totalPrice = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recycler_view_cart);
        totalPriceTextView = findViewById(R.id.total_price);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        adapter = new CartAdapter(cartItems, new CartAdapter.OnCartItemChangeListener() {
            @Override
            public void onQuantityChange(CartItem item, int newQuantity) {
                updateCartItem(item, newQuantity);
            }

            @Override
            public void onRemoveItem(CartItem item) {
                removeCartItem(item);
            }

            @Override
            public void onUpdateTotalPrice() {
                updateTotalPrice(); // Llama al método existente para actualizar el precio total
            }
        });


        recyclerView.setAdapter(adapter);

        loadCartItems();
    }

    private void loadCartItems() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("usuarios").document(userId).collection("carrito")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    cartItems.clear();
                    totalPrice = 0.0;
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        CartItem item = document.toObject(CartItem.class);
                        item.setId(document.getId());
                        cartItems.add(item);
                        totalPrice += item.getPrecio() * item.getQuantity();
                    }
                    adapter.notifyDataSetChanged();
                    updateTotalPrice();
                });
    }

    private void updateCartItem(CartItem item, int newQuantity) {
        String userId = auth.getCurrentUser().getUid();
        db.collection("usuarios").document(userId).collection("carrito")
                .document(item.getId())
                .update("quantity", newQuantity)
                .addOnSuccessListener(aVoid -> {
                    totalPrice = 0.0;
                    for (CartItem cartItem : cartItems) {
                        totalPrice += cartItem.getPrecio() * cartItem.getQuantity();
                    }
                    updateTotalPrice();
                });
    }

    private void removeCartItem(CartItem item) {
        String userId = auth.getCurrentUser().getUid();
        db.collection("usuarios").document(userId).collection("carrito")
                .document(item.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    cartItems.remove(item);
                    adapter.notifyDataSetChanged();
                    totalPrice = 0.0;
                    for (CartItem cartItem : cartItems) {
                        totalPrice += cartItem.getPrecio() * cartItem.getQuantity();
                    }
                    updateTotalPrice();
                });
    }

    private void updateTotalPrice() {
        totalPriceTextView.setText(String.format("Total: %.2f€", totalPrice));
    }
}
