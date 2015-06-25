package com.fevi.fadong.support;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.fevi.fadong.LevelUpActivity;

import java.io.File;

/**
 * Created by 1000742 on 15. 6. 25..
 */
public class WebViewSetting {

    public static WebView appin(Activity activity, WebView webView) {
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
            File databasePath = activity.getDatabasePath("X-O2OSDK-WEB");
            settings.setDatabasePath(databasePath.getPath());
        }
        settings.setDefaultTextEncodingName("UTF-8");
        webView.setWebViewClient(new WebViewClientActivity(activity));
        webView.setWebChromeClient(new WebViewChromeClient());
        webView.addJavascriptInterface(new WebviewBridge(activity), "fevi");
        return webView;
    }

    private static class WebViewClientActivity extends WebViewClient {

        private Activity activity;
        private Handler handler;

        public WebViewClientActivity(Activity activity) {
            this.activity = activity;
            handler = new Handler();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(url.toLowerCase().startsWith("http") || url.toLowerCase().startsWith("https")) {
                view.loadUrl(url);
            } else {
                try {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    activity.startActivity(intent);
                } catch (Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast toast = Toast.makeText(activity, "해당 앱을 띄울 수 없습니다.", Toast.LENGTH_LONG);
                            toast.show();
                        }
                    });
                }

            }
            return true;
        }
    }

    private static class WebViewChromeClient extends WebChromeClient {

    }

    public static class WebviewBridge {

        private Activity activity;
        private Handler handler;

        public WebviewBridge(Activity activity) {
            this.activity = activity;
            handler = new Handler();
        }

        @JavascriptInterface
        public void closeWebview() {
            activity.finish();
        }

        @JavascriptInterface
        public void callToast(final String arg) { // must be final
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast toast = Toast.makeText(activity, arg, Toast.LENGTH_LONG);
                    toast.show();
                    //m_vibrator.vibrate(50);
                }
            });
        }
    }
}
