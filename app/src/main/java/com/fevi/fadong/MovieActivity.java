package com.fevi.fadong;

import android.content.Intent;
import android.media.session.MediaSession;
import android.net.Uri;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.fevi.fadong.support.CircleTransform;
import com.fevi.fadong.support.ContextString;
import com.squareup.picasso.Picasso;


public class MovieActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        Intent intent = getIntent();
        String cardName = intent.getStringExtra(ContextString.cardName);
        String cardTime = intent.getStringExtra(ContextString.cardTime);
        String cardProfile = intent.getStringExtra(ContextString.cardProfile);
        String cardPicture = intent.getStringExtra(ContextString.cardPicture);
        String cardDescription = intent.getStringExtra(ContextString.cardDescription);
        String cardSource = intent.getStringExtra(ContextString.cardSource);


        TextView name = (TextView) findViewById(R.id.fa_name);
        name.setText(cardName);
        TextView time = (TextView) findViewById(R.id.fa_time);
        time.setText(cardTime);
        TextView description = (TextView) findViewById(R.id.fa_description);
        description.setText(cardDescription);

        ImageView profile = (ImageView) findViewById(R.id.fa_profile);
        Picasso.with(this).load(cardProfile).transform(new CircleTransform()).into(profile);
        VideoView picture = (VideoView) findViewById(R.id.fa_picture);
        picture.setVideoURI(Uri.parse(cardSource));
        MediaController mc = new MediaController(this);
        mc.setAnchorView(picture);
        picture.setMediaController(mc);
        picture.start();

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
