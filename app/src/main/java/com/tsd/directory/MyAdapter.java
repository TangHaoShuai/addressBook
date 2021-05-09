package com.tsd.directory;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {
    private ArrayList<User> users;

    public MyAdapter(ArrayList<User> users) {
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.address_itme, null);
            TextView tvName, tvPhone;
            tvName = convertView.findViewById(R.id.tv_name);
            tvPhone = convertView.findViewById(R.id.tv_phone);
            tvName.setText(users.get(position).getName());
            tvPhone.setText(users.get(position).getPhone());
        }
        return convertView;
    }
}
