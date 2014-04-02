package com.cse.rfidpetcollar;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    private String title = "Manage Pets";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_manage, container, false);
        ListView mListView = (ListView) rootView.findViewById(R.id.list_manage);

        ((MainActivity) getActivity()).setTitle(title);

        List<RfidViewItem> items = new ArrayList<RfidViewItem>();
        items.add(new RfidViewListHeader(inflater, "PET DOOR PERMISSIONS"));
        items.add(new RfidViewListItem(inflater,"Sparky"));
        items.add(new RfidViewListItem(inflater,"Mr. Whiskers"));


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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        android.support.v4.app.Fragment fragment = null;
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_pair:
                fragment = new PairFragment();
                break;
            case R.id.action_edit:
                //Todo: add edit
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        if (fragment != null) {
            android.support.v4.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();


        } else {
            Log.e("MainActivity", "Error in creating fragment");
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        ((MainActivity) getActivity()).setTitle(title);
    }
}