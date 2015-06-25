package com.fevi.fadong.support;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.fevi.fadong.LoginActivity;
import com.fevi.fadong.R;

/**
 * Created by 1000742 on 15. 3. 2..
 */
public class AddExperienceCall extends AsyncTask<Object, Void, String> {

    private static final String EXP_UP_URL = "http://www.appinkorea.co.kr/fevi/expup.php";
    private Context context;
    private SharedPreferences loginPreferences;

    public AddExperienceCall(Context context) {
        this.context = context;
        loginPreferences = context.getSharedPreferences(context.getResources().getString(R.string.loginPref), context.MODE_PRIVATE);
    }

    @Override
    protected String doInBackground(Object... params) {
        String id = loginPreferences.getString("id", null);
        String password = loginPreferences.getString("password", null);
        String videoId = (String) params[0];
        return new FadongHttpClient().sendLogin(EXP_UP_URL, "id=" + id + "&password=" + password + "&vid=" + videoId);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        Uri uri = Uri.parse("?" + result);
        if(uri.getQueryParameter("code").equals("0")) {
            Log.e("fadong", "상세카드 불러오기의 회원 정보가 일치하지 않습니다.");
            Intent loginIntent = new Intent(context, LoginActivity.class);
            context.startActivity(loginIntent);
            return;
        }
        Log.e("fadong", "exp up " + result);
        switch (uri.getQueryParameter("exp_code")) {
            case "0": // 경험치가 최대 경험치
                Toast.makeText(context, "최대 경험치에 도달하였습니다. 레벨업이 필요합니다.", Toast.LENGTH_LONG).show();
                break;
            case "1": // 경험치 업 성공
                Toast.makeText(context, "경험치 업에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                break;
            case "2": //
                Toast.makeText(context, "경험치 업에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
