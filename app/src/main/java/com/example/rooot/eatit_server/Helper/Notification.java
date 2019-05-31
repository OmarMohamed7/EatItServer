package com.example.rooot.eatit_server.Helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.example.rooot.eatit_server.Model.Request;
import com.example.rooot.eatit_server.OrderCartView;
import com.example.rooot.eatit_server.R;

public class Notification extends ContextWrapper {

    NotificationManager manager;

    private final String CHANNEL_ID = "com.example.rooot.eatit_server.Helper.ADVANCED";
    private final String CHANNEL_NAME = "serverNotification";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification(Context base) {
        super(base);

        setUpChannel();
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setUpChannel(){

        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME ,
                NotificationManager.IMPORTANCE_HIGH);

        channel.setDescription("test");
        channel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PRIVATE);
        channel.setLightColor(Color.GREEN);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if(manager == null)
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        return manager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public android.app.Notification.Builder createNotification(String key , Request request){

        Intent intent = new Intent(getBaseContext() , OrderCartView.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("userPhone" , request.getPhone());

        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext() , 0 , intent , PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        String title = "User : "+request.getName();

        String body = "Has made a new order # "+key;


        return new android.app.Notification.Builder(getBaseContext() , CHANNEL_ID)
                .setStyle(new android.app.Notification.BigTextStyle().bigText(body))
                .setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(defaultSoundUri)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public NotificationCompat.Builder createSimpleNotification(String key , Request request){

        Intent intent = new Intent(getBaseContext() , OrderCartView.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("userPhone" , request.getPhone());

        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext() , 0 , intent , PendingIntent.FLAG_UPDATE_CURRENT);

        String title = "User : "+request.getName();

        String body = "Has made a new order # "+key;

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        return new NotificationCompat.Builder(getBaseContext())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setSound(defaultSoundUri)
                .setContentIntent(contentIntent)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setDefaults(android.app.Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true);
    }
}
