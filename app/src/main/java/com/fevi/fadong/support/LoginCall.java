package com.fevi.fadong.support;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.fevi.fadong.IntroActivity;
import com.fevi.fadong.InviteActivity;
import com.fevi.fadong.LoginActivity;
import com.fevi.fadong.MainActivity;
import com.fevi.fadong.R;
import com.fevi.fadong.domain.Member;

/**
 * Created by 1000742 on 15. 2. 25..
 */
public class LoginCall extends AsyncTask <Object, Void, String>{

    private static final String LOG_IN_URL = "http://www.appinkorea.co.kr/fevi/login.php";
    private boolean isAutoLogin;
    private Member member;
    private IntroActivity introActivity;
    private ProgressDialog mProgressDialog;
    SharedPreferences loginPreferences;
    private String vid;


    public LoginCall(IntroActivity introActivity, boolean isAutoLogin, Member member, ProgressDialog mProgressDialog) {
        this.introActivity = introActivity;
        this.isAutoLogin = isAutoLogin;
        this.member = member;
        this.mProgressDialog = mProgressDialog;

        loginPreferences = introActivity.getSharedPreferences(introActivity.getResources().getString(R.string.loginPref), introActivity.MODE_PRIVATE);
    }

    @Override
    protected String doInBackground(Object... params) {
        Object param = params[0];
        if(param != null && !((String) param).isEmpty()) {
            vid = (String) param;
        }
        return new FadongHttpClient().sendLogin(LOG_IN_URL, member.getParameter());
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        mProgressDialog.dismiss();

        Uri uri = Uri.parse("?" + result);
        switch (uri.getQueryParameter("code")) {
            case "0":
                Intent loginIntent = new Intent(introActivity, LoginActivity.class);
                loginIntent.putExtra("vid", vid);
                introActivity.startActivity(loginIntent);
                introActivity.finish();
                break;
            case "1": // 로그인 성공
                saveLogin(isAutoLogin, member);

                Intent mainIntent = new Intent(introActivity, MainActivity.class);
                mainIntent.putExtra("member", mappingMemberInfo(member, uri));
                mainIntent.putExtra("vid", vid);
                introActivity.startActivity(mainIntent);
                introActivity.finish();
                break;
            case "2": // 로그인 성공 / 친구초대 5명 미만, 친구 초대 필요
                saveLogin(isAutoLogin, member);
                Intent inviteIntent = new Intent(introActivity, InviteActivity.class);
                inviteIntent.putExtra("member", mappingMemberInfo(member, uri));
                introActivity.startActivity(inviteIntent);
                introActivity.finish();
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

    private void saveLogin(boolean isAutoLogin, Member member) {

        if(isAutoLogin) {
            SharedPreferences.Editor loginPreEdit = loginPreferences.edit();

            loginPreEdit.putString("id", member.getId());
            loginPreEdit.putString("password", member.getPassword());
            loginPreEdit.putBoolean("isAutoLogin", isAutoLogin);

            loginPreEdit.apply();
        }
    }
}
