package com.tosiapps.materialbarcodescannerexample;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tosiapps.materialbarcodescanner.MaterialBarcodeScanner;
import com.tosiapps.materialbarcodescanner.MaterialBarcodeScannerBuilder;
import com.google.android.gms.vision.barcode.Barcode;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class Verify extends AppCompatActivity {
    // Declare variables
    NodeList nodelist;
    ProgressDialog pDialog;
    RelativeLayout state;
    TextView state_text;
    String code;
    public static final String BARCODE_KEY = "BARCODE";

    private Barcode barcodeResult;
    Button home, next;
    ProgressBar p;

    ImageView image;

    String s_code;
    String url;

    WebView wv;

    ArrayList<String> urls = new ArrayList<>();

    String URL = "https://ec.europa.eu/consumers/consumers_safety/safety_products/rapex/alerts/?event=main.weeklyReports.XML";


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        getSupportActionBar().setTitle("Výsledok kontroly");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pDialog = new ProgressDialog(Verify.this);
        // Set progressbar title
        pDialog.setTitle("Prebieha kontrola produktu");
        // Set progressbar message
        pDialog.setMessage("Kontrolujem databázu RAPEX a výsledky zo SOI...");
        pDialog.setIndeterminate(false);
        // Show progressbar
        pDialog.show();

        Bundle extras = getIntent().getExtras();
        code = extras.getString("code");

        code = Uri.encode(code, "utf-8");

        // Locate a TextView in your activity_main.xml layout

        state = (RelativeLayout) findViewById(R.id.state);
        state_text = (TextView) findViewById(R.id.state_text);
        home = (Button) findViewById(R.id.home);
        next = (Button) findViewById(R.id.next);
        p = (ProgressBar) findViewById(R.id.progressBar);
        image = (ImageView) findViewById(R.id.image);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan("overit");
                finish();
            }
        });

        wv=(WebView)findViewById(R.id.wv);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setLoadWithOverviewMode(true);
        wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setBuiltInZoomControls(true);
        wv.getSettings().setPluginState(WebSettings.PluginState.ON);
        wv.setWebViewClient(new WebSite.HelloWebViewClient());
        // Execute DownloadXML AsyncTask

        url = Uri.encode(code + " soi", "utf-8");

        List<Character> code_a = new ArrayList<>();

        int counter = 0;
        for( int i=0; i<code.length(); i++ ) {
            if( code.charAt(i) == '$' ) {
                counter++;
            }
        }

        for (int k = 0; k < counter; k++){
            code_a.add(code.charAt(k));
        }

        if (counter > 12){
            s_code = code_a.get(0) + "+" + code_a.get(1) + code_a.get(2) + code_a.get(3) + code_a.get(4) + code_a.get(5) +
                    code_a.get(6) + "+" +
                    code_a.get(7) + code_a.get(8) + code_a.get(9) + code_a.get(10) + code_a.get(11) + code_a.get(12);


        }else{
            s_code = code;
        }
        Ion.with(getApplicationContext())
                .load("https://www.google.sk/search?q=" + url)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                    if (result.contains("soi.sk")){
                        danger(code, "soi");
                    }else{
                        Ion.with(getApplicationContext())
                                .load("https://www.google.sk/search?q=" + s_code)
                                .asString()
                                .setCallback(new FutureCallback<String>() {
                                    @Override
                                    public void onCompleted(Exception e, String result) {
                                        if (result.contains("soi.sk")){
                                            danger(code, "soi");
                                        }else{
                                            Ion.with(getApplicationContext())
                                                    .load("https://www.google.sk/search?q=" + code + "+rapex")
                                                    .asString()
                                                    .setCallback(new FutureCallback<String>() {
                                                        @Override
                                                        public void onCompleted(Exception e, String result) {
                                                            if (result.contains("ec.europa.eu")){
                                                                danger("https://www.google.sk/search?q=" + code + "+rapex", "rapex");
                                                            }else{
                                                                new DownloadXML().execute(URL);
                                                            }

                                                        }
                                                    });
                                        }

                                    }
                                });

                    }

                    }
                });






    }

    // DownloadXML AsyncTask
    @SuppressLint("StaticFieldLeak")
    private class DownloadXML extends AsyncTask<String, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressbar

        }

        @Override
        protected Void doInBackground(String... Url) {
            try {
                URL url = new URL(Url[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                // Download the XML file
                Document doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
                // Locate the Tag Name
                nodelist = doc.getElementsByTagName("weeklyReport");

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Void args) {

            for (int temp = 0; temp < nodelist.getLength(); temp++) {
                Node nNode = nodelist.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    // Set the texts into TextViews from item nodes
                    // Get the title
                    if (!(eElement.hasAttribute(null))){


                        urls.add(getNode("URL", eElement));
                    }


                }
            }
            // Close progressbar


            for (int j = 0; j < 20; j++){

                    new DownloadDXML().execute(urls.get(j));

            }
            product_ok();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadDXML extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressbar
        }
        @Override
        protected Void doInBackground(String... Url) {
            try {
                URL url = new URL(Url[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                // Download the XML file
                Document doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
                // Locate the Tag Name
                nodelist = doc.getElementsByTagName("notification");

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void args) {
            for (int temp = 0; temp < nodelist.getLength(); temp++) {
                Node nNode = nodelist.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    // Set the texts into TextViews from item nodes 3800146259907
                    // Get the title
                    if (!(eElement.equals("<batchNumber_barcode/>"))) {
                        String number = getNode("batchNumber_barcode", eElement);
                        if (number.contains(code) || number.contains(s_code)) {
                            danger("RAPEX", "rapex");
                        }
                    }
                }
            }
        }
    }

    // getNode function
    private static String getNode(String sTag, Element eElement) {
        NodeList nlList;
        nlList = eElement.getElementsByTagName(sTag).item(0)
                .getChildNodes();
        Node nValue = nlList.item(0);
        if(nValue!=null)
        {
            return nValue.getNodeValue();
        }
        else
        {
            return "KEKS";
        }

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

    @SuppressLint("SetTextI18n")
    void danger(String url, String source){
        p.setVisibility(View.INVISIBLE);
        pDialog.dismiss();
        wv.setVisibility(View.VISIBLE);
        state.setBackgroundColor(Color.parseColor("#C62828"));
        state_text.setText("Nebezpečný produkt!");
        if (source.equals("soi")){
            wv.loadUrl("https://www.soi.sk/sk/Vyhladavanie.soi?s=" + url);
        }
    }

    @SuppressLint("SetTextI18n")
    void product_ok(){
        p.setVisibility(View.INVISIBLE);
        image.setVisibility(View.VISIBLE);
        state.setBackgroundColor(Color.parseColor("#43A047"));
        state_text.setText("Produkt je v poriadku.");
        pDialog.dismiss();
        wv.setVisibility(View.INVISIBLE);
    }

    private void startScan(final String s) {
        final MaterialBarcodeScanner materialBarcodeScanner = new MaterialBarcodeScannerBuilder()
                .withActivity(Verify.this)
                .withEnableAutoFocus(true)
                .withBleepEnabled(true)
                .withBackfacingCamera()
                .withCenterTracker()
                .withText("Skenujem...")
                .withResultListener(new MaterialBarcodeScanner.OnResultListener() {
                    @Override
                    public void onResult(Barcode barcode) {
                        barcodeResult = barcode;
                        Intent intent = new Intent(Verify.this, WebSite.class);
                        intent.putExtra("code", barcode.rawValue);
                        intent.putExtra("type", s);
                        startActivity(intent);
                        finish();
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
