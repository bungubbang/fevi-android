package com.fevi.fadong;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.fevi.fadong.support.AddExperienceCall;
import com.fevi.fadong.support.CircleTransform;
import com.fevi.fadong.support.ContextString;
import com.squareup.picasso.Picasso;


public class MovieActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.7f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.activity_movie);

        Intent intent = getIntent();
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

        new AddExperienceCall(this).execute();

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
}
