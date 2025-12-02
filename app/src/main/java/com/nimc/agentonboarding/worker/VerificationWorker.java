package com.nimc.agentonboarding.worker;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.nimc.agentonboarding.api.ApiClient;
import com.nimc.agentonboarding.api.ApiService;
import com.nimc.agentonboarding.data.AgentEntity;
import com.nimc.agentonboarding.data.AppDatabase;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerificationWorker extends Worker {
    public VerificationWorker(@NonNull Context ctx, @NonNull WorkerParameters params) {
        super(ctx, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
            List<AgentEntity> pending = db.agentDao().getByStatus("PENDING");
            if (pending == null || pending.isEmpty()) return Result.success();

            Retrofit r = ApiClient.getClient(getApplicationContext(), "http://10.0.2.2:3000/");
            ApiService svc = r.create(ApiService.class);

            for (AgentEntity a : pending) {
                Map<String,Object> body = new HashMap<>();
                body.put("nin", a.nin);
                body.put("firstName", a.firstName);
                body.put("lastName", a.lastName);
                body.put("gender", a.gender);
                body.put("dob", a.dob);
                body.put("email", a.email);
                body.put("fepName", a.fepName);
                body.put("fepCode", a.fepCode);
                body.put("state", a.state);
                body.put("face", a.imageBase64);

                Call<Map<String,Object>> call = svc.verifyAgent(body);
                Response<Map<String,Object>> resp = call.execute();
                if (resp.isSuccessful() && resp.body() != null) {
                    String status = (String) resp.body().get("status");
                    if ("verified".equalsIgnoreCase(status)) {
                        a.status = "VERIFIED";
                        db.agentDao().insert(a); // simple way: re-insert; in real app use @Update
                    }
                }
            }
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.retry();
        }
    }
}
