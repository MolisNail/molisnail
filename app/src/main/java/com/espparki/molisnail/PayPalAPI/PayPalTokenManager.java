package com.espparki.molisnail.PayPalAPI;

import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayPalTokenManager {
    private static String accessToken;

    public static void getAccessToken(PayPalTokenCallback callback) {
        if (accessToken != null) {
            callback.onTokenReceived(accessToken);
            return;
        }

        PayPalApi api = PayPalService.getRetrofitInstance().create(PayPalApi.class);
        String authHeader = Credentials.basic(PayPalConfig.CLIENT_ID, PayPalConfig.SECRET);

        // Llamada actualizada
        Call<AccessTokenResponse> call = api.getAccessToken(authHeader, "client_credentials");
        call.enqueue(new Callback<AccessTokenResponse>() {
            @Override
            public void onResponse(Call<AccessTokenResponse> call, Response<AccessTokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    accessToken = "Bearer " + response.body().access_token;
                    callback.onTokenReceived(accessToken);
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        callback.onTokenError(new Exception("Error en la respuesta del servidor: " + response.code() + ", " + errorBody));
                    } catch (Exception e) {
                        callback.onTokenError(new Exception("Error desconocido al procesar la respuesta: " + e.getMessage()));
                    }
                }
            }

            @Override
            public void onFailure(Call<AccessTokenResponse> call, Throwable t) {
                callback.onTokenError(new Exception("Error al hacer la solicitud de token: " + t.getMessage()));
            }
        });
    }


    public interface PayPalTokenCallback {
        void onTokenReceived(String token);

        void onTokenError(Exception e);
    }
}
