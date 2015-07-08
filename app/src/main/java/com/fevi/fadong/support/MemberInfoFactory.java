package com.fevi.fadong.support;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import com.fevi.fadong.domain.Member;

/**
 * Created by 1000742 on 15. 1. 28..
 */
public class MemberInfoFactory {

    private Member member;
    private Context context;

    public MemberInfoFactory(Member member, Context context) {
        this.member = member;
        this.context = context;
    }

    public Member getInfo() {

        // 버전정보 빼내기
        // 앱정보 확인후. 강제 업데이트 시키지
        PackageInfo pi = null;
        try {
            pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String appVer = pi.versionName;


        SharedPreferences pref = context.getSharedPreferences("pref", Context.MODE_PRIVATE);

        String new_reg = pref.getString("REG_ID","");

        //휴대폰 정보 빼내기
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String mdn = telephony.getLine1Number();
        String mcc = telephony.getNetworkOperator();

        //구글계정빼오기
        AccountManager manager = AccountManager.get(context);
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

        member.setMdn(mdn == null? "": mdn);
        member.setMcc(mcc == null? "": mcc);
        member.setFacebookId(facebookId);
        member.setGoogleId(googleId);
        member.setGcm(new_reg);

        return member;
    }
}
