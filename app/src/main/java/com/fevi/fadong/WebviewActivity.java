package com.fevi.fadong;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.fevi.fadong.support.WebViewSetting;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;


public class WebviewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        Intent intent = getIntent();

        WebView webView = (WebView) findViewById(R.id.webViewFevi);
        WebViewSetting.appin(this, webView);

        webView.loadUrl(intent.getStringExtra("url"));

        App application = (App) getApplication();
        Tracker tracker = application.getDefaultTracker();

        tracker.setScreenName("Webview Activity");
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("webview")
                .setAction("show")
                .build());
    }
}
