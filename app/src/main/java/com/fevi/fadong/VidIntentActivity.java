package com.fevi.fadong;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class VidIntentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vid_intent);

        Intent intent = new Intent(this, IntroActivity.class);
        if(getIntent() != null) {
            Uri uri = getIntent().getData();
            if(uri != null) {
                intent.putExtra("vid", uri.getQueryParameter("vid"));
            }
        }
        startActivity(intent);
    }
}
