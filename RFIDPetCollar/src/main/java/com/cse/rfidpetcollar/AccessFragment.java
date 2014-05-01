package com.cse.rfidpetcollar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cse.rfidpetcollar.adapter.RfidViewListAdapter;
import com.cse.rfidpetcollar.model.RfidViewItem;

import java.util.Date;
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
        ((MainActivity) getActivity()).setTitle(title);

        ListView mListView = (ListView) rootView.findViewById(R.id.list_access);
        List<RfidViewItem> items = new ArrayList<RfidViewItem>();

        Date testDateA = new Date(114, 4, 22, 19, 30, 45);
        Date testDateB = new Date(114, 4, 20, 7, 25, 15);

        items.add(new RfidViewListHeader("Sparky"));
        items.add(new RfidViewListAccess(testDateA));
        items.add(new RfidViewListAccess(testDateB));
        items.add(new RfidViewListHeader("Mr. Whiskers"));
        items.add(new RfidViewListAccess(testDateA));
        items.add(new RfidViewListAccess(testDateB));

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