package com.cse.rfidpetcollar;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cse.rfidpetcollar.adapter.RfidViewListAdapter;
import com.cse.rfidpetcollar.model.RfidViewItem;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Joe Paul on 4/10/14.
 */
public class RfidViewListAccess implements RfidViewItem {
    private Date date;
    private final LayoutInflater inflater;

    public RfidViewListAccess(LayoutInflater inflater, Date date1) {
        this.date = date1;
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

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy [HH:mm:ss]");

        TextView date2 = (TextView) view.findViewById(R.id.access_date);
        date2.setText(dateFormat.format(date));

        return view;
    }

}