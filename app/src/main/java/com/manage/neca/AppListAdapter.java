package com.manage.neca;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AppListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    AppListData adata;
    Context context;

    public AppListAdapter(Context con, AppListData data) {
        context = con;
        adata = data;
    }

    public void set_AppListData(AppListData data) {
        if(adata != data) {
            adata = data;
            notifyDataSetChanged();
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_list_item, parent, false);

        AppListViewHolder holder = new AppListViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(adata.getList().size() > 0) {
            if(position < adata.getList().size()) {
                final AppListViewHolder listViewHolder = (AppListViewHolder) holder;

                listViewHolder.set_App_data(adata.getList().get(position), context);
            }
        }
    }

    @Override
    public int getItemCount() {
        if(adata == null) {
            return 0;
        }
        return adata.getList().size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
