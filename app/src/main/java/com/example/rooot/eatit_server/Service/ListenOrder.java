package com.example.rooot.eatit_server.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.example.rooot.eatit_server.Helper.Notification;
import com.example.rooot.eatit_server.Model.Request;
import com.example.rooot.eatit_server.OrderCartView;
import com.example.rooot.eatit_server.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class ListenOrder extends Service implements ChildEventListener{

    DatabaseReference orders;
    Notification notification;
    NotificationManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
        orders = FirebaseDatabase.getInstance().getReference("Requests");

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        orders.addChildEventListener(this);

        return super.onStartCommand(intent, flags, startId);
    }

    public ListenOrder() {
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;

    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        Request request = dataSnapshot.getValue(Request.class);

        if(request.getStatus().equals("0")){

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                notification = new Notification(this);
                android.app.Notification.Builder builder = notification.createNotification(dataSnapshot.getKey() , request);
                notification.getManager().notify(new Random().nextInt(), builder.build());

            }else {




//                NotificationCompat.Builder builder = notification.createSimpleNotification(dataSnapshot.getKey() , request);
//                notification.getManager().notify(new Random().nextInt() , builder.build());
                manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder = createSimpleNotification(dataSnapshot.getKey() , request);
                manager.notify(new Random().nextInt() , builder.build());
            }

        }

//        if(notification != null)
//            orders.removeEventListener(this);

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }

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
