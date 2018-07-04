package com.sunny.putra.clipco;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.sunny.putra.clipco.adapter.ListClipAdapter;
import com.sunny.putra.clipco.controler.ListActivityController;
import com.sunny.putra.clipco.util.DbHelper;
import com.sunny.putra.clipco.util.Globals;
import com.sunny.putra.clipco.util.LogHelper;

import java.util.ArrayList;
import java.util.List;

public class ListClipActivity extends ListActivityController {

    private ListClipAdapter adapter;
    private RecyclerView listclip;
    AlertDialog.Builder box;
    Context mContext;

    private InterstitialAd mInterstitialAd;
    private AdView mAdView;

    List<Clip> clip;
    DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogHelper.print_me("=====onCreate=======");
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
        setContentView(R.layout.activity_list_clip);

        mAdView = findViewById(R.id.adView_list);
        listclip = findViewById(R.id.listClip);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("ClipCo");
        getSupportActionBar().setSubtitle("My Collection");

        MobileAds.initialize(this, Globals.ADD_MOB_APP_ID);

        adMobs();

        box = new AlertDialog.Builder(this);
        mContext = this;

        parsingToAdapter();
        setAdapter();
    }

    public void adMobs() {
        if(Globals.admob) {
            LogHelper.print_me("== dev adMobs ==");
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("6B7C8118873F959A250EF2732E708691")
                    .build();

            mAdView.loadAd(adRequest);

            mAdView.setAdListener(new AdListener() {
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


        } else {
            LogHelper.print_me("== prod adMobs ==");
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.setAdListener(new AdListener() {
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
            mAdView.loadAd(adRequest);
        }

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.it_del_all:
                showDialogDeleteAll(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setAdapter() {
        LogHelper.print_me("==SET ADAPTER==");
        adapter = new ListClipAdapter(mContext, clip);
        listclip.setHasFixedSize(true);
        listclip.setLayoutManager(new LinearLayoutManager(this));
        listclip.setAdapter(adapter);
    }

    public void parsingToAdapter() {
        LogHelper.print_me("=====parsingAdapter=====");
        clip = new ArrayList<>();

        dbHelper = new DbHelper(this);
        Cursor c = dbHelper.getAllData();
        String[] sList = new String[c.getCount()];
        c.moveToFirst();
        for (int ii = 0; ii < c.getCount(); ii++) {
            c.moveToPosition(ii);
            sList[ii] = c.getString(0);
                clip.add(new Clip(
                    c.getLong(0),
                    c.getString(1),
                    c.getString(2)
            ));
        }
        Log.d("fuckyou", clip.toString());

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        LogHelper.print_me("===windowsFocusChanged===");
        deleteDuplicateandNull();
        parsingToAdapter();
        setAdapter();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
    }

}
