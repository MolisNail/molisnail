package com.espparki.molisnail.tienda;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.espparki.molisnail.MainActivity;
import com.espparki.molisnail.R;

import java.util.List;

public class PaymentResultActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PaymentResultAdapter adapter;
    private TextView paymentIdText;
    private Button acceptButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tienda_activity_payment_result);

        recyclerView = findViewById(R.id.payment_result_recycler);
        paymentIdText = findViewById(R.id.payment_id_text);
        acceptButton = findViewById(R.id.btn_accept);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String paymentId = getIntent().getStringExtra("payment_id");
        List<CartItem> cartItems = getIntent().getParcelableArrayListExtra("cart_items");
        double totalPrice = getIntent().getDoubleExtra("total_price", 0.0);

        adapter = new PaymentResultAdapter(cartItems);
        recyclerView.setAdapter(adapter);

        paymentIdText.setText(String.format("ID del Pago: %s\nTotal: %.2f€", paymentId, totalPrice));

        acceptButton.setOnClickListener(v -> {
            CartDataHolder.getInstance().clearCart();
            Intent intent = new Intent(PaymentResultActivity.this, MainActivity.class);
            startActivity(intent);

            finish();
        });
    }

}
