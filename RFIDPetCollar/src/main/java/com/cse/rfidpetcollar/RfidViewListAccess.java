package com.cse.rfidpetcollar;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cse.rfidpetcollar.adapter.RfidViewListAdapter;
import com.cse.rfidpetcollar.model.RfidViewItem;

import java.sql.Time;

/**
 * Created by Joe Paul on 4/10/14.
 */
public class RfidViewListAccess implements RfidViewItem {
    private final String  petName;
    private Time time;
    private final LayoutInflater inflater;

    public RfidViewListAccess(LayoutInflater inflater, String text1, Time time1) {
        this.petName = text1;
        this.time = time1;
        this.inflater = inflater;
    }

    @Override
    public int getViewType() {
        return RfidViewListAdapter.RowType.LIST_ACCESS.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.list_access, null);
        } else {
            view = convertView;
        }

        TextView name = (TextView) view.findViewById(R.id.pet_name);
        name.setText(petName);

        TextView time2 = (TextView) view.findViewById(R.id.access_time);
        time2.setText(time.toString());

        return view;
    }

}