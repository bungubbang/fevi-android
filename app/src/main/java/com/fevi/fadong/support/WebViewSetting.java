package com.fevi.fadong.support;

import android.app.Activity;
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

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.fevi.fadong.App;
import com.fevi.fadong.IntroActivity;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nextapps.naswall.NASWall;
import com.tnkfactory.ad.TnkSession;

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

            App application = (App) activity.getApplication();
            Tracker tracker = application.getDefaultTracker();

            tracker.setScreenName("Webview close");
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("WEBVIEW")
                    .setAction("close")
                    .build());

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
            App application = (App) activity.getApplication();
            Tracker tracker = application.getDefaultTracker();

            tracker.setScreenName("Webview logout button");
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("WEBVIEW")
                    .setAction("logout")
                    .build());

            MemberService.logout(activity);
            activity.finish();
        }

        @JavascriptInterface
        public void naswallOpen(String id) {
            App application = (App) activity.getApplication();
            Tracker tracker = application.getDefaultTracker();

            tracker.setScreenName("Webview naswall");
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("NASWALL")
                    .setAction("click")
                    .setLabel(id)
                    .build());

            NASWall.open(activity, id);
        }

        @JavascriptInterface
        public void calltnk(String id) {
            App application = (App) activity.getApplication();
            Tracker tracker = application.getDefaultTracker();

            tracker.setScreenName("Webview tnk");
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("TNK")
                    .setAction("click")
                    .setLabel(id)
                    .build());

            TnkSession.setUserName(activity, id);
            TnkSession.showAdList(activity, "무료 루비 받기");
        }

        @JavascriptInterface
        public void shareFacebook(String url, String image, String text) {

            App application = (App) activity.getApplication();
            Tracker tracker = application.getDefaultTracker();

            tracker.setScreenName("Share Webview");
            tracker.send(new HitBuilders.EventBuilder()
                    .setAction("Action")
                    .setAction("share")
                    .setLabel(url)
                    .build());

            ShareLinkContent content = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(url))
                    .setImageUrl(Uri.parse(image))
                    .setContentTitle("문상주는 동영상")
                    .setContentDescription(text)
                    .build();

            if (ShareDialog.canShow(ShareLinkContent.class)) {
                ShareDialog.show(activity, content);
            } else {
                String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + url;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
                activity.startActivity(intent);
            }
        }
    }
}
