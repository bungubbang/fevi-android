package com.fevi.fadong;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.fevi.fadong.domain.Member;
import com.fevi.fadong.support.LoginCall;
import com.fevi.fadong.support.MemberInfoFactory;
import com.google.common.base.Strings;


public class IntroActivity extends Activity {


    private static final String LOG_IN_URL = "http://www.appinkorea.co.kr/fevi/login.php";

    private SharedPreferences loginPreferences;
    private ProgressDialog mProgressDialog;

    private String vid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

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
