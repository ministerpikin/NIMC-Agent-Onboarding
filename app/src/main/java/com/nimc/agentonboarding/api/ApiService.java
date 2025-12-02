package com.nimc.agentonboarding.api;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/verify-agent")
    Call<Map<String,Object>> verifyAgent(@Body Map<String,Object> body);

    @POST("/agents/issue-slip")
    Call<Map<String,Object>> issueSlip(@Body Map<String,Object> body);

    @POST("/agents/verify-qr")
    Call<Map<String,Object>> verifyQr(@Body Map<String,Object> body);
}
