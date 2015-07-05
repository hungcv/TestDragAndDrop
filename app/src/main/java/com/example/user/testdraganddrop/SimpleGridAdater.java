package com.example.user.testdraganddrop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by User on 7/1/2015.
 */
public class SimpleGridAdater extends BaseAdapter {

    public SimpleGridAdater(List<ItemGrid> list) {
        this.list = list;
    }

    List<ItemGrid> list;

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gridview,parent,false);
        }
        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        tvTitle.setText(list.get(position).text);
        return convertView;
    }
}
