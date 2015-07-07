package com.fevi.fadong;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.fevi.fadong.domain.Member;
import com.fevi.fadong.support.FadongHttpClient;
import com.fevi.fadong.support.MemberInfoFactory;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


public class LoginActivity extends Activity {

    private static final String LOG_IN_URL = "http://www.appinkorea.co.kr/fevi/login.php";

    SharedPreferences loginPreferences;

    private String vid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        vid = getIntent().getStringExtra("vid");

        loginPreferences = getSharedPreferences(getResources().getString(R.string.loginPref), MODE_PRIVATE);
        String id = loginPreferences.getString("id", null);
        String password = loginPreferences.getString("password", null);
        boolean isAutoLogin = loginPreferences.getBoolean("isAutoLogin", false);

        final Member member = new Member();
        member.setId(id);
        member.setPassword(password);
        new MemberInfoFactory(member, this).getInfo();


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

        App application = (App) getApplication();
        Tracker tracker = application.getDefaultTracker();

        tracker.setScreenName("Login Activity");
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("login")
                .setAction("show")
                .build());
    }

    private void checkLoginCode(Context context, Member member, boolean isAutoLogin) {
        new LoginCall(this, member, isAutoLogin).execute();
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

    private class LoginCall extends AsyncTask <Object, Void, String> {

        private LoginActivity loginActivity;
        private Member member;
        private boolean isAutoLogin;

        private LoginCall(LoginActivity loginActivity, Member member, boolean isAutoLogin) {
            this.loginActivity = loginActivity;
            this.member = member;
            this.isAutoLogin = isAutoLogin;
        }

        @Override
        protected String doInBackground(Object... params) {
            return new FadongHttpClient().sendLogin(LOG_IN_URL, member.getParameter());
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Uri uri = Uri.parse("?" + result);
            switch (uri.getQueryParameter("code")) {
                case "0": // 로그인 실패
                    Toast.makeText(loginActivity, "ID / Password 가 일치 하지 않습니다.", Toast.LENGTH_SHORT).show();
                    break;
                case "1": // 로그인 성공
                    saveLogin(isAutoLogin, member);

                    Intent mainIntent = new Intent(loginActivity, MainActivity.class);
                    mainIntent.putExtra("member", mappingMemberInfo(member, uri));
                    mainIntent.putExtra("vid", vid);
                    loginActivity.startActivity(mainIntent);
                    loginActivity.finish();
                    break;
                case "2": // 로그인 성공 / 친구초대 5명 미만, 친구 초대 필요
                    saveLogin(isAutoLogin, member);

                    Intent inviteIntent = new Intent(loginActivity, InviteActivity.class);
                    inviteIntent.putExtra("member", mappingMemberInfo(member, uri));
                    loginActivity.startActivity(inviteIntent);
                    loginActivity.finish();
                    break;
            }
        }

        private Member mappingMemberInfo(Member member, Uri uri) {
            member.setLevel(uri.getQueryParameter("level"));
            member.setExperience(uri.getQueryParameter("exp"));
            member.setNextExperience(uri.getQueryParameter("next_exp"));
            member.setRuby(uri.getQueryParameter("rubi"));
            member.setHeart(uri.getQueryParameter("heart"));
            return member;
        }
    }

}
