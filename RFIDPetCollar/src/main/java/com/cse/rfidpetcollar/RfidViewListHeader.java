package com.cse.rfidpetcollar;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cse.rfidpetcollar.adapter.RfidViewListAdapter;
import com.cse.rfidpetcollar.model.RfidViewItem;

/**
 * Created by Joseph on 2/5/14.
 */
public class RfidViewListHeader implements RfidViewItem {
    private final String         name;

    public RfidViewListHeader(String name) {
        this.name = name;
    }

    @Override
    public int getViewType() {
        return RfidViewListAdapter.RowType.HEADER_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.header, null);
            // Do some initialization
        } else {
            view = convertView;
        }

        TextView text = (TextView) view.findViewById(R.id.list_header);
        text.setText(name);

        return view;
    }

}
