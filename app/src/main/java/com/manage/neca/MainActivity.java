package com.manage.neca;

import android.app.AppOpsManager;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import cn.iwgang.familiarrecyclerview.FamiliarRecyclerView;
import cn.iwgang.familiarrecyclerview.FamiliarRefreshRecyclerView;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;
import static android.os.Process.myUid;
import static android.os.SystemClock.elapsedRealtime;
import static java.lang.Process.*;
import static java.lang.System.currentTimeMillis;

public class MainActivity extends AppCompatActivity {

    private FamiliarRefreshRecyclerView app_list;
    private FamiliarRecyclerView recyclerview;

    public static long total_Rtraffic;
    public static long total_Ttraffic;
    public static long mobile_Rtraffic;
    public static long mobile_Ttraffic;

    BroadcastReceiver receiver;

    AppListData adata;
    AppListAdapter adapter;
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(checkForPermission()) {
            Toast.makeText(getApplicationContext(), "get permission", Toast.LENGTH_LONG).show();

            TelephonyManager tmanager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String subscriberId = tmanager.getSubscriberId();

            long hi = System.currentTimeMillis()-(1209600000*3);
            Log.d("current time", System.currentTimeMillis()+"");

            NetworkStatsManager manager = (NetworkStatsManager)getSystemService(Context.NETWORK_STATS_SERVICE);
            try {
                NetworkStats stats =  manager.queryDetailsForUid(ConnectivityManager.TYPE_MOBILE, subscriberId, hi, System.currentTimeMillis(),10610);
                Log.d("String man", stats.toString());
                NetworkStats.Bucket bucket = new NetworkStats.Bucket();
                if(stats.getNextBucket(bucket)) {
                    Log.d("DATA", "다운로드: "+getDataFormat(bucket.getRxBytes())+"["+bucket.getRxBytes()+"], 업로드: "+getDataFormat(bucket.getTxBytes())+" - "+bucket.getUid()+":::"+new Date(bucket.getStartTimeStamp()).toString());
                }
                stats = manager.querySummary(ConnectivityManager.TYPE_WIFI, subscriberId, hi, System.currentTimeMillis());
                while(stats.hasNextBucket()) {
                    if(stats.getNextBucket(bucket)) {
                        if(bucket.getUid() == 10610) {
                            Log.d("DATA WIFI", "다운로드: "+getDataFormat(bucket.getRxBytes())+", 업로드: "+getDataFormat(bucket.getTxBytes())+" - "+bucket.getUid()+":::"+new Date(bucket.getStartTimeStamp()).toString());
                            break;
                        }
                    }
                }
            } catch(RemoteException e) {
                Log.e("NETWORK ERROR", e.getMessage());
            }
        } else {
            Toast.makeText(getApplicationContext(), "no permission", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }

        app_list = (FamiliarRefreshRecyclerView) findViewById(R.id.app_data_list);

        app_list.setId(R.id.app_data_list);
        app_list.setLoadMoreView(new LoadMoreView(getApplicationContext(), 1));
        app_list.setColorSchemeColors(0xFFFF5000, Color.RED, Color.YELLOW, Color.GREEN);
        app_list.setLoadMoreEnabled(true);

        recyclerview = app_list.getFamiliarRecyclerView();
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setHasFixedSize(true);

        total_Rtraffic = TrafficStats.getTotalRxBytes();
        total_Ttraffic = TrafficStats.getTotalTxBytes();
        mobile_Rtraffic = TrafficStats.getMobileRxBytes();
        mobile_Ttraffic = TrafficStats.getMobileTxBytes();


        long time = System.currentTimeMillis() - elapsedRealtime();
        String format = DateFormat.getBestDateTimePattern(Locale.KOREA, "MM/dd/yyyy hh:mm aa");
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);

        String s = "폰이 켜진 후 총 데이터 사용량 \n다운로드: "+
                getDataFormat(total_Rtraffic)+"\n업로드: "+getDataFormat(total_Ttraffic)+
                "\n모바일 다운로드: "+getDataFormat(mobile_Rtraffic)+"\n업로드: "+getDataFormat(mobile_Ttraffic)+
                "\n수집 시작 시간: "+dateFormat.format(new Date(time));
        ((TextView)findViewById(R.id.textView2)).setText(s);

        adata = new AppListData();

        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        Iterator<ApplicationInfo> applter= packages.iterator();

        while(applter.hasNext()) {
            ApplicationInfo appInfo = applter.next();
            if(appInfo.uid >= 10000) {
                double rx = (TrafficStats.getUidRxBytes(appInfo.uid) / 1024.0f) / 1024.0f;
                double tx = (TrafficStats.getUidTxBytes(appInfo.uid) / 1024.0f) / 1024.0f;

                Log.d("appinfoman", appInfo.uid+": "+appInfo.loadLabel(pm)+" - "+rx);

                //long uidTraffic =  rx + tx;
                if(rx > 0 || tx > 0) {
                    adata.addAppListData(appInfo.uid, appInfo.icon, String.valueOf(appInfo.loadLabel(pm)), TrafficStats.getUidRxBytes(appInfo.uid), TrafficStats.getUidTxBytes(appInfo.uid));
                    String msg = "app: "+appInfo.uid+"["+String.valueOf(appInfo.loadLabel(pm)+"], Traffic: rx["+rx+"], tx["+tx+"]"+" -- "+appInfo.icon);
                    Log.i("appINFO_T", msg);
                }
            }
        }
        Collections.sort(adata.alist);
        adapter = new AppListAdapter(getApplicationContext(), adata);
        adapter.notifyDataSetChanged();
        recyclerview.setAdapter(adapter);

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_OFF");
        filter.addAction("android.intent.action.SCREEN_ON");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String act = intent.getAction();
                if(act.equals("android.intent.action.SCREEN_OFF")) {
                    Toast.makeText(getApplicationContext(), "SCREEN_OFF", Toast.LENGTH_LONG).show();
                    Log.d("Receiver", "SCREEN_OFF");
                } else {
                    Toast.makeText(getApplicationContext(), "SCREEN_ON", Toast.LENGTH_LONG).show();
                    Log.d("Receiver", "SCREEN_ON");
                }
            }
        };

        registerReceiver(receiver, filter);
        flag = 1;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if(flag == 1) {
                    unregisterReceiver(receiver);
                    flag = 0;
                } else {
                    IntentFilter filter = new IntentFilter();
                    filter.addAction("android.intent.action.SCREEN_OFF");
                    filter.addAction("android.intent.action.SCREEN_ON");
                    registerReceiver(receiver, filter);
                    flag = 1;
                }

            }
        });
    }

    public String getDataFormat(long data) {
        if(data > 1024) {
            if(data > 1048576) {
                return String.format("%.2f", (((data) / 1024.0f) / 1024.0f))+" MB";
            } else {
                return String.format("%.2f", ((data) / 1024.0f))+" KB";
            }
        } else {
            return data+" B";
        }
    }

    private boolean checkForPermission() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());
        return mode == MODE_ALLOWED;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
