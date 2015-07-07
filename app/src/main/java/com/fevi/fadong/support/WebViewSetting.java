package com.fevi.fadong.support;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.fevi.fadong.IntroActivity;
import com.fevi.fadong.LoginActivity;
import com.nextapps.naswall.NASWall;
import com.tnkfactory.ad.TnkSession;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

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
                    e.printStackTrace();
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
            Intent intent = new Intent(activity, IntroActivity.class);
            activity.startActivity(intent);
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

        @JavascriptInterface
        public void logout() {
            MemberService.logout(activity);
            activity.finish();
        }

        @JavascriptInterface
        public void naswallOpen(String id) {
            NASWall.open(activity, id);
        }

        @JavascriptInterface
        public void calltnk(String id) {
            TnkSession.setUserName(activity, id);
            TnkSession.showAdList(activity, "무료 루비 받기");
        }

        @JavascriptInterface
        public void shareFacebook(String url, String image, String text) {
            String urlToShare = url;
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, urlToShare);
            intent.putExtra(Intent.EXTRA_TITLE, text);

            // See if official Facebook app is found
            boolean facebookAppFound = false;
            List<ResolveInfo> matches = activity.getPackageManager().queryIntentActivities(intent, 0);
            for (ResolveInfo info : matches) {
                if (info.activityInfo.packageName.toLowerCase().startsWith("com.facebook.katana")) {
                    intent.setPackage(info.activityInfo.packageName);
                    facebookAppFound = true;
                    break;
                }
            }

            // As fallback, launch sharer.php in a browser
            if (!facebookAppFound) {
                String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + urlToShare;
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
            }

            activity.startActivity(intent);
        }
    }
}
