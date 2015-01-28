package com.fevi.fadong;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fevi.fadong.adapter.dto.Card;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class SignUpActivity extends Activity {

    private static final String SIGN_UP_URL = "http://www.appinkorea.co.kr/fevi/join.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final MemberInfo member = new MemberInfo();

        // 푸시 동의
        Intent intent = getIntent();
        boolean pushAgree = intent.getBooleanExtra("pushAgree", false);
        member.setPushAgree(String.valueOf(pushAgree? 1:0));

        // ID
        final TextView idView = (TextView) findViewById(R.id.signup_id);
        // password
        final TextView passwordView = (TextView) findViewById(R.id.signup_password);
        // password repeat
        final TextView passwordViewRepeat = (TextView) findViewById(R.id.signup_password_repeat);


        getInfo(member);

        Button signUpButton = (Button) findViewById(R.id.signup_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                member.setId(idView.getText().toString());
                member.setPassword(passwordView.getText().toString());
                member.setPasswordRepeat(passwordViewRepeat.getText().toString());

                if (member.isValid()) {
                    Log.e("member", member.getParameter());
                    try {
                        String result = (String) new SignUpCall().execute(member).get();
                        if(result.equals("0")) {
                            Toast.makeText(v.getContext(), "이미 사용중인 아이디입니다.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(v.getContext(), "비밀번호가 일치 하지 않습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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

    private MemberInfo getInfo(MemberInfo member) {

        // 버전정보 빼내기
        // 앱정보 확인후. 강제 업데이트 시키지
        PackageInfo pi = null;
        try {
            pi = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String appVer = pi.versionName;



        //휴대폰 정보 빼내기
        TelephonyManager telephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        String mdn = telephony.getLine1Number();
        String mcc = telephony.getNetworkOperator();

        //구글계정빼오기
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccounts();
        String googleId = "";
        String facebookId = "";
        for (Account account : accounts) {
            if (account.type.equals("com.google")) {        //이러면 구글 계정 구분 가능
                googleId = account.name;
            }
            if (account.type.equals("com.facebook.auth.login")) {        //이러면 구글 계정 구분 가능
                facebookId = account.name;
            }

        }

        member.setMdn(mdn);
        member.setMcc(mcc);
        member.setFacebookId(facebookId);
        member.setGoogleId(googleId);

        return member;
    }

    public String sendSignUp(MemberInfo member) {
        String data = null;
        String line = null;

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(SIGN_UP_URL).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-unlencoded");
            connection.setDoOutput(true);

            OutputStream os = connection.getOutputStream();

            os.write(member.getParameter().getBytes("UTF-8"));
            os.flush();
            os.close();

            InputStream is = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(is), 8 * 1024);
            StringBuilder buff = new StringBuilder();

            while ( ( line = in.readLine() ) != null )
            {
                buff.append(line).append("\n");
            }
            data = buff.toString().trim();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    public class SignUpCall extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            MemberInfo member = (MemberInfo) params[0];
            String s = sendSignUp(member);
            return null;
        }


    }

    class MemberInfo {
        private String id;
        private String password;
        private String passwordRepeat;
        private String mdn;
        private String mcc;
        private String googleId;
        private String facebookId;
        private String gcm;
        private String pushAgree;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getMdn() {
            return mdn;
        }

        public void setMdn(String mdn) {
            this.mdn = mdn;
        }

        public String getMcc() {
            return mcc;
        }

        public void setMcc(String mcc) {
            this.mcc = mcc;
        }

        public String getGoogleId() {
            return googleId;
        }

        public void setGoogleId(String googleId) {
            this.googleId = googleId;
        }

        public String getFacebookId() {
            return facebookId;
        }

        public void setFacebookId(String facebookId) {
            this.facebookId = facebookId;
        }

        public String getGcm() {
            return gcm;
        }

        public void setGcm(String gcm) {
            this.gcm = gcm;
        }

        public String getPushAgree() {
            return pushAgree;
        }

        public void setPushAgree(String pushAgree) {
            this.pushAgree = pushAgree;
        }

        public String getPasswordRepeat() {
            return passwordRepeat;
        }

        public void setPasswordRepeat(String passwordRepeat) {
            this.passwordRepeat = passwordRepeat;
        }

        public boolean isValid() {
            if(password.equals(passwordRepeat)) {
                return true;
            }
            return false;
        }

        public String getParameter() {
            StringBuilder sb = new StringBuilder();
            sb.append("id=").append(getId())
                    .append("&password=").append(getPassword())
                    .append("&mdn=").append(getMdn())
                    .append("&mcc=").append(getMcc())
                    .append("&googleID=").append(getGoogleId())
                    .append("&facebookID=").append(getFacebookId())
                    .append("&gcm=").append(getGcm())
                    .append("&push=").append(getPushAgree());
            return sb.toString();
        }

        @Override
        public String toString() {
            return "MemberInfo{" +
                    "id='" + id + '\'' +
                    ", password='" + password + '\'' +
                    ", passwordRepeat='" + passwordRepeat + '\'' +
                    ", mdn='" + mdn + '\'' +
                    ", mcc='" + mcc + '\'' +
                    ", googleId='" + googleId + '\'' +
                    ", facebookId='" + facebookId + '\'' +
                    ", gcm='" + gcm + '\'' +
                    ", pushAgree='" + pushAgree + '\'' +
                    '}';
        }
    }
}
