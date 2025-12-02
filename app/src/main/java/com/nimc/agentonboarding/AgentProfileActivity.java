package com.nimc.agentonboarding;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.nimc.agentonboarding.data.AgentEntity;
import com.nimc.agentonboarding.data.AppDatabase;

import java.util.List;

public class AgentProfileActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_agent_profile);

        ImageView iv = findViewById(R.id.ivPhoto);
        TextView tv = findViewById(R.id.tvInfo);
        Button btnShowQr = findViewById(R.id.btnShowQr);

        new Thread(() -> {
            AppDatabase db = AppDatabase.getDatabase(this);
            List<AgentEntity> verified = db.agentDao().getByStatus("VERIFIED");
            AgentEntity a = verified != null && !verified.isEmpty() ? verified.get(0) : null;
            runOnUiThread(() -> {
                if (a == null) {
                    tv.setText("No verified agent yet.");
                    btnShowQr.setEnabled(false);
                } else {
                    tv.setText(a.firstName + " " + a.lastName + "\nNIN: " + a.nin);
                    if (a.imageBase64 != null) {
                        byte[] bytes = Base64.decode(a.imageBase64, Base64.DEFAULT);
                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        iv.setImageBitmap(bmp);
                    }
                    btnShowQr.setOnClickListener(v -> {
                        Intent i = new Intent(this, QRDisplayActivity.class);
                        i.putExtra("agentId", a.id);
                        startActivity(i);
                    });
                }
            });
        }).start();
    }
}
