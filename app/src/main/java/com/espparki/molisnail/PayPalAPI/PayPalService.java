package com.espparki.molisnail.PayPalAPI;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PayPalService {
    public static Retrofit getRetrofitInstance() {
        // Crear el interceptor de logging
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Configurar el cliente HTTP con el interceptor
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        // Configurar Retrofit con la URL base de PayPal
        return new Retrofit.Builder()
                .baseUrl(PayPalConfig.BASE_URL) // Usar URL definida en PayPalConfig
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
