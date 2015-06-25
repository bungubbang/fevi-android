package com.fevi.fadong;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.fevi.fadong.support.AddExperienceCall;
import com.fevi.fadong.support.CircleTransform;
import com.fevi.fadong.support.ContextString;
import com.fevi.fadong.support.WebViewSetting;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.Set;


public class MovieActivity extends Activity {

    private static final String WATCH_MOVIE_IDS = "WATCH_MOVIE_IDS";

    SharedPreferences preferences;
    private String cardId;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(getResources().getString(R.string.loginPref), MODE_PRIVATE);

        setContentView(R.layout.activity_movie);

        Intent intent = getIntent();
        cardId = intent.getStringExtra(ContextString.cardId);
        String cardName = intent.getStringExtra(ContextString.cardName);
        String cardTime = intent.getStringExtra(ContextString.cardTime);
        String cardProfile = intent.getStringExtra(ContextString.cardProfile);
        String cardPicture = intent.getStringExtra(ContextString.cardPicture);
        String cardDescription = intent.getStringExtra(ContextString.cardDescription);
        final String cardSource = intent.getStringExtra(ContextString.cardSource);


        TextView name = (TextView) findViewById(R.id.fa_name);
        name.setText(cardName);
        TextView time = (TextView) findViewById(R.id.fa_time);
        time.setText(cardTime);
        TextView description = (TextView) findViewById(R.id.fa_description);
        description.setMovementMethod(new ScrollingMovementMethod());
        description.setText(cardDescription);

        ImageView profile = (ImageView) findViewById(R.id.fa_profile);
        Picasso.with(this).load(cardProfile).transform(new CircleTransform()).into(profile);

        final VideoView videoView = (VideoView) findViewById(R.id.fa_picture);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        MediaController mc = new MediaController(this);
        mc.setAnchorView(videoView);

        videoView.setVideoURI(Uri.parse(cardSource));
        videoView.setMediaController(mc);

        videoView.setBackgroundColor(Color.BLACK);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressBar.setVisibility(View.GONE);
                videoView.start();
                videoView.setBackgroundColor(Color.TRANSPARENT);
            }
        });

        ImageView fullscreenIcon = (ImageView) findViewById(R.id.fullscreen_icon);
        fullscreenIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 자체 플레이어 호출
                // Intent fullScreenIntent = new Intent(v.getContext(), FullScreenVideoActivity.class);
                // fullScreenIntent.putExtra(ContextString.cardSource, cardSource);
                // v.getContext().startActivity(fullScreenIntent);

                // 미디어 인텐트 호출
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(cardSource), "video/*");
                v.getContext().startActivity(intent);
            }
        });

        ImageView closeButton = (ImageView) findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Set<String> watching = preferences.getStringSet(WATCH_MOVIE_IDS, new HashSet<String>());
        if(!watching.contains(cardId)) {
            new AddExperienceCall(this).execute(cardId);
            SharedPreferences.Editor edit = preferences.edit();
            watching.add(cardId);
            edit.putStringSet(WATCH_MOVIE_IDS, watching);
            edit.apply();
        }

        String memberId = preferences.getString("id", null);

        webView = (WebView) findViewById(R.id.webView);
        WebViewSetting.appin(this, webView);
        webView.loadUrl("http://www.appinkorea.co.kr/fevi/share.php?id=" + memberId + "&vid=" + cardId);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);
        webView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie, menu);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
