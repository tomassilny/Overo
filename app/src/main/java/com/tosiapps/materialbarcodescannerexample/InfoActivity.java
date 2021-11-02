package com.tosiapps.materialbarcodescannerexample;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Bundle extras = getIntent().getExtras();
        final String type = extras.getString("type");

        TextView title = (TextView) findViewById(R.id.title);
        TextView text = (TextView) findViewById(R.id.text);
        Button rate = (Button) findViewById(R.id.rate);

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        switch (type){
            case "pomoc":
                getSupportActionBar().setTitle("Pomoc");
                title.setText("Pomoc");
                text.setText("Aplikácia Overo slúži pre skenovanie čiarových kódov a QR kódov, ktoré aplikácia spracúva a vyhodnocuje na základe" +
                        " užívateľom vybratej akcie. \n\nFunkcie aplikácie:\n\n" +
                        "• Overenie produktu - základná a primárna funkcia aplikácie. Funkcia overenie produktu oskenuje čiarový kód produktu a" +
                        " potom porovná čiarový kód z databázou RAPEX (Rapid Alert System) a zároveň zistí, či sa daný produkt nachádza na zozname" +
                        " nebezpečných produktov SOI (Slovenská Obchodná Inšpekcia), následne upozorní užívateľa a ak je produkt nebezpečný, tak zobrazí priamo" +
                        " v aplikácii informácie (fotky a iné detaily) nebezpečného produktu.\n" +
                        "\n• Porovnanie produktov - na základe oskenovaného čiarového kódu zobrazí na porovnávacej stránke Heureka produkt + jeho rôzne" +
                        " ceny.\n\n" +
                        "• Skenovanie QR kódu - aplikácia oskenuje QR kód, ktorý následne spracuje a zobrazí webovú lokalitu, ktorú daný kód reprezentuje.\n\n" +
                        "• Vyhľadávanie kódu na internete - jednoduchá funkcia, ktorá daný kód vyhľadá na Google.");
                break;
            case "info":
                rate.setVisibility(View.VISIBLE);
                getSupportActionBar().setTitle("Informácie o aplikácii");
                title.setText("O aplikácii");
                text.setGravity(View.TEXT_ALIGNMENT_CENTER);
                text.setText("Verzia aplikácie: 1.0.1 \nZdroje: SOI, RAPEX \nKontakt: tosiapps@gmail.com \n\n" +
                        "Aplikácia bola spolahlivo testovaná. V prípade akýchkoľvek otázok nás neváhajte kontaktovať.\n\n" +
                        "© 2018\n\nOhodnoťte našu aplikáciu: ");
                break;
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
}
