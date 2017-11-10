package com.manage.neca;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.TrafficStats;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.iwgang.familiarrecyclerview.FamiliarRecyclerView;
import cn.iwgang.familiarrecyclerview.FamiliarRefreshRecyclerView;

public class MainActivity extends AppCompatActivity {

    private FamiliarRefreshRecyclerView app_list;
    private FamiliarRecyclerView recyclerview;

    public static long total_Rtraffic;
    public static long total_Ttraffic;

    AppListData adata;
    AppListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        String s = "폰이 켜진 후 총 데이터 사용량 - \n다운로드: "+
                String.format("%.2f", (((total_Rtraffic) / 1024.0f) / 1024.0f))+"mb\n업로드: "+String.format("%.2f", (((total_Ttraffic) / 1024.0f) / 1024.0f))+"mb";
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

                //long uidTraffic =  rx + tx;
                if(rx > 0 || tx > 0) {
                    adata.addAppListData(appInfo.uid, appInfo.icon, String.valueOf(appInfo.loadLabel(pm)), TrafficStats.getUidRxBytes(appInfo.uid), TrafficStats.getUidTxBytes(appInfo.uid));
                    String msg = "app: "+appInfo.uid+"["+String.valueOf(appInfo.loadLabel(pm)+"], Traffic: rx["+rx+"], tx["+tx+"]"+" -- "+appInfo.icon);
                    Log.i("appINFO_T", msg);
                }
            }
        }
        adapter = new AppListAdapter(getApplicationContext(), adata);
        recyclerview.setAdapter(adapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
