package com.fevi.fadong;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import com.fevi.fadong.support.AddExperienceCall;
import com.fevi.fadong.support.CircleTransform;
import com.fevi.fadong.support.ContextString;
import com.fevi.fadong.support.WebViewSetting;
import com.fevi.fadong.support.db.WatchVidService;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.Set;


public class MovieActivity extends Activity {

    private static final String WATCH_MOVIE_IDS = "WATCH_MOVIE_IDS";

    SharedPreferences preferences;
    private String cardId;

    private WebView webView;
    private Handler handler;

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

        final String memberId = preferences.getString("id", null);

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                WatchVidService watchVidService = WatchVidService.getInstance(getApplicationContext());

                if (!watchVidService.exist(memberId, cardId)) {
                    new AddExperienceCall(getApplicationContext()).execute(cardId);
                    watchVidService.insert(memberId, cardId);
                }
            }
        }, 5000);



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

        App application = (App) getApplication();
        Tracker tracker = application.getDefaultTracker();

        tracker.setScreenName("Movie Activity");
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("MOVIE")
                .setAction("show")
                .setLabel(cardId)
                .build());

        RelativeLayout review = (RelativeLayout) findViewById(R.id.review);
        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
            }
        });

        ScrollView scrollView = (ScrollView) findViewById(R.id.review_text);
        scrollView.requestDisallowInterceptTouchEvent(true);

        TextView belowText = (TextView) findViewById(R.id.below_text);
        belowText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((View) view.getParent().getParent()).setVisibility(View.GONE);
            }
        });

        RelativeLayout reviewButton = (RelativeLayout) findViewById(R.id.review_button);
        reviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent market = new Intent(Intent.ACTION_VIEW);
                market.setData(Uri.parse("market://details?id=com.fevi.fadong"));
                startActivity(market);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        handler.removeMessages(0);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        handler.removeMessages(0);
        super.onDestroy();
    }
}
