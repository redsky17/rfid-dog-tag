package com.cse.rfidpetcollar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.cse.rfidpetcollar.model.RfidViewItem;

import java.util.List;

/**
 * Created by Joseph on 2/5/14.
 */
public class RfidViewListAdapter extends ArrayAdapter<RfidViewItem> {
    private LayoutInflater mInflater;

    public enum RowType {
        LIST_ITEM, HEADER_ITEM, LIST_ACCESS
    }

    public RfidViewListAdapter(Context context, List<RfidViewItem> items) {
        super(context, 0, items);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getViewTypeCount() {
        return RowType.values().length;

    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(mInflater, convertView);
    }
}