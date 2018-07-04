package com.sunny.putra.clipco;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SwitchCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.sunny.putra.clipco.Notification.KillNotificationsService;
import com.sunny.putra.clipco.Notification.NotificationHelper;
import com.sunny.putra.clipco.controler.MainControlerActivity;
import com.sunny.putra.clipco.receiver.NetworkStateChangeReceiver;
import com.sunny.putra.clipco.util.Globals;
import com.sunny.putra.clipco.util.LogHelper;

import static com.sunny.putra.clipco.receiver.NetworkStateChangeReceiver.IS_NETWORK_AVAILABLE;

public class MainActivity extends MainControlerActivity {

    Button btnTest, btnDelete, btnDelete2, btnSave, btnShare, btnClip, btnCopy, btnUpdate, btnConvert, btnConvert2, btnScan;
    Boolean viewClip = false;
    Boolean isChecked2 = false;
    TextView tvTime;
    EditText etTest;
    String text, tgl_notif, id, test, msg;
    android.support.v7.widget.Toolbar toolbar;
    TextView tbTittle;
    SwitchCompat switchAB;

    public String KeyToggle = "isChecked";

    private NotificationHelper notificationHelper;

    private int maxCount = 4;
    private String KEY_COUNT = "aCount";
    private InterstitialAd mInterstitialAd;                              //adMob
    AdView mAdview;                                                      //adMob

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("ClipCo");

        toolbar = findViewById(R.id.tool_bar);
        btnCopy = findViewById(R.id.btnCopy);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        btnDelete2 = findViewById(R.id.btnDel2);
        btnClip = findViewById(R.id.btnListClip);
        btnShare = findViewById(R.id.btnShare);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnConvert = findViewById(R.id.btnConvertToQR);
        btnConvert2 = findViewById(R.id.btnConvertToQR2);
        btnScan = findViewById(R.id.btnScanToQR);
        etTest = findViewById(R.id.etTest);
        tvTime = findViewById(R.id.tvTime);
        tbTittle = findViewById(R.id.toolbar_title);
        switchAB = findViewById(R.id.switch_save);
        mAdview = findViewById(R.id.adView_main);                               //adMob

        MobileAds.initialize(this, Globals.ADD_MOB_APP_ID);             //adMob
        mInterstitialAd = new InterstitialAd(this);                     //adMob
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_video));    //adMob
        adMobs();

        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = pref.edit();

        if (Build.VERSION.SDK_INT >= 26) {
            notificationHelper = new NotificationHelper(this);
        }

        tgl_notif = setDate();
        tvTime.setText(tgl_notif);

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickmenu(0);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickmenu(1);
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickmenu(2);
            }
        });
        btnDelete2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickmenu(2);
            }
        });
        btnClip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickmenu(3);
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickmenu(4);
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickmenu(5);
            }
        });
        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionSave();
            }
        });
        btnConvert2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionSave();
            }
        });
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionCamera2();
            }
        });
        switchAB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    isChecked2 = false;
                    notifClose();
                    editor.putBoolean("isChecked", false);
                    editor.apply();
                    LogHelper.print_me("cek = " + pref.getBoolean("isChecked", false));
                    tbTittle.setText(R.string.manual_mode);
                    analyticFBLog(Globals.TOGGLE_ID_UNACTIVE, Globals.TOGGLE_TYPE, Globals.TOGGLE_NAME_UNACTIVE);
                } else {
                    isChecked2 = true;

                    ServiceConnection mConnection = new ServiceConnection() {
                        public void onServiceConnected(ComponentName className, IBinder binder) {
                            ((KillNotificationsService.KillBinder) binder).service.startService(new Intent(
                                    MainActivity.this, MainActivity.class));
                            LogHelper.print_me("==onServON==");

                            if (Build.VERSION.SDK_INT >= 26) {
                                Notification.Builder nb = notificationHelper.getAndroidChannelNotification2("ClipCo", getString(R.string.auto_save_mode));
                                notificationHelper.getManager().notify(0, nb.build());
                            } else {
                                NotificationBuilder2(getString(R.string.auto_save_mode));
                            }
                        }

                        public void onServiceDisconnected(ComponentName className) {
                            LogHelper.print_me("==onServDC==");
                        }
                    };
                    bindService(new Intent(MainActivity.this,
                                    KillNotificationsService.class), mConnection,
                            Context.BIND_AUTO_CREATE);

                    editor.putBoolean("isChecked", true);
                    editor.apply();
                    LogHelper.print_me("cek = " + pref.getBoolean("isChecked", false));
                    tbTittle.setText(R.string.auto_save_mode);
                    analyticFBLog(Globals.TOGGLE_ID_ACTIVE, Globals.TOGGLE_TYPE, Globals.TOGGLE_NAME_ACTIVE);
                }
            }
        });

        msg = etTest.getText().toString();

        getIntentData();
        prepareMenu();
        clipboardListener();

    }

    public void getIntentData() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        viewClip = intent.getBooleanExtra("vc", false);
        String clip = intent.getStringExtra("clip");
        LogHelper.print_me("cek intent List Clip = " + id + " | " + clip + " | " + viewClip);
        if (id != null && clip != null && viewClip) {
            mAdview.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.GONE);
            etTest.setText(clip);
            btnSave.setVisibility(View.GONE);
            btnUpdate.setVisibility(View.VISIBLE);
            btnConvert.setVisibility(View.GONE);
            btnScan.setVisibility(View.GONE);
            btnConvert2.setVisibility(View.VISIBLE);
            btnClip.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
            btnDelete2.setVisibility(View.VISIBLE);
            analyticFBLog(Globals.ADAPTER_ID, Globals.ADAPTER_TYPE, Globals.ADAPTER_NAME);
        }
    }

    public void prepareMenu() {
        isChecked2 = pref.getBoolean("isChecked", false);
        if (isChecked2) {
            switchAB.setChecked(true);
            tbTittle.setText(getString(R.string.auto_save_mode));
        } else {
            switchAB.setChecked(false);
            tbTittle.setText(getString(R.string.manual_mode));
        }
    }

    public void actCount() {
        int aCount = getFromSP(KEY_COUNT);
        LogHelper.print_me("== count " + aCount);
        aCount++;
        int bCount = aCount;

        if (bCount >= maxCount) {
            LogHelper.print_me("==showingAds==");
            mInterstitialAd.show();
        }

        insertToSP(KEY_COUNT, bCount);
        LogHelper.print_me("== count " + getFromSP(KEY_COUNT));
    }

    public void insertToSP(String key, int count) {
        editor.putInt(key, count);
        editor.apply();
    }

    public int getFromSP(String key) {
        return pref.getInt(key, 0);
    }

    public void removeKeySP(String key) {
        editor.remove(key);
        editor.apply();
        LogHelper.print_me("== count " + getFromSP(key));
    }

    public void adMobs() {
        if (Globals.admob) {
            LogHelper.print_me("== dev adMobs ==");
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("6B7C8118873F959A250EF2732E708691")
                    .build();

            mInterstitialAd.loadAd(adRequest);
            mAdview.loadAd(adRequest);

            mAdview.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                }

                @Override
                public void onAdClosed() {
                    Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdLeftApplication() {
                    Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }
            });

            mInterstitialAd.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    showInterstitial();
                }
            });

        } else {
            LogHelper.print_me("== prod adMobs ==");
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdview.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                }

                @Override
                public void onAdClosed() {
                    Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdLeftApplication() {
                    Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                }
            });
            mAdview.loadAd(adRequest);
        }
    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
//            mInterstitialAd.show();
            actCount();
        }
    }

    public void clickmenu(int position) {
        LogHelper.print_me("received keys = " + position);
        switch (position) {
            case 0:
                if (etTest.getText().toString().isEmpty()) {
                    ToastHelper("Empty Text");
                } else {
                    String msg = etTest.getText().toString();
                    copyToClipBoard(msg);
                    goToActivity(ListClipActivity.class);
                    analyticFBLog(Globals.BUTTON_ID_COPY, Globals.BUTTON_TYPE, Globals.BUTTON_NAME_COPY);
                }
                break;
            case 1:
                if (etTest.getText().toString().isEmpty()) {
                    ToastHelper("Empty Text");
                } else {
                    String time = tvTime.getText().toString();
                    String text = etTest.getText().toString();
                    LogHelper.print_me("inserting " + tgl_notif + " and " + text + " to sqLite");
                    insertValuetoSqlite(time, text);
                    LogHelper.print_me("succes inserting value to sqLite");
                    checkNumberofRowDB();
                    etTest.setText(null);
                    ToastHelper("Clipboard Saved");
                    analyticFBLog(Globals.BUTTON_ID_SAVE, Globals.BUTTON_TYPE, Globals.BUTTON_NAME_SAVE);
                }
                break;
            case 2:
                if (etTest.getText().toString().isEmpty()) {
                    ToastHelper("Empty Text");
                } else {
                    showDialogDeleteRow(this, id);
                    analyticFBLog(Globals.BUTTON_ID_DELETE, Globals.BUTTON_TYPE, Globals.BUTTON_NAME_DELETE);
                }
                break;
            case 3:
                goToActivity(ListClipActivity.class);
                analyticFBLog(Globals.BUTTON_ID_MYCOLLECT, Globals.BUTTON_TYPE, Globals.BUTTON_NAME_MYCOLLECT);
                break;
            case 4:
                if (etTest.getText().toString().isEmpty()) {
                    ToastHelper("empty text");
                } else {
                    shareIntentString(etTest.getText().toString());
                    analyticFBLog(Globals.BUTTON_ID_SHARE, Globals.BUTTON_TYPE, Globals.BUTTON_NAME_SHARE);
                }
                break;
            case 5:
                if (etTest.getText().toString().isEmpty()) {
                    ToastHelper("Empty Text");
                } else {
                    updateRow(id, etTest.getText().toString());
                    goToActivity(ListClipActivity.class);
                    analyticFBLog(Globals.BUTTON_ID_UPDATE, Globals.BUTTON_TYPE, Globals.BUTTON_NAME_UPDATE);
                }
                break;
            case 6:
                if (etTest.getText().toString().isEmpty()) {
                    ToastHelper("Empty text");
                } else {
                    showDialogQRCode(this, etTest.getText().toString());
                    analyticFBLog(Globals.BUTTON_ID_CONVERT, Globals.BUTTON_TYPE, Globals.BUTTON_NAME_CONVERT);
                }
                break;
            case 7:
                goToActivity(QrReaderActivity.class);
                analyticFBLog(Globals.BUTTON_ID_SCAN, Globals.BUTTON_TYPE, Globals.BUTTON_NAME_SCAN);
                break;
        }
    }

    public void checkPermissionSave() {
        LogHelper.print_me("====reqPermissionWriteExt====");
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 2);
        } else {
            clickmenu(6);
        }
    }

    public void checkPermissionCamera2() {
        LogHelper.print_me("====reqPermissionCamera====");
        String permission = Manifest.permission.CAMERA;
        int grant = ContextCompat.checkSelfPermission(this, permission);
        if (grant != PackageManager.PERMISSION_GRANTED) {
            String[] permission_list = new String[1];
            permission_list[0] = permission;
            ActivityCompat.requestPermissions(this, permission_list, 1);
        } else {
            clickmenu(7);
        }
    }

    protected void clipboardListener() {
        final SharedPreferences pref = this.getSharedPreferences("Mypref", 0);
        final SharedPreferences.Editor editor2 = pref.edit();
        final ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {

            @Override
            public void onPrimaryClipChanged() {

                if (clipboard.hasPrimaryClip()) {
                    android.content.ClipDescription description = clipboard.getPrimaryClipDescription();
                    android.content.ClipData data = clipboard.getPrimaryClip();
                    String cp = data.toString();
                    LogHelper.print_me("cp ..." + cp);
                    if (data != null && description != null && description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {

                        test = readFromTextPlain();
                        etTest.setText(test);
                        String intent = pref.getString("share", null);
                        LogHelper.print_me("=== " + intent);
                        if (intent != null && intent == "1") {
                            shareIntentString(test);
                            editor2.remove("share");
                            editor2.apply();
                        } else {
                            ToastHelper("copied to clipboard");
                            autoSave();
                        }
                    } else if (data != null && description != null && description.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML)) {

                        test = pasteHtmltoText();
                        etTest.setText(test);
                        String intent = pref.getString("share", null);
                        if (intent != null && intent == "1") {
                            shareIntentString(test);
                            editor2.remove("share");
                            editor2.apply();
                        } else {
                            ToastHelper("copied to clipboard");
                            autoSave();
                        }
                    }
                }
            }
        });
    }

    private void autoSave() {
        if (isChecked2) {
            LogHelper.print_me("cek = " + isChecked2);
            LogHelper.print_me("==autosave==");
            insertValuetoSqlite(tgl_notif, test);
        }
    }

    public void networkListener() {
        IntentFilter intentFilter = new IntentFilter(NetworkStateChangeReceiver.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isNetworkAvailable = intent.getBooleanExtra(IS_NETWORK_AVAILABLE, false);
                String networkStatus = isNetworkAvailable ? "connected" : "disconnected";

                Snackbar.make(findViewById(R.id.activity_main), "Network Status: " + networkStatus, Snackbar.LENGTH_LONG).show();
                if (!isNetworkAvailable) {
                    btnTest.setClickable(false);
                    btnTest.setText(networkStatus);
                } else {
                    btnTest.setClickable(true);
                    btnTest.setText(networkStatus);
                }
            }
        }, intentFilter);
    }

    @Override
    protected void onDestroy() {
        notifClose();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            LogHelper.print_me("===onKeyDevicePressed===");
            if (getFromSP(KEY_COUNT) >= maxCount) {
                removeKeySP(KEY_COUNT);
            }
            finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            Toast.makeText(this, "Camera Permission granted", Toast.LENGTH_SHORT).show();
            clickmenu(7);
        } else if (requestCode == 2) {
            clickmenu(6);
        }
    }

}



