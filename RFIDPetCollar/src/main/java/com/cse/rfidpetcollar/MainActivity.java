package com.cse.rfidpetcollar;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.UUID;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ListActivity;
import android.content.Context;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.BroadcastReceiver;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.IBinder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.BaseAdapter;
import android.widget.Toast;
import android.bluetooth.*;
import com.cse.rfidpetcollar.adapter.NavDrawerListAdapter;
import com.cse.rfidpetcollar.model.NavDrawerItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends android.support.v7.app.ActionBarActivity {
    private NavDrawerListAdapter adapter;                   // adapter for menu items
    private String[] mNavTitles;                            // each menu item
    private TypedArray mNavIcons;                           // icon for each menu item
    private ArrayList<NavDrawerItem> navDrawerItems;        // each menu item with icon

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mTitle;

    private final String TAG = MainActivity.class.getSimpleName();

    // Bluetooth needed members
    private RBLService mBLEservice;
    private BluetoothAdapter mBLEAdapter;
    private ArrayList<BluetoothDevice> deviceList;

    private final long SCAN_PERIOD = 15000;
    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavTitles = getResources().getStringArray(R.array.nav_drawer_items);
        mNavIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mTitle = getString(R.string.app_name);

        navDrawerItems = new ArrayList<NavDrawerItem>();
        for (int i = 0; i < mNavTitles.length; i++) {
            navDrawerItems.add(new NavDrawerItem(mNavTitles[i], mNavIcons.getResourceId(i, -1)));
        }

        // Recycle the typed array
        mNavIcons.recycle();

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                // TODO: Make this set the title to the selected view's title.
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(getString(R.string.app_name));
                //getSupportActionBar().removeAllTabs();
               // getSupportActionBar().
                invalidateOptionsMenu();
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new ManageFragment()).commit();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);



        final BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBLEAdapter = mBluetoothManager.getAdapter();

        if (mBLEAdapter == null) {
            Toast.makeText(this, "BLE not supported.  BLE is required for this app to work.", Toast.LENGTH_SHORT)
                    .show();
            finish();
            return;
        }

        if(!mBLEAdapter.isEnabled()) {
            Log.e(TAG, "Bluetooth not enabled");
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Bluetooth not enabled!")
                    .setMessage("Go to settings and turn it on?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO: take user to settings
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent enableBtIntent = new Intent(
                                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        }
                    }).show();
        }

        Intent mBLEIntent = new Intent(this, RBLService.class);
        bindService(mBLEIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume()
    {
        // make sure bluetooth still enable, sync with app
        super.onResume();

        if (!mBLEAdapter.isEnabled()) {
            Log.e(TAG, "Bluetooth not enabled");
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Bluetooth not enabled!")
                    .setMessage("Go to settings and turn it on?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO: take user to settings
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent enableBtIntent = new Intent(
                                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        }
                    }).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // This hides action bar items when the nav drawer is open, per
        // Android design guidelines
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);

        return super.onPrepareOptionsMenu(menu);
    }


    /**
     * Diplays fragment view for selected nav drawer list item
     */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new ManageFragment();
                break;
            case 1:
                fragment = new AccessFragment();
                break;
            case 2:
                fragment = new LocateFragment();
                break;
// TODO: Add more fragments as needed.

            default:
                break;
        }

        if (fragment != null) {
            android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            //setNavDrawerItemsNormal();
            //TextView tv = (TextView)view.findViewById(R.id.title);
            //if (tv != null){
            //    tv.setTypeface(null, Typeface.BOLD);
            //}
            displayView(position);
        }
    }

    private void setNavDrawerItemsNormal() {
        for (int i = 0; i < mDrawerList.getChildCount(); i++) {
            View v = mDrawerList.getChildAt(i);
            TextView txtview = ((TextView) v.findViewById(R.id.title));
            txtview.setTypeface(null, Typeface.NORMAL);
        }
    }

    private boolean mScanning;
    private Handler mHandler = new Handler();

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBLEAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBLEAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBLEAdapter.stopLeScan(mLeScanCallback);
        }
    }


    private void postScan()
    {
        if(deviceList.size() > 0)
        {
            for(BluetoothDevice device : deviceList)
            {
                if(device.getAddress().equals("F7:11:FE:5A:54:60"))
                {
                    //mBLEservice.connect(device.getAddress());
                }

            }
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi,
                             byte[] scanRecord) {
            Log.i(TAG, "New LE Device: " + device.getName() + " @ " + rssi);
            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    if(device.getAddress().equals("F7:11:FE:5A:54:60"))
                    {
                       mBLEservice.connect(device.getAddress());
                    }
                }
            });
        }
    };

    public final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            //final UUID UUIDZ[] = {mBLEservice.UUID_BLE_SHIELD_RX, mBLEservice.UUID_BLE_SHIELD_TX, mBLEservice.UUID_BLE_SHIELD_SERVICE};
            //final ParcelUuid UUID[] = { new ParcelUuid(mBLEservice.UUID_BLE_SHIELD_RX), new ParcelUuid(mBLEservice.UUID_BLE_SHIELD_TX), new ParcelUuid(mBLEservice.UUID_BLE_SHIELD_SERVICE) };

            boolean enabled;
            deviceList = new ArrayList<BluetoothDevice>();

            mBLEservice = ((RBLService.LocalBinder) service)
                    .getService();

            if (enabled = mBLEservice.initialize())
            {
                // BLE initialized correctly
                scanLeDevice(enabled);
            }
            else
            {
                Log.e( TAG, "Bluetooth not enabled");
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Bluetooth not enabled!")
                        .setMessage("Go to settings and turn it on?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // take user to settings
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })

                        .show();
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
                Toast.makeText(getApplicationContext(), "Disconnected",
                        Toast.LENGTH_SHORT).show();

            } else if (RBLService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                Toast.makeText(getApplicationContext(), "Connected",
                        Toast.LENGTH_SHORT).show();
            } else if (RBLService.ACTION_DATA_AVAILABLE.equals(action)) {

            } else if (RBLService.ACTION_GATT_RSSI.equals(action)) {

            }
        }
    };
    private void getGattService(BluetoothGattService gattService) {
        if (gattService == null)
            return;

        BluetoothGattCharacteristic characteristicTx = gattService
                .getCharacteristic(RBLService.UUID_BLE_SHIELD_TX);

        BluetoothGattCharacteristic characteristicRx = gattService
                .getCharacteristic(RBLService.UUID_BLE_SHIELD_RX);
        mBLEservice.setCharacteristicNotification(characteristicRx,
                true);
        mBLEservice.readCharacteristic(characteristicRx);
    }


}
