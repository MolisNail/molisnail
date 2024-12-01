package com.espparki.molisnail.PayPalAPI;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayPalPaymentManager {
    public static void createPayment(String amount, String currency, PayPalPaymentCallback callback) {
        // Asegurarse de que el valor del monto esté en el formato correcto
        String formattedAmount = amount.replace(",", ".");

        PayPalTokenManager.getAccessToken(new PayPalTokenManager.PayPalTokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                PayPalApi api = PayPalService.getRetrofitInstance().create(PayPalApi.class);
                PaymentRequest request = new PaymentRequest(
                        "CAPTURE",
                        new PurchaseUnit[]{new PurchaseUnit(new Amount(currency, formattedAmount), "Producto")}
                );

                Call<PaymentResponse> call = api.createPayment(token, request);

                call.enqueue(new Callback<PaymentResponse>() {
                    @Override
                    public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onPaymentSuccess(response.body().id);
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                                callback.onPaymentError(new Exception("Error en la respuesta del servidor: " + response.code() + ", " + errorBody));
                            } catch (IOException e) {
                                callback.onPaymentError(new Exception("Error al leer el cuerpo de la respuesta: " + e.getMessage()));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<PaymentResponse> call, Throwable t) {
                        callback.onPaymentError(new Exception(t));
                    }
                });
            }

            @Override
            public void onTokenError(Exception e) {
                callback.onPaymentError(e);
            }
        });
    }

    public interface PayPalPaymentCallback {
        void onPaymentSuccess(String paymentId);

        void onPaymentError(Exception e);
    }
}
