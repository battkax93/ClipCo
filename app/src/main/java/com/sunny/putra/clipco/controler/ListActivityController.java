package com.sunny.putra.clipco.controler;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.sunny.putra.clipco.ListClipActivity;
import com.sunny.putra.clipco.MainActivity;
import com.sunny.putra.clipco.R;
import com.sunny.putra.clipco.util.DbHelper;
import com.sunny.putra.clipco.util.LogHelper;


/**
 * Created by Wayan-MECS on 5/24/2018.
 */

public class ListActivityController extends AppCompatActivity {

    public void showDialogDeleteAll(final Context context) {
        final Dialog dialog = new Dialog(context, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_delete_row);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button dialogBtn_cancel = dialog.findViewById(R.id.btn_no);
        Button dialogBtn_okay = dialog.findViewById(R.id.btn_ok);

        dialogBtn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbHelper dbHelper = new DbHelper(context);
                dbHelper.deleteAll();
                dbHelper.close();

                Log.d("LOG_DB", String.valueOf(dbHelper.numberOfRows()));
                LogHelper.print_me("all database has been deleted");

                dialog.dismiss();
                MainControlerActivity mac = new MainControlerActivity();
                mac.infoDialog(context, "all data collection has been deleted");

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

    public void showDialog(final Context context, final String msg, final Long id) {
        final Dialog dialog = new Dialog(context, R.style.PauseDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final TextView text = dialog.findViewById(R.id.txt_file_path);
        text.setText(msg);

        ImageView ivCopy = dialog.findViewById(R.id.ivCopy);
        ImageView ivEdit = dialog.findViewById(R.id.ivEdit);
        ImageView ivShare = dialog.findViewById(R.id.ivShare);
        ImageView ivDelete = dialog.findViewById(R.id.ivDel);

        ivCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogHelper.print_me("=====Copy=======");
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(msg, msg);
                cm.setPrimaryClip(clip);
                dialog.dismiss();
            }
        });

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbHelper dbHelper = new DbHelper(context);
                dbHelper.deleteRow(String.valueOf(id));
                dbHelper.close();
                dialog.dismiss();
            }
        });

        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ListClipActivity activity = (ListClipActivity) context;
                activity.moveToMain(String.valueOf(id), msg);
            }
        });



        ivShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogHelper.print_me("=====Share=======");
                SharedPreferences pref = context.getSharedPreferences("Mypref", 0);
                SharedPreferences.Editor editor2 = pref.edit();
                editor2.putString("share", "1");
                editor2.apply();
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(msg, msg);
                cm.setPrimaryClip(clip);
                dialog.dismiss();

            }
        });

        dialog.show();
    }

    public void moveToMain(String id, String clip) {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("id", id);
        i.putExtra("clip", clip);
        i.putExtra("vc", true);
        i.putExtra("ic",false);
        startActivity(i);
    }

    public void deleteDuplicateandNull (){
        DbHelper dbHelper = new DbHelper(this);
        LogHelper.print_me("getcount row " + String.valueOf(dbHelper.numberOfRows()));
        dbHelper.delDuplicateRow();
        dbHelper.delNullRow();
        LogHelper.print_me("getcount row " + String.valueOf(dbHelper.numberOfRows()));
        dbHelper.close();
    }

}
