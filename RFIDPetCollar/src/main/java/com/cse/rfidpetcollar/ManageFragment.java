package com.cse.rfidpetcollar;

import android.app.FragmentTransaction;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.cse.rfidpetcollar.adapter.RfidViewListAdapter;
import com.cse.rfidpetcollar.model.RfidViewItem;
import com.cse.rfidpetcollar.sql.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * Created by Joseph on 2/5/14.
 */
public class ManageFragment extends android.support.v4.app.Fragment {
    public ManageFragment(){}

    //private BluetoothDevice mDevice;
    private String title = "Manage Pets";

    private ListView mListView;
    private RfidViewListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_manage, container, false);
        mListView = (ListView) rootView.findViewById(R.id.list_manage);

        ((MainActivity) getActivity()).setTitle(title);

        List<RfidViewItem> items = new ArrayList<RfidViewItem>();
        items.add(new RfidViewListHeader("PET DOOR PERMISSIONS"));

        DatabaseHelper helper = new DatabaseHelper(this.getActivity());
        List<Pet> pets = helper.getAllPets();

        for (Pet pet : pets) {
            items.add(new RfidViewListItem(pet.getName(), pet.getRfidId(), pet.getAllowed()));
        }

        //items.add(new RfidViewListItem(inflater,"Sparky"));
        //items.add(new RfidViewListItem(inflater,"Mr. Whiskers"));

        adapter = new RfidViewListAdapter(this.getActivity(), items);
        mListView.setAdapter(adapter);

        setHasOptionsMenu(true);

        Bundle args = getArguments();
        //if (args != null) {
        //    mDevice = args.getParcelable(MainActivity.EXTRAS_DEVICE);
        //} else {
        //    Toast.makeText(getActivity(), "Arguments null", Toast.LENGTH_LONG).show();
        //}

        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu){
        try {
            DrawerLayout mDrawerLayout = (DrawerLayout) getView().findViewById(R.id.drawer_layout);
            ListView mDrawerList = (ListView) getView().findViewById(R.id.left_drawer);

            boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

            menu.findItem(R.id.action_pair).setVisible(!drawerOpen);
            //menu.findItem(R.id.action_edit).setVisible(!drawerOpen);

            super.onPrepareOptionsMenu(menu);
        }
        catch (Exception ex) { }
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
            default:
                return super.onOptionsItemSelected(item);
        }

        if (fragment != null) {
            //Bundle bundle = new Bundle();
            //bundle.putParcelable(MainActivity.EXTRAS_DEVICE, mDevice);
            //fragment.setArguments(bundle);

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

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public static final class RfidListItemActionMode implements ActionMode.Callback {
        Context ctx;

        public RfidListItemActionMode(Context ctx){
            this.ctx = ctx;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

            menu.add("Delete").setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            Toast toast = null;

            ArrayList<RfidViewListItem> selectedListItems = new ArrayList<RfidViewListItem>();

            StringBuilder selectedItems = new StringBuilder();

            // get items selected
            //for (RfidViewListItem i : ((ManageFragment) ctx).adapter.mailList) {
            //    if (i.isChecked()) {
            //        selectedListItems.add(i);
            //        selectedItems.append(i.getTitle()).append(", ");
            //    }
            //}

            if (item.getTitle().equals("Delete")) {
                // Delete
                toast = Toast.makeText(ctx, "Delete: " + selectedItems.toString(), Toast.LENGTH_SHORT);

            }
            if (toast != null) {
                toast.show();
            }
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) { }
    }
}