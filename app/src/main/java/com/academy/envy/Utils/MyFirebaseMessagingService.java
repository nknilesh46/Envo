package com.academy.envy.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.academy.envy.ChatActivity;
import com.academy.envy.EnvActivity;
import com.academy.envy.R;
import com.academy.envy.SelectEnvActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.net.URI;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> extraData = remoteMessage.getData();

        String message = extraData.get("message");
        String env = extraData.get("env");
        long[] pattern = {1,0,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,0,1,1,1,1,};

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this,"TAC")
                        .setContentTitle(message)
                        .setContentText(env)
                        .setSmallIcon(R.drawable.ic_launcher_background).setCategory("moo").setVibrate(pattern);
        Intent intent;
        if(env.equals("Chat")){
             intent = new Intent(this, ChatActivity.class);;
        }
        else{
            intent = new Intent(this, EnvActivity.class);;
        }
        intent.putExtra("SELECTED_ENV", env);


        PendingIntent pendingIntent = PendingIntent.getActivity(this, 10, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        int id =  (int) System.currentTimeMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("TAC","WhenAppIsOpen",NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(id,notificationBuilder.build());

    }
}
