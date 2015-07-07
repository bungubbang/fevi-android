package com.fevi.fadong;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.fevi.fadong.domain.Member;
import com.fevi.fadong.support.LoginCall;
import com.fevi.fadong.support.MemberInfoFactory;
import com.fevi.fadong.support.NetworkManager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.common.base.Strings;
import com.nextapps.naswall.NASWall;
import com.parse.Parse;
import com.parse.ParseInstallation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class IntroActivity extends Activity {


    private static final String LOG_IN_URL = "http://www.appinkorea.co.kr/fevi/login.php";

    private SharedPreferences loginPreferences;
    private ProgressDialog mProgressDialog;

    private String vid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        App application = (App) getApplication();
        Tracker tracker = application.getDefaultTracker();

        tracker.setScreenName("Intro Activity");
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("intro")
                .setAction("show")
                .build());

        if (!NetworkManager.isOnline(this)) {
            Toast.makeText(this, "해당 앱을 사용하려면 인터넷에 접속하여야 합니다. 인터넷에 접속후 다시 실행해 주십시오.", Toast.LENGTH_LONG).show();
            return;
        }

        FacebookSdk.sdkInitialize(this);
        NASWall.init(this, false);

        vid = getIntent().getStringExtra("vid");

        loginPreferences = getSharedPreferences(getResources().getString(R.string.loginPref), MODE_PRIVATE);
        String id = loginPreferences.getString("id", null);
        String password = loginPreferences.getString("password", null);
        boolean isAutoLogin = loginPreferences.getBoolean("isAutoLogin", false);
        mProgressDialog = ProgressDialog.show(IntroActivity.this, "", "잠시만 기다려 주세요.", true);

        if(!Strings.isNullOrEmpty(id) && !Strings.isNullOrEmpty(password) && isAutoLogin) {
            Member member = new Member();
            member.setId(id);
            member.setPassword(password);
            new MemberInfoFactory(member, this).getInfo();
            new LoginCall(this, true, member, mProgressDialog).execute(this.vid);
        } else {
            mProgressDialog.dismiss();
            Intent loginIntent = new Intent(this, LoginActivity.class);
            this.startActivity(loginIntent);
            this.finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_intro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void finishActivity() {
        this.finish();
    }



}
