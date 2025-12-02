package com.nimc.agentonboarding;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.WriterException;

/**
 * (For demo, we just generate a QR from a fake payload; you should call backend /agents/issue-slip in a real flow.)
 */
public class QRDisplayActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_qrdisplay);

        ImageView iv = findViewById(R.id.ivQr);

        // In production: fetch payload+signature from backend /agents/issue-slip
        String payload = "{\"nin\":\"00000000000\",\"name\":\"Demo Agent\",\"role\":\"Agent\",\"issuedAt\":\"2025-01-01T00:00:00Z\"}";
        String signature = "demo-signature-base64";
        String qrValue = Base64.encodeToString(payload.getBytes(), Base64.NO_WRAP) + "." + signature;

        try {
            BitMatrix bm = new MultiFormatWriter().encode(qrValue, BarcodeFormat.QR_CODE, 512,512);
            int w = bm.getWidth(), h = bm.getHeight();
            int[] pixels = new int[w*h];
            for (int y=0; y<h; y++) {
                int offset = y*w;
                for (int x=0; x<w; x++) {
                    pixels[offset+x] = bm.get(x,y) ? 0xFF000000 : 0xFFFFFFFF;
                }
            }
            Bitmap bmp = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
            bmp.setPixels(pixels,0,w,0,0,w,h);
            iv.setImageBitmap(bmp);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
