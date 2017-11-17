package com.manage.neca;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class AppListViewHolder extends RecyclerView.ViewHolder {
    public TextView name, datatext;
    public ImageView icon;
    public ProgressBar dataper;

    public AppListData adata;

    public AppListViewHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.app_name);
        datatext = (TextView) itemView.findViewById(R.id.app_data_text);
        icon = (ImageView) itemView.findViewById(R.id.app_icon);
        dataper = (ProgressBar) itemView.findViewById(R.id.app_dataper);
    }

    public void set_App_data(AppListData data, Context context) {
        adata = data;

        long traffic = data.getRxTraffic() + data.getTxTraffic();

        name.setText(data.getApp_name());
        int res = data.getApp_img();
        if(res == 0) {
            Picasso.with(context).load(R.mipmap.ic_launcher).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher_round).fit().into(icon);
        } else {
            Picasso.with(context).load(res).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher_round).fit().into(icon);
        }


        dataper.setClickable(false);
        double pro = (int)(traffic / (MainActivity.total_Rtraffic + MainActivity.total_Ttraffic) * 100.0f);
        Log.v("pro", pro+"");
        dataper.setProgress((int)pro);
        datatext.setText( getDataFormat(traffic) );
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
}
