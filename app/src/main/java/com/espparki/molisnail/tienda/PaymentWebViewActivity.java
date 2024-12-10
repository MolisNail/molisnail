package com.espparki.molisnail.tienda;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.espparki.molisnail.PayPalAPI.PayPalApi;
import com.espparki.molisnail.PayPalAPI.PayPalService;
import com.espparki.molisnail.PayPalAPI.PayPalTokenManager;
import com.espparki.molisnail.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentWebViewActivity extends AppCompatActivity {
    public static final String EXTRA_URL = "approve_url";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_webview);

        webView = findViewById(R.id.payment_webview);

        // Configuración del WebView para garantizar compatibilidad con PayPal
        WebSettings webSettings = webView.getSettings();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webSettings.setUserAgentString(
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36"
        );

        String url = getIntent().getStringExtra(EXTRA_URL);

        if (url != null) {
            webView.setWebViewClient(new WebViewClient() {
                @Override

                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    String redirectUrl = request.getUrl().toString();
                    Log.d("WebView", "Redirigiendo a: " + redirectUrl);
                        Uri uri = Uri.parse(redirectUrl);
                        String paymentId = uri.getQueryParameter("token");
                        Intent intent = new Intent(PaymentWebViewActivity.this, PaymentResultActivity.class);
                        intent.putExtra("payment_id", paymentId);
                        intent.putParcelableArrayListExtra("cart_items", new ArrayList<>(CartDataHolder.getInstance().getCartItems()));
                        intent.putExtra("total_price", CartActivity.getTotalPrice());
                        startActivity(intent);

                        finish();
                        return true;

                }


                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    Log.d("WebView", "Página cargada: " + url);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Log.e("WebViewError", "Error: " + description + " en URL: " + failingUrl);
                    Toast.makeText(PaymentWebViewActivity.this, "Error al cargar la página.", Toast.LENGTH_LONG).show();
                }
            });


            webView.loadUrl(url);
        } else {
            Toast.makeText(this, "No se encontró una URL válida para la aprobación.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void capturePayment(String token) {
        PayPalTokenManager.getAccessToken(new PayPalTokenManager.PayPalTokenCallback() {
            @Override
            public void onTokenReceived(String accessToken) {
                PayPalService.getRetrofitInstance().create(PayPalApi.class)
                        .capturePayment(accessToken, token)
                        .enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(PaymentWebViewActivity.this, "Pago completado con éxito.", Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Log.e("PayPal", "Error al capturar el pago. Código: " + response.code());
                                    Toast.makeText(PaymentWebViewActivity.this, "Error al capturar el pago.", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.e("PayPal", "Error de red: " + t.getMessage());
                                Toast.makeText(PaymentWebViewActivity.this, "Fallo en la captura del pago.", Toast.LENGTH_LONG).show();
                            }
                        });
            }

            @Override
            public void onTokenError(Exception e) {
                Log.e("PayPal", "Error al obtener el token: " + e.getMessage());
                Toast.makeText(PaymentWebViewActivity.this, "Error al obtener el token.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
