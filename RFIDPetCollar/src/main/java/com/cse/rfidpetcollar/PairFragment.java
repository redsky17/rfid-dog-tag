package com.cse.rfidpetcollar;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
    private BluetoothDevice mDevice;
    private String mDeviceAddress;
    private String mDeviceName;
    private Map<UUID, BluetoothGattCharacteristic> map = new HashMap<UUID, BluetoothGattCharacteristic>();

    public final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            //final UUID UUIDZ[] = {mBLEservice.UUID_BLE_SHIELD_RX, mBLEservice.UUID_BLE_SHIELD_TX, mBLEservice.UUID_BLE_SHIELD_SERVICE};
            //final ParcelUuid UUID[] = { new ParcelUuid(mBLEservice.UUID_BLE_SHIELD_RX), new ParcelUuid(mBLEservice.UUID_BLE_SHIELD_TX), new ParcelUuid(mBLEservice.UUID_BLE_SHIELD_SERVICE) };

             mBLEservice = ((RBLService.LocalBinder) service)
                    .getService();

            if (!mBLEservice.initialize())
            {
                Log.i("RFID_PET_COLLAR", "Unable to initialize Bluetooth in PairFragment");
            }
            else
            {
                mBLEservice.connect(mDeviceAddress);
                Toast.makeText(getActivity(), "Connected to: " + mDeviceName, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBLEservice = null;
        }
    };

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

        //Bundle args = getArguments();
        //if (args != null) {
        //    mDevice = args.getParcelable(MainActivity.EXTRAS_DEVICE);
        //} else {
        //    Toast.makeText(getActivity(), "Arguments null", Toast.LENGTH_LONG).show();
        //}

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

        Intent gattServiceIntent = new Intent(getActivity(), RBLService.class);
        getActivity().bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

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

        if (((MainActivity) getActivity()).mDevice != null) {
            mDeviceName = ((MainActivity) getActivity()).mDevice.getName();
            mDeviceAddress = ((MainActivity) getActivity()).mDevice.getAddress();
        }


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