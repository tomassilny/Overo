package com.tosiapps.materialbarcodescannerexample;

import android.text.Html;
import android.util.Log;

/**
 * Created by Tomas on 27. 1. 2018.
 */

class MyJavaScriptInterface {
    @SuppressWarnings("unused")
    public void processHTML(final String html)
    {
        Log.i("processed html",html);

        Thread OauthFetcher=new Thread(new Runnable() {

            @Override
            public void run() {

                String oAuthDetails=null;
                oAuthDetails= Html.fromHtml(html).toString();
                Log.i("oAuthDetails",oAuthDetails);

            }
        });OauthFetcher.start();
    }
}
