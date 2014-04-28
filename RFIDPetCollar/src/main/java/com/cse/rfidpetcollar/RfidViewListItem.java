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
    private final String         str1;
    private final LayoutInflater inflater;
    private boolean isChecked = false;

    public RfidViewListItem(LayoutInflater inflater, String text1) {
        this.str1 = text1;
        this.inflater = inflater;
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

        text1.setText(str1);

        return view;
    }

    public boolean isChecked(){
        return this.isChecked;
    }

    public void setIsChecked(boolean checked){
        this.isChecked = checked;
    }

}