package com.fevi.fadong;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.fevi.fadong.support.WebViewSetting;


public class LevelUpActivity extends Activity {

    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.7f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.activity_level_up);

        SharedPreferences loginPreferences = getSharedPreferences(getResources().getString(R.string.loginPref), MODE_PRIVATE);
        String id = loginPreferences.getString("id", "");
        String password = loginPreferences.getString("password", "");

        if(id.equals("") || password.equals("")) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            this.startActivity(loginIntent);
            this.finish();
        }

        ImageView closeButton = (ImageView) findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        webView = (WebView) findViewById(R.id.levelup_webview);
        WebViewSetting.appin(this, webView);
        webView.loadUrl("http://appinkorea.co.kr/fevi/level_up.php?id=" + id + "&password=" + password);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
