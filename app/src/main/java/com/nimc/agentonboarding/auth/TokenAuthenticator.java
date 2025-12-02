package com.nimc.agentonboarding.auth;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TokenAuthenticator implements Authenticator {
    private final TokenManager tokenManager;
    private final String baseUrl;

    public TokenAuthenticator(TokenManager tm, String baseUrl) {
        this.tokenManager = tm;
        this.baseUrl = baseUrl;
    }

    @Override public Request authenticate(Route route, Response response) throws IOException {
        String refresh = tokenManager.getRefreshToken();
        if (refresh == null) return null;

        Retrofit r = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        AuthApi api = r.create(AuthApi.class);
        Call<Map> call = api.refresh(Collections.singletonMap("refreshToken", refresh));
        try {
            retrofit2.Response<Map> resp = call.execute();
            if (resp.isSuccessful() && resp.body() != null) {
                Map body = resp.body();
                String newAccess = (String) body.get("accessToken");
                String newRefresh = (String) body.get("refreshToken");
                tokenManager.saveTokens(newAccess, newRefresh);
                return response.request().newBuilder()
                        .header("Authorization", "Bearer " + newAccess)
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
