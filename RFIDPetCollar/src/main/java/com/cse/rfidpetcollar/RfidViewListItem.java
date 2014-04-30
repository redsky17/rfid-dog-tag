package com.cse.rfidpetcollar;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cse.rfidpetcollar.adapter.RfidViewListAdapter;
import com.cse.rfidpetcollar.model.RfidViewItem;

/**
 * Created by Joseph on 2/5/14.
 */
public class RfidViewListItem implements RfidViewItem {
    private final String         name;
    private final String         rfidId;
    //private final LayoutInflater inflater;
    private boolean isChecked = false;

    public RfidViewListItem(String text1, String rfidId) {
        this.name = text1;
        this.rfidId = rfidId;
        //this.inflater = inflater;
    }

    @Override
    public int getViewType() {
        return RfidViewListAdapter.RowType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.list_item, null);
            // Do some initialization
        } else {
            view = convertView;
        }

        TextView text1 = (TextView) view.findViewById(R.id.list_content);

        text1.setText(name);

        return view;
    }

    public String getRfidId(){
        return rfidId;
    }

    public boolean isChecked(){
        return this.isChecked;
    }

    public void setIsChecked(boolean checked){
        this.isChecked = checked;
    }

}