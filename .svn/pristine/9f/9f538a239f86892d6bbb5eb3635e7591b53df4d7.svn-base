package com.het.dx.adpter;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.het.ap.bean.ApDeviceBean;
import com.het.dx.R;

import java.util.ArrayList;
import java.util.List;


public class WiFiAdpter extends BaseAdapter {
    private Context context;
    List<ApDeviceBean> list = new ArrayList<>();
    private LayoutInflater mInflater = null;

    public WiFiAdpter(Context context,List<ApDeviceBean> llll) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.list = llll;
    }

    public void notifyChanged(){
        this.notifyDataSetChanged();
        this.notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position).getSsid();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        //如果缓存convertView为空，则需要创建View
        if(convertView == null)
        {
            holder = new ViewHolder();
            //根据自定义的Item布局加载布局
            convertView = mInflater.inflate(R.layout.ssid_item, null);
            holder.ssid = (TextView)convertView.findViewById(R.id.ssidstr);
            holder.beiz = (TextView)convertView.findViewById(R.id.beiz);
            //将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
            convertView.setTag(holder);
        }else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.ssid.setText(list.get(position).getSsid());
//        holder.beiz.setText(list.get(position).getBssid());

        return convertView;
    }

    //ViewHolder静态类
    static class ViewHolder
    {
        public TextView ssid;
        public TextView beiz;
    }
}