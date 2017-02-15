/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.mylittleswift.sampleapp;


import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import io.github.mylittleswift.blesdk.BleOperation;

import java.util.ArrayList;
import java.util.List;


/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends ListActivity {


    /**
     * Views
     **/
    ListView lv;

    private BluetoothAdapter mBluetoothAdapter;
    private DeviceListAdapter adapter;
    private boolean mScanning;
    private Handler mHandler;


    List<DeviceInfo> list = new ArrayList<>();
    //private static UUID[] serviceUuids=new UUID[]{UUID.fromString("00001950-0000-1000-8000-00805f9b34fb")};

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.

    BleOperation.DeviceDisconveredCallback deviceDisconvered_callback = new BleOperation.DeviceDisconveredCallback() {
        @Override
        public void onNewDeviceDisconvered(String deviceName, String mac, int rssi) {
            DeviceInfo info = new DeviceInfo();
            info.mac = mac;
            info.name = deviceName;
            info.rssi = rssi;
            list.add(info);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    adapter.notifyDataSetChanged();

                }
            });
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(R.string.title_devices);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Common.mBleOperation = new BleOperation(this);
        mHandler = new Handler();
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.

        //Start Scanning;
        Common.mBleOperation.scan(this.deviceDisconvered_callback);
        mScanning = true;
        this.lv = getListView();
        this.adapter = new DeviceListAdapter(DeviceScanActivity.this, 0, this.list);
        this.lv.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                this.list.clear();
                this.adapter.notifyDataSetChanged();
                Common.mBleOperation.scan(this.deviceDisconvered_callback);
                mScanning = true;
                invalidateOptionsMenu();
                break;
            case R.id.menu_stop:
                Common.mBleOperation.stopScan();
                mScanning = false;
                invalidateOptionsMenu();
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String mac = list.get(position).mac;
        Intent intent = new Intent(this, ConsoleActivity.class);
        intent.putExtra("MAC", mac);
        startActivity(intent);

    }
    static class DeviceListAdapter extends ArrayAdapter<DeviceInfo> {

        Context mContext;


        public DeviceListAdapter(Context context, int resource, List objects) {
            super(context, resource, objects);
            this.mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                View v = View.inflate(mContext, R.layout.listitem_device, null);
                ViewHolder vh = new ViewHolder(v);
                refreshData(position, v);
                return v;
            } else {

                refreshData(position, convertView);
                return convertView;
            }
        }


        private void refreshData(int position, View v) {

            ViewHolder vh = (ViewHolder) v.getTag();
            vh.deviceName.setText(super.getItem(position).name);
            vh.deviceAddress.setText(super.getItem(position).mac);
        }
    }


    // Adapter for holding devices found through scanning.
    // Device scan callback.
    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;

        ViewHolder(View v) {

            deviceName = (TextView) v.findViewById(R.id.device_name);
            deviceAddress = (TextView) v.findViewById(R.id.device_address);
            v.setTag(this);
        }

    }

    static class DeviceInfo {
        String name;
        String mac;
        int rssi;


    }
}