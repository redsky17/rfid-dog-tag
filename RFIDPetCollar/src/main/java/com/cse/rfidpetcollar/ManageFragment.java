package com.cse.rfidpetcollar;

import android.app.Fragment;
import android.content.ClipData;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cse.rfidpetcollar.adapter.RfidViewListAdapter;
import com.cse.rfidpetcollar.model.RfidViewItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joseph on 2/5/14.
 */
public class ManageFragment extends android.support.v4.app.Fragment {

    public ManageFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_manage, container, false);

        ListView mListView = (ListView) rootView.findViewById(R.id.list_manage);

        List<RfidViewItem> items = new ArrayList<RfidViewItem>();
        items.add(new RfidViewListHeader(inflater, "DOGGY DOOR"));
        items.add(new RfidViewListItem(inflater,"Sparky"));
        items.add(new RfidViewListItem(inflater,"Mr. Whiskers"));
        items.add(new RfidViewListHeader(inflater, "LITTER BOX"));
        items.add(new RfidViewListItem(inflater, "Sparky"));
        items.add(new RfidViewListItem(inflater, "Mr. Whiskers"));

        RfidViewListAdapter adapter = new RfidViewListAdapter(this.getActivity(), items);
        mListView.setAdapter(adapter);

        setHasOptionsMenu(true);

        return rootView;
    }

    private void getListViewItems(){
        //SharedPreferences prefs = getSharedPreferences()
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.manage_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }
}