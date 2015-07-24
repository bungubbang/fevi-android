package com.fevi.fadong;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

    public static final String TAG = "mundong";
    public static final int NOTIFICATION_ID = 1;
    private static NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    
	public GCMIntentService() {
        super("34043858841");
    }
 
    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        SharedPreferences pref = getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("REG_ID", registrationId);
        editor.apply();
    }
 
    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
    }
 
    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        String message = intent.getExtras().getString("message");
        String bigicon = intent.getExtras().getString("bigicon");
        String title = intent.getExtras().getString("title");
        String url = intent.getExtras().getString("url");
        Log.i(TAG, "Received message : " + message);
        // notifies user
        generateNotification(context, message, title, url, bigicon);
    }
 
    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        // notifies user
    }
 
    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
    }
 
    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);

        return super.onRecoverableError(context, errorId);
    }
 
    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private void generateNotification(Context context, String message, String title, String target_url, String bigicon) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent;
       if(target_url == null || target_url.isEmpty()){
    	   contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, IntroActivity.class), 0);

       }else{
    	   contentIntent = PendingIntent.getActivity(this, 0, new Intent(Intent.ACTION_VIEW, Uri.parse(target_url)), 0);
       }
       
       Bitmap mainIcon;
       if(bigicon == null || bigicon.isEmpty()){
    	   mainIcon = BitmapFactory.decodeResource(getResources(),
                   R.drawable.ic_launcher);
       }else{
    	   mainIcon=getImageFromURL(bigicon);
       }
       
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setLargeIcon(mainIcon)
        .setSmallIcon(R.drawable.trans)
        .setContentTitle(title)
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(message))
        .setContentText(message);
                
        
        //Vibration
        mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        //Ton
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        //LED
        mBuilder.setLights(Color.YELLOW, 3000, 3000);

        //�좎떬諭꾩삕 �좎룞�쇿뜝�숈삕�좑옙
        mBuilder.setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build()); 
   
 
    }
    public static Bitmap getImageFromURL(String imageURL){
        Bitmap imgBitmap = null;
        HttpURLConnection conn = null;
        BufferedInputStream bis = null;
        
        try
        {
            URL url = new URL(imageURL);
            conn = (HttpURLConnection)url.openConnection();
            conn.connect();
            
            int nSize = conn.getContentLength();
            bis = new BufferedInputStream(conn.getInputStream(), nSize);
            imgBitmap = BitmapFactory.decodeStream(bis);
        }
        catch (Exception e){
            e.printStackTrace();
        } finally{
            if(bis != null) {
                try {bis.close();} catch (IOException e) {}
            }
            if(conn != null ) {
                conn.disconnect();
            }
        }
        
        return imgBitmap;
    }    

}
