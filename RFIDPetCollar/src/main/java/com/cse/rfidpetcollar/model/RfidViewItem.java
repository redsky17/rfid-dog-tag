package com.cse.rfidpetcollar.model;

import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Joseph on 2/5/14.
 */
public interface RfidViewItem {
    public int getViewType();
    public View getView(LayoutInflater inflater, View convertView);
}