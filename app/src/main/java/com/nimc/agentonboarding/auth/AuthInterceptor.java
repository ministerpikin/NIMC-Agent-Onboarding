package com.nimc.agentonboarding.auth;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final TokenManager tokenManager;
    public AuthInterceptor(TokenManager tm) { this.tokenManager = tm; }

    @Override public Response intercept(Chain chain) throws IOException {
        Request req = chain.request();
        String token = tokenManager.getAccessToken();
        if (token != null) {
            Request nr = req.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(nr);
        }
        return chain.proceed(req);
    }
}
