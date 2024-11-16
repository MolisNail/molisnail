package com.espparki.molisnail;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private WebView lightwidgetWebView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Configuración de LightWidget
        lightwidgetWebView = view.findViewById(R.id.lightwidgetWebView);

        // Configuración del WebView
        WebSettings webSettings = lightwidgetWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true); // Habilita almacenamiento DOM
        webSettings.setAllowFileAccess(true); // Permite acceso a archivos locales
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW); // Permite contenido mixto

        // Establece un WebViewClient personalizado para manejar URLs específicas
        lightwidgetWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Si la URL tiene un esquema "intent://" o "instagram://", intenta abrir la app de Instagram
                if (url.startsWith("intent://") || url.startsWith("instagram://")) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        if (intent != null) {
                            startActivity(intent);
                            return true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // Carga otras URLs en el WebView
                    view.loadUrl(url);
                }
                return true;
            }
        });

        // Cargar la URL del widget de LightWidget
        lightwidgetWebView.loadUrl("https://cdn.lightwidget.com/widgets/59ec7a51113158288c2ea0d0d6ec540b.html");

        return view;
    }
}
