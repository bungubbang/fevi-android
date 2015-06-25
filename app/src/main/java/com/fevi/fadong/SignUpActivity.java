package com.fevi.fadong;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fevi.fadong.domain.Member;
import com.fevi.fadong.support.FadongHttpClient;
import com.fevi.fadong.support.MemberInfoFactory;

import java.util.concurrent.ExecutionException;


public class SignUpActivity extends Activity {

    private static final String SIGN_UP_URL = "http://www.appinkorea.co.kr/fevi/join.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final Member member = new Member();

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

        new MemberInfoFactory(member, this).getInfo();

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
                        if(result.equals("code=0")) {
                            Toast.makeText(v.getContext(), "이미 사용중인 아이디입니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(v.getContext(), "회원가입이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                            Intent loginIntent = new Intent(v.getContext(), LoginActivity.class);
                            v.getContext().startActivity(loginIntent);
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

    public class SignUpCall extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            Member member = (Member) params[0];
            return new FadongHttpClient().sendLogin(SIGN_UP_URL, member.getParameter());
        }


    }

}
