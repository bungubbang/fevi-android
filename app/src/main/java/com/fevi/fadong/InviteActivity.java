package com.fevi.fadong;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.fevi.fadong.domain.Member;
import com.fevi.fadong.support.MemberService;
import com.fevi.fadong.support.WebViewSetting;


public class InviteActivity extends Activity {

    private SharedPreferences loginPreferences;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("친구초대");
        setContentView(R.layout.activity_invite);

        loginPreferences = getSharedPreferences(getResources().getString(R.string.loginPref), MODE_PRIVATE);

        Intent intent = getIntent();
        Member member = (Member) intent.getSerializableExtra("member");

        webView = (WebView) findViewById(R.id.invite_webView);
        WebViewSetting.appin(this, webView);

        webView.loadUrl("http://www.appinkorea.co.kr/fevi/invite.php?id=" + member.getId() + "&password=" + member.getPassword());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event

        // Handle your other action bar items...
        switch (item.getItemId()) {
            case R.id.fadong_logout:
                MemberService.logout(this);
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
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