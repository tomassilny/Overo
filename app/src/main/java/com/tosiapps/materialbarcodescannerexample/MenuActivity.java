package com.tosiapps.materialbarcodescannerexample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.balysv.materialripple.MaterialRippleLayout;
import com.tosiapps.materialbarcodescanner.MaterialBarcodeScanner;
import com.tosiapps.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;


public class MenuActivity extends AppCompatActivity {
    public static final String BARCODE_KEY = "BARCODE";

    private Barcode barcodeResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(com.tosiapps.materialbarcodescanner.R.layout.activity_menu);

        MaterialRippleLayout porovnat = (MaterialRippleLayout)findViewById(R.id.porovnat);
        porovnat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan("porovnat");
            }
        });

        MaterialRippleLayout overit = (MaterialRippleLayout)findViewById(R.id.overit);
        overit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan("overit");
            }
        });

        MaterialRippleLayout qr = (MaterialRippleLayout)findViewById(R.id.qr);
        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan("qr");
            }
        });

        MaterialRippleLayout internet = (MaterialRippleLayout)findViewById(R.id.internet);
        internet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan("internet");
            }
        });

        MaterialRippleLayout pomoc = (MaterialRippleLayout)findViewById(R.id.pomoc);
        pomoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                intent.putExtra("type", "pomoc");
                startActivity(intent);
            }
        });

        MaterialRippleLayout info = (MaterialRippleLayout)findViewById(R.id.info);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                intent.putExtra("type", "info");
                startActivity(intent);
            }
        });

    }
    private void startScan(final String s) {
        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(MenuActivity.this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withCenterTracker()
                .withText("Skenujem...")
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) {
                        barcodeResult = barcode;
                        Intent intent = new Intent(MenuActivity.this, WebSite.class);
                        intent.putExtra("code", barcode.rawValue);
                        intent.putExtra("type", s);
                        startActivity(intent);
                    }
                })
                .build();
        materialBarcodeScanner.startScan();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(BARCODE_KEY, barcodeResult);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != MaterialBarcodeScanner.RC_HANDLE_CAMERA_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startScan("-");
            return;
        }
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(android.R.string.ok, listener)
                .show();
    }
}
