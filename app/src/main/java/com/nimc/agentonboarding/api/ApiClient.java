package com.nimc.agentonboarding.api;

import android.content.Context;
import com.nimc.agentonboarding.auth.AuthInterceptor;
import com.nimc.agentonboarding.auth.TokenAuthenticator;
import com.nimc.agentonboarding.auth.TokenManager;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static Retrofit getClient(Context ctx, String baseUrl) {
        TokenManager tm = new TokenManager(ctx);

        HttpLoggingInterceptor log = new HttpLoggingInterceptor();
        log.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient http = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new AuthInterceptor(tm))
                .authenticator(new TokenAuthenticator(tm, baseUrl))
                .addInterceptor(log)
                .retryOnConnectionFailure(true)
                .build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(http)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
