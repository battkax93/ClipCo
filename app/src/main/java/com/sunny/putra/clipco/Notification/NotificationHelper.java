package com.sunny.putra.clipco.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;

import com.sunny.putra.clipco.BuildConfig;
import com.sunny.putra.clipco.MainActivity;
import com.sunny.putra.clipco.R;

import java.io.File;
import java.util.List;

/**
 * Created by Wayan-MECS on 7/4/2018.
 */

public class NotificationHelper extends ContextWrapper {
    private NotificationManager mManager;
    public static final String ANDROID_CHANNEL_ID = "id.co.adira.ad1mobileakses.ANDROID";
    public static final String ANDROID_CHANNEL_NAME = "ANDROID CHANNEL";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationHelper(Context base) {
        super(base);
        createChannels();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createChannels() {
        //create notification channel
        NotificationChannel androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID, ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        // Sets whether notifications posted to this channel should display notification lights
        androidChannel.enableLights(true);
        // Sets whether notification posted to this channel should vibrate.
        androidChannel.enableVibration(true);
        // Sets the notification light color for notifications posted to this channel
        androidChannel.setLightColor(Color.YELLOW);
        // Sets whether notifications posted to this channel appear on the lockscreen or not
        androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(androidChannel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getAndroidChannelNotification(String title, String body, String nm) {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        String root = Environment.getExternalStorageDirectory().toString();

        File myDir = new File(root + "/saved_QRcode/" + nm);
        Uri pdfURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID.replace(".dev", "") + ".provider", myDir);
        Intent target = new Intent(Intent.ACTION_VIEW);
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        target.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        target.setDataAndType(pdfURI, "image/*");
        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        List<ResolveInfo> infos = this.getPackageManager().queryIntentActivities(target, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : infos) {
            String packageName = resolveInfo.activityInfo.packageName;
            this.grantUriPermission(packageName, pdfURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        intent.setDataAndType(pdfURI, "image/*");
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_clipco);

        return new Notification.Builder(getApplicationContext(), ANDROID_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_convert_qr)
                .setLargeIcon(logo)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pIntent)
                .setAutoCancel(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getAndroidChannelNotification2(String title, String body) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_clipco);

        return new Notification.Builder(getApplicationContext(), ANDROID_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_active)
                .setLargeIcon(logo)
                .setOngoing(true)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);
    }
}