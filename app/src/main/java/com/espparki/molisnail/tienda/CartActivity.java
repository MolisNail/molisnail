package com.espparki.molisnail.tienda;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.espparki.molisnail.PayPalAPI.PayPalPaymentManager;
import com.espparki.molisnail.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CartActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private TextView totalPriceTextView;
    private Button proceedToPaymentButton;
    private static double totalPrice = 0.0;

    public static double getTotalPrice(){
        return totalPrice;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tienda_activity_cart);

        recyclerView = findViewById(R.id.recycler_view_cart);
        totalPriceTextView = findViewById(R.id.total_price);
        proceedToPaymentButton = findViewById(R.id.btn_proceed_to_payment);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        adapter = new CartAdapter(CartDataHolder.getInstance().getCartItems(), new CartAdapter.OnCartItemChangeListener() {
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
                updateTotalPrice();
            }
        });

        recyclerView.setAdapter(adapter);
        loadCartItems();

        proceedToPaymentButton.setOnClickListener(v -> proceedToPayment());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void loadCartItems() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("usuarios").document(userId).collection("carrito")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<CartItem> cartItems = CartDataHolder.getInstance().getCartItems();
                    cartItems.clear();
                    totalPrice = 0.0;
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        CartItem item = document.toObject(CartItem.class);
                        item.setId(document.getId());
                        db.collection("productos").document(item.getId())
                                .get()
                                .addOnSuccessListener(productDoc -> {
                                    if (productDoc.exists()) {
                                        item.setImagen(productDoc.getString("imagenUrl"));
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                        cartItems.add(item);
                        totalPrice += item.getPrecio() * item.getQuantity();
                    }
                    adapter.notifyDataSetChanged();
                    updateTotalPrice();
                });
    }

    private void updateTotalPrice() {
        totalPrice = 0.0;
        for (CartItem item : CartDataHolder.getInstance().getCartItems()) {
            totalPrice += item.getPrecio() * item.getQuantity();
        }
        totalPriceTextView.setText(String.format("Total: %.2f€", totalPrice));
    }

    private void proceedToPayment() {
        if (totalPrice <= 0) {
            Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        PayPalPaymentManager paymentManager = new PayPalPaymentManager(this);
        paymentManager.createPayment(String.format("%.2f", totalPrice), "EUR", new PayPalPaymentManager.PayPalPaymentCallback() {

            @Override
            public void onPaymentSuccess(String paymentId) {
                Toast.makeText(CartActivity.this, "Pago realizado con éxito. ID: " + paymentId, Toast.LENGTH_LONG).show();
                clearCart();
                openPaymentResultActivity(paymentId);
            }

            @Override
            public void onPaymentError(Exception e) {
                Toast.makeText(CartActivity.this, "Error al realizar el pago: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            @Override
            public void onPaymentRedirect(String approveUrl) {
                Intent intent = new Intent(CartActivity.this, PaymentWebViewActivity.class);
                intent.putExtra(PaymentWebViewActivity.EXTRA_URL, approveUrl);
                startActivity(intent);
            }
        });
    }

    private void clearCart() {
        String userId = auth.getCurrentUser().getUid();
        db.collection("usuarios").document(userId).collection("carrito")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        db.collection("usuarios").document(userId).collection("carrito")
                                .document(document.getId()).delete();
                    }
                    CartDataHolder.getInstance().clearCart();
                    adapter.notifyDataSetChanged();
                    totalPrice = 0.0;
                    updateTotalPrice();
                });
    }

    private void removeCartItem(CartItem item) {
        String userId = auth.getCurrentUser().getUid();
        db.collection("usuarios").document(userId).collection("carrito")
                .document(item.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    CartDataHolder.getInstance().getCartItems().remove(item);
                    adapter.notifyDataSetChanged();
                    updateTotalPrice();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CartActivity.this, "Error al eliminar el artículo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateCartItem(CartItem item, int newQuantity) {
        String userId = auth.getCurrentUser().getUid();
        db.collection("usuarios").document(userId).collection("carrito")
                .document(item.getId())
                .update("quantity", newQuantity)
                .addOnSuccessListener(aVoid -> {
                    item.setQuantity(newQuantity);
                    adapter.notifyDataSetChanged();
                    updateTotalPrice();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CartActivity.this, "Error al actualizar el artículo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void openPaymentResultActivity(String paymentId) {
        Intent intent = new Intent(CartActivity.this, PaymentResultActivity.class);
        intent.putExtra("payment_id", paymentId);
        startActivity(intent);
    }
}
