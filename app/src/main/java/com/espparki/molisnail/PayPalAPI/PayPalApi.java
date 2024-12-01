package com.espparki.molisnail.PayPalAPI;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

class AccessTokenResponse {
    String access_token;
    String token_type;
}

class PaymentRequest {
    String intent;
    PurchaseUnit[] purchase_units;

    public PaymentRequest(String intent, PurchaseUnit[] purchase_units) {
        this.intent = intent;
        this.purchase_units = purchase_units;
    }
}

class PurchaseUnit {
    private Amount amount;
    private String description;

    public PurchaseUnit(Amount amount, String description) {
        this.amount = amount;
        this.description = description;
    }
}

class Amount {
    String currency_code;
    String value;

    public Amount(String currency_code, String value) {
        this.currency_code = currency_code;
        this.value = value;
    }
}

class PaymentResponse {
    String id;
    String status;
}

public interface PayPalApi {
    @FormUrlEncoded
    @POST("v1/oauth2/token")
    Call<AccessTokenResponse> getAccessToken(
            @Header("Authorization") String authHeader,
            @Field("grant_type") String grantType
    );

    @POST("v2/checkout/orders")
    Call<PaymentResponse> createPayment(
            @Header("Authorization") String authHeader,
            @Body PaymentRequest paymentRequest
    );
}
