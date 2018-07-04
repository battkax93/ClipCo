package com.sunny.putra.clipco.controler;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sunny.putra.clipco.BuildConfig;
import com.sunny.putra.clipco.ListClipActivity;
import com.sunny.putra.clipco.MainActivity;
import com.sunny.putra.clipco.Notification.NotificationHelper;
import com.sunny.putra.clipco.R;
import com.sunny.putra.clipco.util.DbHelper;
import com.sunny.putra.clipco.util.Globals;
import com.sunny.putra.clipco.util.LogHelper;

import net.glxn.qrgen.android.QRCode;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by EKO on 5/21/2018.
 */


public class MainControlerActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private FirebaseAnalytics mFirebaseAnalytics;

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    Context mContext;
    String fname;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();
    }

    public void NotificationBuilder() {
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_QR/" + fname);
        intent.setDataAndType(Uri.fromFile(myDir), "image/*");

        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_clipco);
        notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_download)
                .setLargeIcon(logo)
                .setContentTitle("ClipCo")
                .setContentText("Downloading File")
                .setContentIntent(pIntent)
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());

    }

    public void NotificationBuilder2(String s) {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);


        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_clipco);
        notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(logo)
                .setOngoing(true)
                .setContentTitle("ClipCo")
                .setContentText(s)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);
        notificationManager.notify(0, notificationBuilder.build());
    }


    public void notifClose() {
        LogHelper.print_me("===NotifClose===");
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancel(0);
    }

    public void ToastHelper(String msg) {
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * dialog
     **/

    public void infoDialog(final Context context, final String msg) {
        final Dialog dialog = new Dialog(context, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.global_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView text = dialog.findViewById(R.id.tvGlobaldialog);
        text.setText(msg);

        Button btnClose = dialog.findViewById(R.id.btnClose);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showDialogDeleteRow(final Context context, final String id) {
        final Dialog dialog = new Dialog(context, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setTitle("Are you sure want to delete this collection ?");
        dialog.setContentView(R.layout.custom_dialog_delete_row);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button dialogBtn_cancel = dialog.findViewById(R.id.btn_no);
        Button dialogBtn_okay = dialog.findViewById(R.id.btn_ok);

        dialogBtn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbHelper dbHelper = new DbHelper(context);
                dbHelper.deleteRow(id);
                dbHelper.close();

                Log.d("LOG_DB", String.valueOf(dbHelper.numberOfRows()));
                LogHelper.print_me("this row has been deleted");

                dialog.dismiss();
                ToastHelper("deleted");
                goToActivity(ListClipActivity.class);
            }
        });

        dialogBtn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void showDialogQRCode(final Context context, final String msg) {
        LogHelper.print_me("checking msg = " + msg);

        final Dialog dialog = new Dialog(context, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog_qr);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        ImageView ivQR = dialog.findViewById(R.id.ivQRCode);
        Button btnSave = dialog.findViewById(R.id.btn_save_qr);
        Button btnShare = dialog.findViewById(R.id.btn_share_qr);

        int bgColor = 0xffffffff;//0x00ffffff;
        int foreColor = 0xff000000;//0xFFFFFFFF;
        final Bitmap bmp = QRCode.from(msg)
                .withSize(160, 160)   //setting color qrcode
                .withColor(foreColor, bgColor)
                .bitmap();
        ivQR.setImageBitmap(bmp);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (Build.VERSION.SDK_INT >= 26) {
                    saveImge3(bmp);
                } else {
                    SaveImageQrCode(bmp);
                }
                analyticFBLog(Globals.BUTTON_ID_D_SAVEQR, Globals.DIALOG_BUTTON_TYPE, Globals.BUTTON_NAME_D_SAVEQR);
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareIntentBitmap(bmp);
                analyticFBLog(Globals.BUTTON_ID_D_SHARE, Globals.DIALOG_BUTTON_TYPE, Globals.BUTTON_NAME_D_SHARE);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * tools
     **/

    public void saveImge3(Bitmap bmp) {
        LogHelper.print_me("==SDK>26==");
        LogHelper.print_me("==svimg3==");

        String n = String.valueOf(System.currentTimeMillis());
        fname = "Image-" + n + ".jpg";
        String contentName = "Downloading QR CODE";
        String root = Environment.getExternalStorageDirectory().toString();

        File docFile = new File(root + "/saved_QRcode");
        docFile.mkdirs();
        File file = new File(docFile, fname);
        if (file.exists())
            file.delete();
        try {
            if (Build.VERSION.SDK_INT >= 26) {
                NotificationHelper notificationHelper = new NotificationHelper(this);
                Notification.Builder nb = notificationHelper.getAndroidChannelNotification(getString(R.string.clipco), contentName, fname);
                notificationHelper.getManager().notify(1, nb.build());

                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();

                nb.setProgress(100, 0, false);
                nb = notificationHelper.getAndroidChannelNotification(getString(R.string.clipco), "QR CODE Downloaded", fname);
                notificationHelper.getManager().notify(1, nb.build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Uri pdfURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID.replace(".dev","") + ".provider", docFile);
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
    }

    private void SaveImageQrCode(Bitmap bmp) {
        LogHelper.print_me("==SDK<26==");
        LogHelper.print_me("===saveimg===");

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_QR");
        myDir.mkdirs();
        String n = String.valueOf(System.currentTimeMillis());
        fname = "Image-" + n + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists())
            file.delete();
        try {
            NotificationBuilder();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            LogHelper.print_me("===saveimagesuccess===");
            ToastHelper("QRCODE has been saved");

            notificationBuilder.setProgress(0, 0, false);
            notificationBuilder.setContentText("QR CODE Downloaded");
            notificationManager.notify(0, notificationBuilder.build());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public String pasteHtmltoText() {

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard.hasPrimaryClip()) {
            android.content.ClipDescription description = clipboard.getPrimaryClipDescription();
            android.content.ClipData data = clipboard.getPrimaryClip();
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            LogHelper.print_me("cek dt = " + item.getText().toString());
            if (data != null && description != null && description.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML))
                return String.valueOf(item.getText());
        }
        return null;
    }

    public String readFromTextPlain() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard.hasPrimaryClip()) {
            android.content.ClipDescription description = clipboard.getPrimaryClipDescription();
            android.content.ClipData data = clipboard.getPrimaryClip();
            LogHelper.print_me("cek data = " + data.toString());
            if (data != null && description != null && description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
                return String.valueOf(data.getItemAt(0).getText());
        }
        return null;
    }

    public void shareIntentBitmap(Bitmap bmp) {
        LogHelper.print_me("===shareintentbitmap===");

        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), bmp, "title", null);
        Uri bitmapUri = Uri.parse(bitmapPath);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
        startActivity(Intent.createChooser(intent, "Share"));

        /*Uri bmpUri;
        String pathofBmp = MediaStore.Images.Media.insertImage(getContentResolver(), bmp, "title", null);
        if (pathofBmp != null) {
            bmpUri = Uri.parse(pathofBmp);
            final Intent emailIntent1 = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            emailIntent1.putExtra(Intent.EXTRA_STREAM, bmpUri);
            emailIntent1.setType("image/png");
        }*/
    }

    public void shareIntentString(String s) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "From ClipCo");
        sendIntent.putExtra(Intent.EXTRA_TEXT, s);
        sendIntent.setType("text/plain");
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(sendIntent, "share via"));
    }

    public void shareIntentString2(String s) {
        LogHelper.print_me("=====Share2=======");
        ClipboardManager cm = (ClipboardManager) getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(s, s);
        cm.setPrimaryClip(clip);
    }

    public String setDate() {
        Locale locale = new Locale("en", "US");
        SimpleDateFormat df = new SimpleDateFormat("EEEE, dd MMMM yyyy", locale);
        Date dt = new Date();
        return df.format(dt);
    }

    public void copyToClipBoard(String msg) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(msg, msg);
        clipboard.setPrimaryClip(clip);
    }

    public void analyticFBLog(String id, String name, String type) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        LogHelper.print_me("send analytic" + type);
    }

    /**
     * SQLite
     **/

    public void insertValuetoSqlite(String tgl_notif, String text) {
        LogHelper.print_me("ready for insert " + tgl_notif + " and " + text);
        DbHelper dbhelper = new DbHelper(this);
        dbhelper.insertValue(tgl_notif, text);
        dbhelper.close();
    }

    public void updateRow(String id, String clip) {
        LogHelper.print_me("get keys = " + id + " | " + clip);
        DbHelper dbHelper = new DbHelper(this);
        dbHelper.updateRow(id, clip);
        dbHelper.close();
    }

    public void checkNumberofRowDB() {
        DbHelper dbhelper = new DbHelper(getApplicationContext());
        Log.d("LOG_DB", String.valueOf(dbhelper.numberOfRows()));
        dbhelper.close();
    }


    /**
     * move
     **/

    public void goToActivity(Class<?> cls) {
        startAct(cls, false, null);
    }

    public void startAct(Class<?> cls, boolean finish, Bundle bundle) {
        Intent intent = new Intent(getApplicationContext(), cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (bundle != null)
            intent.putExtras(bundle);
        startActivity(intent);
        if (finish)
            finish();
    }

    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
    }

}
