package com.cse.rfidpetcollar;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Joseph on 4/30/2014.
 */
public class TogglePermissionActivity extends Activity {
    // Bluetooth needed members
    private RBLService mBLEservice;

    String rfidId;
    boolean allowed;
    private boolean gotGattService = false;
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
                boolean connected = mBLEservice.connect(mDeviceAddress);
                Log.i("RFID_PET_COLLAR", "Connect succeeded: " + connected);
                Toast.makeText(TogglePermissionActivity.this, "Connected to: " + mDeviceName + ", " + mDeviceAddress, Toast.LENGTH_LONG).show();
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
                //displayData(intent.getByteArrayExtra(RBLService.EXTRA_DATA));
            } else if (RBLService.ACTION_GATT_CONNECTED.equals((action))) {

            }

            if (gotGattService) {
                // send the command to the bluetooth adapter
                // send "T" command to bluetooth to toggle the rfid tag
                BluetoothGattCharacteristic characteristic = map
                        .get(RBLService.UUID_BLE_SHIELD_TX);

                byte[] buf = new byte[12];
                if (rfidId != null && characteristic != null){
                    buf[0] = 'T';
                    for (int i = 1; i < 11; i++) {
                        buf[i] = (byte)rfidId.charAt(i - 1);
                    }

                    byte yes = 1;
                    byte no = 0;

                    buf[11] = (allowed) ? yes : no;

                    characteristic.setValue(buf);

                    try { Thread.sleep(500); } catch (InterruptedException ex ) { }

                    mBLEservice.writeCharacteristic(characteristic);
                }

                finish();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent launchedFrom = getIntent();
        rfidId = launchedFrom.getStringExtra("RFID_TAG");
        allowed = launchedFrom.getBooleanExtra("IS_ALLOWED", false);
        mDevice = launchedFrom.getParcelableExtra("BLE_DEVICE");

        if (mDevice != null) {
            mDeviceName = mDevice.getName();
            mDeviceAddress = mDevice.getAddress();
        }

        Intent gattServiceIntent = new Intent(this, RBLService.class);
        bindService(gattServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mDevice != null) {
            mDeviceName = mDevice.getName();
            mDeviceAddress = mDevice.getAddress();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mBLEservice != null) {
           mBLEservice.disconnect();
           mBLEservice.close();
       }

        unregisterReceiver(mGattUpdateReceiver);

       unbindService(mServiceConnection);

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

        gotGattService = true;
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
