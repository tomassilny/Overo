package com.tosiapps.materialbarcodescannerexample;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.tosiapps.materialbarcodescanner.MaterialBarcodeScanner;
import com.tosiapps.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;


public class WebSite extends AppCompatActivity {

    Button home, next;
    public static final String BARCODE_KEY = "BARCODE";
    private Barcode barcodeResult;
    WebView wb;

    public static class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }
    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_site);

        Bundle extras = getIntent().getExtras();
        String code = extras.getString("code");
        final String type = extras.getString("type");

        switch (type){
            case "porovnat":
                getSupportActionBar().setTitle("Porovnanie produktov");
                break;
            case "qr":
                getSupportActionBar().setTitle("Skenovanie QR kódu");
                break;
            case "internet":
                getSupportActionBar().setTitle("Výsledky vyhľadávania");
                break;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        home = (Button) findViewById(R.id.home);
        next = (Button) findViewById(R.id.next);

        wb=(WebView)findViewById(R.id.web);
        wb.getSettings().setJavaScriptEnabled(true);
        wb.getSettings().setLoadWithOverviewMode(true);
        wb.getSettings().setUseWideViewPort(true);
        wb.getSettings().setBuiltInZoomControls(true);
        wb.getSettings().setPluginState(WebSettings.PluginState.ON);
        wb.setWebViewClient(new HelloWebViewClient());
        switch (type){
            case "porovnat":
                wb.loadUrl("https://www.heureka.sk/?h%5Bfraze%5D=" + code);
                break;
            case "overit":
                Intent intent = new Intent(WebSite.this, Verify.class);
                intent.putExtra("code", code);
                startActivity(intent);
                finish();
                break;
            case "qr":
                wb.loadUrl(code);
                break;
            case "internet":
                wb.loadUrl("https://www.google.sk/search?q=" + code);
                break;
        }

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan(type);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void startScan(final String s) {
        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(WebSite.this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withCenterTracker()
                .withText("Skenujem...")
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) {
                        barcodeResult = barcode;
                        Intent intent = new Intent(WebSite.this, WebSite.class);
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
