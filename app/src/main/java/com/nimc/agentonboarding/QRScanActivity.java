package com.nimc.agentonboarding;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimc.agentonboarding.api.ApiClient;
import com.nimc.agentonboarding.api.ApiService;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class QRScanActivity extends AppCompatActivity {
    private TextView tv;
    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);
        tv = findViewById(R.id.tvResult);
        new IntentIntegrator(this).initiateScan();
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (result != null) {
            String text = result.getContents();
            if (text == null) { finish(); return; }
            String[] parts = text.split("\\.");
            if (parts.length != 2) { tv.setText("Invalid QR format"); return; }
            String payloadJson = new String(Base64.decode(parts[0], Base64.DEFAULT));
            String signature = parts[1];

            Retrofit r = ApiClient.getClient(this, "http://10.0.2.2:3000/");
            ApiService svc = r.create(ApiService.class);
            JsonObject payloadObj = JsonParser.parseString(payloadJson).getAsJsonObject();
            Map<String,Object> body = new HashMap<>();
            body.put("payload", payloadObj);
            body.put("signature", signature);

            svc.verifyQr(body).enqueue(new Callback<Map<String,Object>>() {
                @Override public void onResponse(Call<Map<String,Object>> call, Response<Map<String,Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Object v = response.body().get("valid");
                        tv.setText("QR valid: " + v + "\nPayload: " + payloadJson);
                    } else tv.setText("Verification failed");
                }
                @Override public void onFailure(Call<Map<String,Object>> call, Throwable t) {
                    tv.setText("Error: " + t.getMessage());
                }
            });
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
