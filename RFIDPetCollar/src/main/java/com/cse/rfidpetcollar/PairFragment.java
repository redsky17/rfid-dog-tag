package com.cse.rfidpetcollar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.cse.rfidpetcollar.adapter.RfidViewListAdapter;
import com.cse.rfidpetcollar.model.RfidViewItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Joe Paul on 4/1/14.
 */
public class PairFragment extends android.support.v4.app.Fragment {
    public PairFragment(){}
    private String title = "Pair Tags";

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pair, container, false);
        ((MainActivity) getActivity()).setTitle(title);

        Button scan = (Button) rootView.findViewById(R.id.scan_tags);
        ListView mListView = (ListView) rootView.findViewById(R.id.list_pair);
        final List<RfidViewItem> items = new ArrayList<RfidViewItem>();
        final RfidViewListAdapter adapter = new RfidViewListAdapter(this.getActivity(), items);
        mListView.setAdapter(adapter);

        setHasOptionsMenu(true);
        scan.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                items.add(new RfidViewListHeader(inflater, "Available Tags"));
                items.add(new RfidViewListPair(inflater, "000000001"));
                items.add(new RfidViewListPair(inflater, "000000002"));
                adapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }

        @Override
        public void onResume()
        {
        super.onResume();
        // Set title
        ((MainActivity) getActivity()).setTitle(title);
    }
}