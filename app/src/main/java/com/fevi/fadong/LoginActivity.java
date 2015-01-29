package com.fevi.fadong;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.fevi.fadong.domain.Member;
import com.fevi.fadong.support.FadongHttpClient;
import com.fevi.fadong.support.MemberInfoFactory;
import com.google.common.base.Strings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;


public class LoginActivity extends Activity {

    private static final String LOG_IN_URL = "http://www.appinkorea.co.kr/fevi/login.php";

    SharedPreferences loginPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginPreferences = getSharedPreferences(getResources().getString(R.string.loginPref), MODE_PRIVATE);
        String id = loginPreferences.getString("id", null);
        String password = loginPreferences.getString("password", null);
        boolean isAutoLogin = loginPreferences.getBoolean("isAutoLogin", false);

        final Member member = new Member();
        member.setId(id);
        member.setPassword(password);
        new MemberInfoFactory(member, this).getInfo();

        if(checkAutoLogin(id, password, isAutoLogin)) {
            checkLoginCode(this, member, isAutoLogin);
        }

        Button loginButton = (Button) findViewById(R.id.fa_login_button);
        loginButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                member.setId(((EditText) findViewById(R.id.fa_id_field)).getText().toString());
                member.setPassword(((EditText) findViewById(R.id.fa_password_field)).getText().toString());
                boolean isAutoLogin = ((CheckBox) findViewById(R.id.fa_auto_field)).isChecked();

                checkLoginCode(v.getContext(), member, isAutoLogin);

            }
        });

        Button signUpButton = (Button) findViewById(R.id.fa_signup_button);
        signUpButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AgreementActivity.class);
                v.getContext().startActivity(intent);
            }
        });
    }

    private boolean checkAutoLogin(String id, String password, boolean isAutoLogin) {
        return (!Strings.isNullOrEmpty(id) && !Strings.isNullOrEmpty(password) && isAutoLogin);
    }

    private void checkLoginCode(Context context, Member member, boolean isAutoLogin) {
        Log.e("member1", member.getParameter());
        String result = null;
        try {
            result = (String) new LoginCall().execute(member).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        Uri uri = Uri.parse("?" + result);
        Log.e("result", result);
        Log.e("uri", uri.getQueryParameter("code"));
        switch (uri.getQueryParameter("code")) {
            case "0": // 로그인 실패
                Toast.makeText(context, "ID / Password 가 일치 하지 않습니다.", Toast.LENGTH_SHORT).show();
                break;
            case "1": // 로그인 성공
                saveLogin(isAutoLogin, member);

                Intent mainIntent = new Intent(context, MainActivity.class);
                context.startActivity(mainIntent);
                break;
            case "2": // 로그인 성공 / 친구초대 5명 미만, 친구 초대 필요
                saveLogin(isAutoLogin, member);

                Intent inviteIntent = new Intent(context, InviteActivity.class);
                context.startActivity(inviteIntent);
                break;
        }
    }

    private void saveLogin(boolean isAutoLogin, Member member) {

        if(isAutoLogin) {
            SharedPreferences.Editor loginPreEdit = loginPreferences.edit();

            loginPreEdit.putString("id", member.getId());
            loginPreEdit.putString("password", member.getPassword());
            loginPreEdit.putBoolean("isAutoLogin", isAutoLogin);

            loginPreEdit.apply();
        }
    }

    public class LoginCall extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            Member member = (Member) params[0];
            return new FadongHttpClient().sendLogin(LOG_IN_URL, member.getParameter());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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


}
