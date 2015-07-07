package com.fevi.fadong;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


public class VidIntentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vid_intent);

        Intent intent = new Intent(this, IntroActivity.class);
        if(getIntent() != null) {
            Uri uri = getIntent().getData();
            if(uri != null) {

                App application = (App) getApplication();
                Tracker tracker = application.getDefaultTracker();

                tracker.setScreenName("VidIntent Activity");
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("vidIntent")
                        .setAction("show")
                        .setLabel(uri.getQueryParameter("vid"))
                        .build());

                intent.putExtra("vid", uri.getQueryParameter("vid"));
            }
        }
        startActivity(intent);
    }
}
