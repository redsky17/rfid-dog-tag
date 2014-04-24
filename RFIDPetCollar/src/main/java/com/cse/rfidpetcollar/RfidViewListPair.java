package com.cse.rfidpetcollar;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cse.rfidpetcollar.adapter.RfidViewListAdapter;
import com.cse.rfidpetcollar.model.RfidViewItem;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Joe Paul on 4/10/14.
 */
public class RfidViewListPair implements RfidViewItem {
    private String tag;
    private final LayoutInflater inflater;

    public RfidViewListPair(LayoutInflater inflater, String tagId) {
        this.tag = tagId;
        this.inflater = inflater;
    }

    @Override
    public int getViewType() {
        return RfidViewListAdapter.RowType.LIST_PAIR.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.list_pair, null);
        } else {
            view = convertView;
        }

        TextView tag2 = (TextView) view.findViewById(R.id.tag_id);
        tag2.setText(tag);

        return view;
    }

}