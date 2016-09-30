package com.gae.scaffolder.plugin;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import java.util.Map;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * Created by Felipe Echanique on 08/06/2016.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FCMPlugin";
	Bitmap bitmap;
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        Log.d(TAG, "==> MyFirebaseMessagingService onMessageReceived");

        String title = "";
        String Body = "";
        String image = "";

        if( remoteMessage.getNotification() != null){
            try {
                Log.d(TAG, "\tNotification Title: " + remoteMessage.getNotification().getTitle());
                Log.d(TAG, "\tNotification Message: " + remoteMessage.getNotification().getBody());
                title = remoteMessage.getNotification().getTitle();
                Body = remoteMessage.getNotification().getBody();
                //image = remoteMessage.getNotification().get("image");
            }
            catch (Exception e)
            {

            }
        }

        if(remoteMessage.getData() != null)
        {
            try {
                Log.d(TAG, "\tNotification Data Title: " + remoteMessage.getData().get("title"));
                Log.d(TAG, "\tNotification Data Message: " + remoteMessage.getData().get("body"));
                title = remoteMessage.getData().get("title");
                Body = remoteMessage.getData().get("body");
                image = remoteMessage.getData().get("image");
            }
            catch (Exception e)
            {

            }
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("wasTapped", false);
        //for (String key : remoteMessage.getData().keySet()) {
//                 Object value = remoteMessage.getData().get("Title");
//                 Log.d(TAG, "\tKey: " + key + " Value: " + value);
        data.put("Title", title);
        data.put("Body",Body);
        //}

        Log.d(TAG, "\tNotification Data: " + data.toString());
        FCMPlugin.sendPushPayload( data );
        sendNotification(title,Body,image);
        //sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(), remoteMessage.getData());
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    /*private void sendNotification(String title, String messageBody, Map<String, Object> data) {
        Intent intent = new Intent(this, FCMPluginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    intent.putExtra("Title",title);
	    intent.putExtra("Body",messageBody);
	    Log.e(TAG,"Rahul Title is -> " + intent.getExtras().getString("Title"));
	    Log.e(TAG,"Rahul Body is -> " + intent.getExtras().getString("Body"));
		for (String key : data.keySet()) {
			intent.putExtra(key, data.get(key).toString());
		}
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getApplicationInfo().icon)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 , notificationBuilder.build());
    }*/
	
	private void sendNotification(String title,String Body,String img) {

        Intent intent = new Intent(this, FCMPluginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String imageUri = img;

        //To get a Bitmap image from the URL received
        bitmap = getBitmapfromUrl(imageUri);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(getApplicationInfo().icon)
                .setLargeIcon(bitmap)
                .setContentTitle(title)
                .setContentText(Body)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap))/*Notification with Image*/
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
	
	public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
}
