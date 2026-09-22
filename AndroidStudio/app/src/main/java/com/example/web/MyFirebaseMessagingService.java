package com.example.web;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCMService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "FCM 메시지 수신");

        String title = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getTitle() : "알림";
        String message = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getBody() : "메시지 본문이 없습니다.";

        String latitude = remoteMessage.getData().get("latitude");
        String longitude = remoteMessage.getData().get("longitude");

        Log.d(TAG, "Title: " + title + ", Message: " + message);
        sendNotification(title, message, latitude, longitude);
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "FCM 토큰: " + token);
    }

    private void sendNotification(String title, String messageBody, String latitude, String longitude) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("url", "/helpcall");
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String channelId = "emergency_channel";
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.icon_sos)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(0xFFFF0000)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Emergency Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}