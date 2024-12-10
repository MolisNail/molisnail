package com.espparki.molisnail.PayPalAPI;

import okhttp3.Credentials;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayPalTokenManager {

    public static void getAccessToken(PayPalTokenCallback callback) {
        PayPalApi api = PayPalService.getRetrofitInstance().create(PayPalApi.class);
        String authHeader = Credentials.basic(PayPalConfig.CLIENT_ID, PayPalConfig.SECRET);

        Call<AccessTokenResponse> call = api.getAccessToken(authHeader, "client_credentials");
        call.enqueue(new Callback<AccessTokenResponse>() {
            @Override
            public void onResponse(Call<AccessTokenResponse> call, Response<AccessTokenResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String accessToken = "Bearer " + response.body().getAccessToken();
                    callback.onTokenReceived(accessToken);
                } else {
                    callback.onTokenError(new Exception("Error al obtener el token: " + response.code() + " - " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<AccessTokenResponse> call, Throwable t) {
                callback.onTokenError(new Exception("Error de red: " + t.getMessage()));
            }
        });
    }

    public interface PayPalTokenCallback {
        void onTokenReceived(String token);

        void onTokenError(Exception e);
    }
}
