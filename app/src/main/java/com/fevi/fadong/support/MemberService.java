package com.fevi.fadong.support;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.fevi.fadong.LoginActivity;
import com.fevi.fadong.R;

/**
 * Created by 1000742 on 15. 3. 9..
 */
public class MemberService {

    public static void logout(Context context) {
        SharedPreferences loginPreferences = context.getSharedPreferences(context.getResources().getString(R.string.loginPref), context.MODE_PRIVATE);
        SharedPreferences.Editor loginPreEdit = loginPreferences.edit();
        loginPreEdit.putString("id", "");
        loginPreEdit.putString("password", "");
        loginPreEdit.putBoolean("isAutoLogin", false);

        loginPreEdit.apply();

        Intent loginIntent = new Intent(context, LoginActivity.class);
        context.startActivity(loginIntent);
    }
}
