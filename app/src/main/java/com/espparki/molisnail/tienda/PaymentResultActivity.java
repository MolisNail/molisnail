package com.espparki.molisnail.tienda;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.espparki.molisnail.R;

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

        // Obtener los datos del carrito desde CartDataHolder
        adapter = new PaymentResultAdapter(CartDataHolder.getInstance().getCartItems());
        recyclerView.setAdapter(adapter);

        // Mostrar el ID del pago
        String paymentId = getIntent().getStringExtra("payment_id");
        paymentIdText.setText(String.format("ID del Pago: %s", paymentId));

        // Botón para finalizar y regresar
        acceptButton.setOnClickListener(v -> finish());
    }
}
