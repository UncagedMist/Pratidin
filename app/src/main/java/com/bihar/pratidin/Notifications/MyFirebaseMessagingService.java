package com.bihar.pratidin.Notifications;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bihar.pratidin.Activity.HomeActivity;
import com.bihar.pratidin.Common.Common;
import com.bihar.pratidin.R;
import com.bihar.pratidin.Remote.IMyAPI;
import com.bihar.pratidin.SplashActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    IMyAPI myAPI = Common.getAPI();

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        createNotification(
                message.getData().get("title"),
                message.getData().get("message"),
                message.getData().get("image")
        );


    }

    private void createNotification(String notificationTitle, String notificationContent, String imageUrl){
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Let's create a notification builder object
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,
                getResources().getString(R.string.notification_channel_id));
        // Create a notificationManager object
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        // If android version is greater than 8.0 then create notification channel
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            // Create a notification channel
            NotificationChannel notificationChannel = new NotificationChannel(
                    getResources().getString(R.string.notification_channel_id),
                    getResources().getString(R.string.channel_name),
                    NotificationManager.IMPORTANCE_HIGH
            );
            // Set properties to notification channel
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);

            // Pass the notificationChannel object to notificationManager
            notificationManager.createNotificationChannel(notificationChannel);
        }
        builder.setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setOnlyAlertOnce(true);

        if (imageUrl != null) {
            Bitmap bitmap = getBitmapFromUrl(imageUrl);
            builder.setStyle(
                    new NotificationCompat.BigPictureStyle()
                            .bigPicture(bitmap)
                            .bigLargeIcon(null)
            ).setLargeIcon(bitmap);
        }

        notificationManager.notify(1, builder.build());
    }

    public Bitmap getBitmapFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        }
        catch (Exception e) {
            Log.e("awesome", "Error in getting notification image: " + e.getLocalizedMessage());
            return null;
        }
    }




    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);

        Log.d("token", token);

        registerToken(token);
    }

    @SuppressLint("HardwareIds")
    private void registerToken(String token) {
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        myAPI.insertToken(deviceId,token)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                    }
                });
    }
}
