package com.cse.rfidpetcollar;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.cse.rfidpetcollar.adapter.RfidViewListAdapter;
import com.cse.rfidpetcollar.model.RfidViewItem;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Joe Paul on 4/1/14.
 */
public class PairFragment extends android.support.v4.app.Fragment {
    public PairFragment() {
    }

    private String title = "Pair Tags";

    private RfidViewListAdapter adapter;
    private List<RfidViewItem> items;

    // Bluetooth needed members
    private RBLService mBLEservice;
    private BluetoothAdapter mBLEAdapter;
    private ArrayList<BluetoothDevice> deviceList;

    private Map<UUID, BluetoothGattCharacteristic> map = new HashMap<UUID, BluetoothGattCharacteristic>();

    private final long SCAN_PERIOD = 15000;

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (RBLService.ACTION_GATT_DISCONNECTED.equals(action)) {
            } else if (RBLService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                getGattService(mBLEservice.getSupportedGattService());
            } else if (RBLService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getByteArrayExtra(RBLService.EXTRA_DATA));
            }
        }
    };

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pair, container, false);
        ((MainActivity) getActivity()).setTitle(title);

        Button scan = (Button) rootView.findViewById(R.id.scan_tags);
        ListView mListView = (ListView) rootView.findViewById(R.id.list_pair);
        items = new ArrayList<RfidViewItem>();
        adapter = new RfidViewListAdapter(this.getActivity(), items);
        mListView.setAdapter(adapter);

        final BluetoothManager mBluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBLEAdapter = mBluetoothManager.getAdapter();

        if (!mBLEAdapter.isEnabled()) {
            Log.e("RFID_Pet_Collar", "Bluetooth not enabled");
            new AlertDialog.Builder(PairFragment.this.getActivity())
                    .setTitle("Bluetooth not enabled!")
                    .setMessage("Go to settings and turn it on?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO: take user to settings
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
        }

        Intent mBLEIntent = new Intent(this.getActivity(), RBLService.class);
        getActivity().bindService(mBLEIntent, ((MainActivity) getActivity()).mServiceConnection, Context.BIND_AUTO_CREATE);

        setHasOptionsMenu(true);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.clear();

                // send "R" command to bluetooth to get available tags
                BluetoothGattCharacteristic characteristic = map
                        .get(RBLService.UUID_BLE_SHIELD_TX);

                byte[] buf = {'R'};

                characteristic.setValue(buf);

                mBLEservice.writeCharacteristic(characteristic);
            }
        });

        return rootView;
    }

    private void displayData(byte[] byteArray) {

        if (byteArray != null) {
            String data = new String(byteArray);
            items.add(new RfidViewListHeader("Available Tags"));

            items.add(new RfidViewListPair(data));
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this.getActivity(), "No RFID Tags Found", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set title
        ((MainActivity) getActivity()).setTitle(title);

        getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    public void onStop()
    {
        super.onStop();

        getActivity().unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mBLEservice.disconnect();
        mBLEservice.close();
    }

    private void getGattService(BluetoothGattService gattService) {
        if (gattService == null)
            return;

        BluetoothGattCharacteristic characteristic = gattService
                .getCharacteristic(RBLService.UUID_BLE_SHIELD_TX);
        map.put(characteristic.getUuid(), characteristic);

        BluetoothGattCharacteristic characteristicRx = gattService
                .getCharacteristic(RBLService.UUID_BLE_SHIELD_RX);
        mBLEservice.setCharacteristicNotification(characteristicRx,
                true);
        mBLEservice.readCharacteristic(characteristicRx);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(RBLService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(RBLService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(RBLService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(RBLService.ACTION_DATA_AVAILABLE);

        return intentFilter;
    }
}