package com.espparki.molisnail.PayPalAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;


class AccessTokenResponse {
    String access_token;
    String token_type;
    int expires_in;
    public String getAccessToken() {
        return access_token;
    }

    public void setAccessToken(String access_token) {
        this.access_token = access_token;
    }

    public String getTokenType() {
        return token_type;
    }

    public void setTokenType(String token_type) {
        this.token_type = token_type;
    }

    public int getExpiresIn() {
        return expires_in;
    }

    public void setExpiresIn(int expires_in) {
        this.expires_in = expires_in;
    }
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
        if (amount == null || description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Amount and description cannot be null or empty.");
        }
        this.amount = amount;
        this.description = description;
    }
    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
class Amount {
    String currency_code;
    String value;

    public Amount(String currency_code, String value) {
        if (currency_code == null || currency_code.isEmpty() || value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Currency code and value cannot be null or empty.");
        }
        this.currency_code = currency_code;
        this.value = value;
    }
    public String getCurrencyCode() {
        return currency_code;
    }

    public void setCurrencyCode(String currency_code) {
        this.currency_code = currency_code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
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

    @POST("v2/checkout/orders/{order_id}/capture")
    Call<Void> capturePayment(
            @Header("Authorization") String authHeader,
            @Path("order_id") String orderId
    );
}

class PaymentResponse {
    private String id;
    private String status;
    private List<Link> links;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public static class Link {
        private String href;
        private String rel;
        private String method;

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getRel() {
            return rel;
        }

        public void setRel(String rel) {
            this.rel = rel;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }
    }
}

class PayPalConfig {
    public static final String CLIENT_ID = "Ae_xipblKySakkOsaWDC4GCU5xjsruEwzctzkpfso5FHhfNKSrTkZZzP3naMITKAOH-i12EnrTfgU4iB";
    public static final String SECRET = "EOjTyLcIiuKkn2trUvHOqMnaoI2QZNJbUbKvtm34BCIphRtqD0kbjX2xJOCar_WBCEASpeiEDraWNE-K";

    public static final String BASE_URL = "https://api-m.sandbox.paypal.com/"; // URL de sandbox
}
