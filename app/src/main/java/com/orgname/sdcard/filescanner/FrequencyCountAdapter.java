package com.orgname.sdcard.filescanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;


public class FrequencyCountAdapter extends BaseAdapter {
    private final ArrayList mData;

    public FrequencyCountAdapter(Map<String, Integer> map) {
        mData = new ArrayList();
        mData.addAll(map.entrySet());
    }

    @Override
    public int getCount() {
        return mData.size();
    }


    @Override
    public Map.Entry<String, Integer> getItem(int position) {
        return (Map.Entry) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View result;

        if (convertView == null) {
            result = LayoutInflater.from(parent.getContext()).inflate(R.layout.frequency_list_row, parent, false);
        } else {
            result = convertView;
        }

        Map.Entry<String, Integer> item = getItem(position);

        ((TextView) result.findViewById(R.id.extension_name)).setText(item.getKey());
        ((TextView) result.findViewById(R.id.extension_size)).setText(""+item.getValue());

        return result;
    }

    public void refresh()
    {
        notifyDataSetChanged();
    }
}

