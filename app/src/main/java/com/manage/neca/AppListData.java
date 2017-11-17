package com.manage.neca;

import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class AppListData implements Comparable {
    public List<AppListData> alist = new ArrayList<>();
    int app_uid;
    int app_img;
    long rxTraffic = 0;
    long txTraffic = 0;
    String app_name;
    Drawable app_icon;

    public List<AppListData> getList() {
        return alist;
    }

    public void setList(List<AppListData> list) {
        alist = list;
    }

    public int getApp_uid() {
        return app_uid;
    }

    public void setApp_uid(int uid) {
        app_uid = uid;
        setRxTraffic(0);
        setTxTraffic(0);
    }

    public int getApp_img() {
        return app_img;
    }

    public void setApp_img(int img) {
        app_img = img;
    }

    public String getApp_name() {
        return app_name;
    }

    public void setApp_name(String name) {
        app_name = name;
    }

    public Drawable getApp_icon() {
        return app_icon;
    }

    public void setApp_icon(Drawable icon) {
        app_icon = icon;
    }

    public long getRxTraffic() {
        return rxTraffic;
    }

    public void setRxTraffic(long traffic) {
        rxTraffic = traffic;
    }

    public long getTxTraffic() {
        return txTraffic;
    }

    public void setTxTraffic(long traffic) {
        txTraffic = traffic;
    }

    public void addAppListData(int uid, int img, String name, long rx, long tx) {
        AppListData temp = new AppListData();
        temp.setApp_uid(uid);
        temp.setApp_img(img);
        temp.setApp_name(name);
        temp.setRxTraffic(rx);
        temp.setTxTraffic(tx);

        alist.add(temp);
    }

    @Override
    public int compareTo(@NonNull Object o) {
        long total = rxTraffic+txTraffic;
        AppListData temp = (AppListData) o;
        if(total > temp.rxTraffic+temp.txTraffic) {
            return -1;
        } else if(total < temp.rxTraffic+temp.txTraffic) {
            return 1;
        } else {
            return 0;
        }
    }
}
