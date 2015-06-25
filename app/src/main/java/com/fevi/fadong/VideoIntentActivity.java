package com.fevi.fadong;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;


public class VideoIntentActivity extends ActionBarActivity {

    private String vid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_intent);

        Intent intent = new Intent(this, IntroActivity.class);

        Uri uri = getIntent().getData();
        if(uri != null) {
            vid = uri.getQueryParameter("vid");
            Log.i("fadong", "start activity video intent : " + vid);
            intent.putExtra("vid", vid);
        }

        this.startActivity(intent);
    }
}
