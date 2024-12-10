package com.espparki.molisnail.PayPalAPI;

import android.content.Context;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayPalPaymentManager {
    private Context context;

    public PayPalPaymentManager(Context context) {
        this.context = context;
    }

    public void createPayment(String amount, String currency, PayPalPaymentCallback callback) {
        String formattedAmount = amount.replace(",", ".");
        PayPalTokenManager.getAccessToken(new PayPalTokenManager.PayPalTokenCallback() {
            @Override
            public void onTokenReceived(String token) {
                PayPalApi api = PayPalService.getRetrofitInstance().create(PayPalApi.class);
                PaymentRequest request = new PaymentRequest(
                        "CAPTURE",
                        new PurchaseUnit[]{new PurchaseUnit(new Amount(currency, formattedAmount), "Compra de ejemplo")}
                );

                api.createPayment(token, request).enqueue(new Callback<PaymentResponse>() {
                    @Override
                    public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            PaymentResponse paymentResponse = response.body();
                            String approveUrl = paymentResponse.getLinks().stream()
                                    .filter(link -> "approve".equals(link.getRel()))
                                    .map(PaymentResponse.Link::getHref)
                                    .findFirst()
                                    .orElse(null);

                            if (approveUrl != null) {
                                callback.onPaymentRedirect(approveUrl);
                            } else {
                                callback.onPaymentError(new Exception("No se encontró el enlace de aprobación."));
                            }
                        } else {
                            callback.onPaymentError(new Exception("Error en la respuesta del servidor: " + response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call<PaymentResponse> call, Throwable t) {
                        callback.onPaymentError(new Exception("Error de red: " + t.getMessage()));
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

        void onPaymentRedirect(String approveUrl);
    }
}
