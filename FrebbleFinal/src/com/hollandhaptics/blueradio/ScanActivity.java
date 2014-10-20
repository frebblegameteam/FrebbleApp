/**
 * File=ScanActivity.java
 *
 * Created by Michael Testa on Aug 1, 2013
 * Copyright 2013 BlueRadios, Inc. All rights reserved.
 *
 */
package com.hollandhaptics.blueradio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.hollandhaptics.frebble.R;

/**
 * Discovery LE BRSP Devices Controller
 * 
 */
public class ScanActivity extends Activity {
    private final String TAG = "BRSPTERM." + this.getClass().getSimpleName();
    public static final int REQUEST_SELECT_DEVICE = 1;

    private ArrayList<DeviceItem> _deviceList;
    private Map<String, DeviceItem> _deviceMap = new HashMap<String, DeviceItem>();
    private ArrayAdapter<DeviceItem> _deviceAdapter;

    private BluetoothAdapter _bluetoothAdapter;

    // Helper class for a device list item data element
    private static class DeviceItem {
	public String id;
	public String name;
	public BluetoothDevice bluetoothDevice;
	public int rssi;

	public DeviceItem(BluetoothDevice device) {
	    this.id = device.getAddress();
	    this.name = device.getName();
	    this.bluetoothDevice = device;
	}

	@Override
	public String toString() {
	    return "Name: " + name + "\nAddress: " + id + "\nRSSI: " + rssi + "\nType: " +typeToString(bluetoothDevice.getType());
	}
	private String typeToString(int deviceType) {
	    switch (deviceType) {
	    case 1:
		return "Classic (CB Only)";
	    case 2:
		return "Single Mode (LE Only)";
	    case 3:
		return "Dual Mode (CB/LE)";
	    default:
		return "Unknown";
	    }
	}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	Log.d(TAG, "onCreate");
	super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
	Log.d(TAG, "onDestroy");
	super.onDestroy();
    }

    @Override
    protected void onRestart() {
	Log.d(TAG, "onRestart");
	super.onRestart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
	Log.d(TAG, "onRestoreInstanceState");
	super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
	Log.d(TAG, "onResume");
	super.onResume();
	init();
	startDiscovery();
    }

    @Override
    protected void onPause() {
	Log.d(TAG, "onPause");
	stopDiscovery();
	super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
	Log.d(TAG, "onSaveInstanceState");
	super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
	Log.d(TAG, "onStart");
	super.onStart();
	init();
	startDiscovery();
    }

    @Override
    protected void onStop() {
	Log.d(TAG, "onStop");
	super.onStop();
	stopDiscovery();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
	super.onConfigurationChanged(newConfig);
    }

    private void init() {
	setContentView(R.layout.activity_scan);

	_deviceList = new ArrayList<DeviceItem>();
	_deviceAdapter = new ArrayAdapter<DeviceItem>(this, android.R.layout.simple_list_item_1, _deviceList);

	final ListView listview = (ListView) findViewById(R.id.scanview);

	if (_bluetoothAdapter == null || !_bluetoothAdapter.isEnabled()) {
	    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	    startActivityForResult(enableBtIntent, 1);
	}

	final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
	if (_bluetoothAdapter == null) {
	    _bluetoothAdapter = bluetoothManager.getAdapter();
	}

	listview.setAdapter(_deviceAdapter);

	listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	    @Override
	    public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
		stopDiscovery();
		DeviceItem selectedDeviceItem = ((DeviceItem) _deviceList.get(position));
		String selectedTitle = getResources().getString(R.string.app_name) + " - " + selectedDeviceItem.name;
		Log.d(TAG, "Address selected:" + selectedDeviceItem.id);

		Intent returnIntent = new Intent();
		returnIntent.putExtra("title", selectedTitle);
		returnIntent.putExtra("device", selectedDeviceItem.bluetoothDevice);
		setResult(RESULT_OK, returnIntent);
		finish();
		return;
	    }
	});
    }

    private void startDiscovery() {
	
	if (_bluetoothAdapter != null) {
	    if (!_bluetoothAdapter.isEnabled())
		_bluetoothAdapter.enable();
	    if (!_bluetoothAdapter.isDiscovering()) {
		_bluetoothAdapter.stopLeScan(mLeScanCallback);
	    }
	    Log.d(TAG, "Starting scan on thread id:" + Process.myTid());
	    _bluetoothAdapter.startLeScan(mLeScanCallback);
	} else {
	    Log.e(TAG, "Bluetooth adapter is null");
	}
    }
    
    

    private void stopDiscovery() {
	_bluetoothAdapter.stopLeScan(mLeScanCallback);
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
	@Override
	public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
	    Log.v(TAG, "Scan found device" + device.getAddress());
	    Log.d(TAG, "onLeScan called from thread id:" + Process.myTid());
	    runOnUiThread(new Runnable() {
		public void run() {
		    Log.d(TAG, "Updating data and ui from thread id:" + Process.myTid());
		    addDeviceItem(device, rssi);
		    _deviceAdapter.notifyDataSetChanged();
		}
	    });
	}
    };

    // Adds a device item to the collections if they don't exist.
    // The existing is modified if already in the collections
    private void addDeviceItem(BluetoothDevice device, int rssi) {
	DeviceItem existingDevice = getDeviceItem(device.getAddress());
	if (existingDevice == null) {
	    DeviceItem item = new DeviceItem(device);
	    item.rssi = rssi;
	    _deviceList.add(item);
	    _deviceMap.put(item.id, item);
	} else {
	    // Modify existing values
	    existingDevice.name = device.getName();
	    existingDevice.rssi = rssi;
	}
    }

    // returns a device item by key address. Null if not exists
    private DeviceItem getDeviceItem(String address) {
	for (DeviceItem device : _deviceList)
	    if (device.id.equals(address))
		return device;
	return null;
    }

}
