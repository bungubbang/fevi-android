package com.fevi.fadong;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.File;


public class WebviewActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        Intent intent = getIntent();

        WebView webView = (WebView) findViewById(R.id.webViewFevi);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(false);
        settings.setSupportMultipleWindows(true);
        settings.setSupportZoom(false);
        settings.setBlockNetworkImage(false);
        settings.setLoadsImagesAutomatically(true);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            File databasePath = getDatabasePath("X-O2OSDK-WEB");
            settings.setDatabasePath(databasePath.getPath());
        }
        settings.setDefaultTextEncodingName("UTF-8");

        webView.loadUrl(intent.getStringExtra("url"));
    }
}
