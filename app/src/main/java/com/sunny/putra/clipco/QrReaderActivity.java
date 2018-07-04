package com.sunny.putra.clipco;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.PointF;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.widget.TextView;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

public class QrReaderActivity extends Activity implements QRCodeReaderView.OnQRCodeReadListener {

    private QRCodeReaderView qrCodeReaderView;
    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_reader);
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
        qrCodeReaderView = findViewById(R.id.qrdecoderview);
        tv = findViewById(R.id.tvText);

        qrCodeReaderView.setOnQRCodeReadListener(this);
        qrCodeReaderView.setQRDecodingEnabled(true);
        qrCodeReaderView.setAutofocusInterval(2000L);
        qrCodeReaderView.setTorchEnabled(true);
        qrCodeReaderView.setBackCamera();
        qrCodeReaderView.setHorizontalFadingEdgeEnabled(true);
        qrCodeReaderView.animate();
        qrCodeReaderView.startCamera();

    }

    public void playBeepSound() {
        ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
        toneGen1.startTone(ToneGenerator.TONE_CDMA_HIGH_S_X4, 150);
        toneGen1.release();
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        playBeepSound();
        tv.setText(text);
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(text, text);
        cm.setPrimaryClip(clip);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        qrCodeReaderView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        qrCodeReaderView.stopCamera();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);

    }
}

