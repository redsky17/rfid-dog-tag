package com.cse.rfidpetcollar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cse.rfidpetcollar.adapter.RfidViewListAdapter;
import com.cse.rfidpetcollar.model.RfidViewItem;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joseph on 3/27/14.
 */
public class AccessFragment extends android.support.v4.app.Fragment {
    public AccessFragment(){}
    private String title = "Access Times";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_access, container, false);
        ListView mListView = (ListView) rootView.findViewById(R.id.list_access);
        Time testTime = new Time(950);

        ((MainActivity) getActivity()).setTitle(title);

        List<RfidViewItem> items = new ArrayList<RfidViewItem>();
        items.add(new RfidViewListHeader(inflater, "PET DOOR LAST ACCESS TIMES"));

        items.add(new RfidViewListAccess(inflater,"Sparky", testTime));
        items.add(new RfidViewListAccess(inflater,"Mr. Whiskers", testTime));

        RfidViewListAdapter adapter = new RfidViewListAdapter(this.getActivity(), items);
        mListView.setAdapter(adapter);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        ((MainActivity) getActivity()).setTitle(title);
    }
}