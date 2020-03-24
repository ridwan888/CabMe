package com.example.cabme.qrscanner;

import android.Manifest;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Vibrator;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cabme.R;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScannerQR extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    private TextView txtResult;
    Vibrator vibrate;

    // https://www.youtube.com/watch?v=MegowI4T_L8


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner);

        scannerView = (ZXingScannerView)findViewById(R.id.zxscan);
        txtResult = (TextView) findViewById(R.id.txt_result);

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        scannerView.setResultHandler(ScannerQR.this);
                        scannerView.startCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(ScannerQR.this,"You must accept to scan the QR code", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();
    }

    @Override
    public void handleResult(Result rawResult) {

        txtResult.setText(rawResult.getText());
        Toast.makeText(ScannerQR.this, "Payment Received", Toast.LENGTH_LONG).show();
        vibrate = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrate.vibrate(400);

//        scannerView.setFlash(false);

//        setContentView(R.layout.scanner);


    }
}
