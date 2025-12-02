package com.nimc.agentonboarding.auth;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("/auth/refresh")
    Call<Map> refresh(@Body Map body);
}
