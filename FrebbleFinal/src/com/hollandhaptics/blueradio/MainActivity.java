package com.hollandhaptics.blueradio;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothProfile;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Process;
import android.text.Editable;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.blueradios.Brsp;
import com.blueradios.BrspCallback;
import com.hollandhaptics.frebble.R;

public class MainActivity extends Activity {
    private final String TAG = "BRSPTERM." + this.getClass().getSimpleName();
    private static final int MAX_OUTPUT_LINES = 100 + 1; // max lines in the
							 // outputView
    static List<String> _outputLines = new ArrayList<String>();

    private Brsp _brsp;
    private BluetoothDevice _selectedDevice;

    private AlertDialog confirm;
    private EditText _txtCommand;
    private TextView _textViewOutput;
    private View _view;
    private ScrollView _scrollView;

    private BrspCallback _brspCallback = new BrspCallback() {

	@Override
	public void onSendingStateChanged(Brsp obj) {
	    Log.d(TAG, "onSendingStateChanged thread id:" + Process.myTid());
	}

	@Override
	public void onConnectionStateChanged(Brsp obj) {
	    Log.d(TAG, "onConnectionStateChanged state:" + obj.getConnectionState() + " thread id:" + Process.myTid());
	    final Brsp brspObj = obj;
	    runOnUiThread(new Runnable() {
		@Override
		public void run() {
		    invalidateOptionsMenu();
		    BluetoothDevice currentDevice = brspObj.getDevice();
		    if (currentDevice != null && brspObj.getConnectionState() == BluetoothProfile.STATE_CONNECTED) {
			// Log.d(TAG, "Creating bond for device:" +
			// currentDevice.getAddress());
			// currentDevice.createBond();
		    }
		}
	    });
	}

	@Override
	public void onDataReceived(final Brsp obj) {
	    Log.d(TAG, "onDataReceived thread id:" + Process.myTid());
	    runOnUiThread(new Runnable() {
		@Override
		public void run() {
		    byte[] bytes = obj.readBytes();
		    if (bytes != null) {
			String input = new String(bytes);
			addLineToTextView(input);
		    } else {
			// This occasionally happens but no data should be lost
		    }
		}

		private void addLineToTextView(String lineText) {
		    _textViewOutput.append(lineText);
		    removeLinesFromTextView();
		    _scrollView.post(new Runnable() {
			@Override
			public void run() {
			    _scrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}
		    });
		}

		private void removeLinesFromTextView() {
		    int linesToRemove = _textViewOutput.getLineCount() - MAX_OUTPUT_LINES;
		    if (linesToRemove > 0) {
			for (int i = 0; i < linesToRemove; i++) {
			    Editable text = _textViewOutput.getEditableText();
			    int lineStart = _textViewOutput.getLayout().getLineStart(0);
			    int lineEnd = _textViewOutput.getLayout().getLineEnd(0);
			    text.delete(lineStart, lineEnd);
			}
		    }
		}

	    });
	    super.onDataReceived(obj);
	}

	@Override
	public void onError(Brsp obj, Exception e) {
	    Log.d(TAG, "onError:" + e.getMessage() + " thread id:" + Process.myTid());

	    super.onError(obj, e);
	}

	@Override
	public void onBrspModeChanged(Brsp obj) {
	    super.onBrspModeChanged(obj);
	    runOnUiThread(new Runnable() {
		@Override
		public void run() {
		    invalidateOptionsMenu();
		}
	    });
	}

	@Override
	public void onRssiUpdate(Brsp obj) {
	    Log.d(TAG, "onRssiUpdate thread id:" + Process.myTid());
	    super.onRssiUpdate(obj);
	    Log.d(TAG, "Remote device RSSI:" + obj.getLastRssi()); // Log RSSI
	}

	@Override
	public void onBrspStateChanged(Brsp obj) {
	    Log.d(TAG, "onBrspStateChanged thread id:" + Process.myTid());
	    super.onBrspStateChanged(obj);
	    int currentState = obj.getBrspState();
	    obj.readRssi(); // read the RSSI once
	    if (obj.getBrspState() == Brsp.BRSP_STATE_READY) {
		// Ready to write
		// _brsp.writeBytes("Test".getBytes());
	    }
	}

    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
	// Hack to prevent onCreate being called on orientation change
	// This probably should be done in a better way in a real app
	// http://stackoverflow.com/questions/456211/activity-restart-on-rotation-android
	super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	Log.d(TAG, "onCreate");
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_blueradiomain);
	_txtCommand = (EditText) findViewById(R.id.editTextCommand);
	_textViewOutput = (TextView) findViewById(R.id.textViewOutput);
	_scrollView = (ScrollView) findViewById(R.id.scrollView);
	_textViewOutput.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(View v) {
		// hideSoftKeyboard(); //Uncomment to hide keyboard after every
		// entry
	    }
	});
	_txtCommand.setImeOptions(EditorInfo.IME_ACTION_SEND);
	_txtCommand.setOnEditorActionListener(new OnEditorActionListener() {
	    @Override
	    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_SEND) {
		    return onEnterClicked(_txtCommand.getText());
		}
		Toast.makeText(MainActivity.this, "Not Connected", Toast.LENGTH_SHORT).show();
		return false;
	    }
	});
	_brsp = new Brsp(_brspCallback, 10000, 10000);

	doScan();
    }

    private boolean onEnterClicked(Editable s) {
	if (_brsp.getBrspState() == Brsp.BRSP_STATE_READY) {
	    if (s.length() > 0) {
		String commandString = s.append("\r").toString();
		_brsp.writeBytes(commandString.getBytes());
		TextKeyListener.clear(_txtCommand.getText());
		return true; // Keep keyboard visible
	    }
	} else {
	    Toast.makeText(MainActivity.this, "BRSP not ready", Toast.LENGTH_SHORT).show();
	}
	return false; // Hide keyboard
    }

    @Override
    protected void onDestroy() {
	Log.d(TAG, "onDestroy");
	doDisconnect();
	super.onDestroy();
    }

    @Override
    protected void onStart() {
	super.onStart();
    }

    @Override
    protected void onStop() {
	super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	Log.d(TAG, "onCreateOptionsMenu");
	getMenuInflater().inflate(R.menu.main, menu);
	return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
	Log.d(TAG, "onPrepareOptionsMenu");
	if (_selectedDevice != null) {
	    MenuItem item;
	    MenuItem connectStatusItem;
	    String menuText;

	    item = menu.findItem(R.id.menu_action_connect);
	    connectStatusItem = menu.findItem(R.id.menu_action_connect_status);
	    if (_selectedDevice != null) {
		item.setVisible(true);
		if (_brsp.getConnectionState() == BluetoothGatt.STATE_CONNECTED) {
		    item.setIcon(R.drawable.connect);
		    connectStatusItem.setIcon(R.drawable.connect);
		    item.setTitle("Disconnect");
		} else {		    
		    item.setIcon(R.drawable.disconnect);
		    connectStatusItem.setIcon(R.drawable.disconnect);
		    item.setTitle("Connect");
		}
		
	    } else {
		item.setVisible(false);
	    }

	    // Add item for changing brsp mode
	    item = menu.findItem(R.id.menu_action_brspmode);
	    if (_brsp.getConnectionState() == BluetoothGatt.STATE_CONNECTED) {
		item.setVisible(true);
		switch (_brsp.getBrspMode()) {
		case Brsp.BRSP_MODE_DATA:
		    menuText = "Data Mode";
		    break;
		case Brsp.BRSP_MODE_COMMAND:
		    menuText = "Command Mode";
		    break;
		default:
		    menuText = "";
		    // Not supported in this sample
		}
		item.setTitle(menuText);
	    } else {
		item.setVisible(false);
	    }
	}
	return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	Log.d(TAG, "onOptionsItemSelected");
	Log.d(TAG, "Title selected = " + item.getTitle());
	switch (item.getItemId()) {
	case R.id.menu_action_connect:
	case R.id.menu_action_connect_status:
	    if (_brsp.getConnectionState() == BluetoothGatt.STATE_DISCONNECTED)
		doConnect();
	    else if (_brsp.getConnectionState() == BluetoothGatt.STATE_CONNECTED)
		doDisconnect();
	    break;
	case R.id.menu_action_scan:
	    doScan();
	    break;
	case R.id.menu_action_brspmode:
	    if (_brsp.getBrspMode() == Brsp.BRSP_MODE_DATA)
		_brsp.setBrspMode(Brsp.BRSP_MODE_COMMAND);
	    else
		_brsp.setBrspMode(Brsp.BRSP_MODE_DATA);
	    break;
	case R.id.menu_action_exit:
	    doQuit();
	    break;
	case R.id.menu_action_clear_output:
	    _textViewOutput.setText("");
	    break;
	}
	return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	Log.d(TAG, "onKeyDown keyCode:" + keyCode + " event:" + event.toString());
	if (keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot()) {
	    doQuit();
	    return true;
	} else {
	    return super.onKeyDown(keyCode, event);
	}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	Log.d(TAG, "onActivityResult requestCode:" + requestCode + " resultCode:" + resultCode);
	if (resultCode == RESULT_OK) {
	    switch (requestCode) {
	    case ScanActivity.REQUEST_SELECT_DEVICE:
		if (resultCode == RESULT_OK) {
		    _selectedDevice = data.getParcelableExtra("device");
		    setTitle(data.getStringExtra("title"));
		    invalidateOptionsMenu();
		    doDisconnect();
		    doConnect();
		    showSoftKeyboard(_txtCommand);
		}
		break;
	    default:
		Log.w(TAG, "Unknown requestCode encountered in onActivityResult.  Ignoring code:" + requestCode);
		break;
	    }

	}
	super.onActivityResult(requestCode, resultCode, data);
    }

    private void hideSoftKeyboard() {
	InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
	inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

    private void showSoftKeyboard(EditText field) {
	field.setFocusableInTouchMode(true);
	field.requestFocus();
	hideSoftKeyboard();
	InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Service.INPUT_METHOD_SERVICE);
	// Not working for some reason
	// inputMethodManager.showSoftInput(field,
	// InputMethodManager.SHOW_IMPLICIT);
	inputMethodManager.toggleSoftInput(0, 0);
    }

    private void doScan() {
	Intent i = new Intent(this, ScanActivity.class);
	startActivityForResult(i, ScanActivity.REQUEST_SELECT_DEVICE);
    }

    private void doConnect() {
	if (_selectedDevice != null && _brsp.getConnectionState() == BluetoothGatt.STATE_DISCONNECTED) {
	    boolean result = false;

	    String bondStateText = "";
	    switch (_selectedDevice.getBondState()) {
	    case BluetoothDevice.BOND_BONDED:
		bondStateText = "BOND_BONDED";
		break;
	    case BluetoothDevice.BOND_BONDING:
		bondStateText = "BOND_BONDING";
		break;
	    case BluetoothDevice.BOND_NONE:
		bondStateText = "BOND_NONE";
		break;
	    }
	    Log.d(TAG, "Bond State:" + bondStateText);

	    result = _brsp.connect(this.getApplicationContext(), _selectedDevice);
	    Log.d(TAG, "Connect result:" + result);
	}
    }

    private void doDisconnect() {
	Log.d(TAG, "Atempting to disconnect");
	if (_brsp.getConnectionState() != BluetoothGatt.STATE_DISCONNECTED)
	    _brsp.disconnect();
    }

    private void doQuit() {
	new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(R.string.msg_quit_title)
		.setMessage(R.string.msg_quit_detail).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
			finish();
		    }
		}).setNegativeButton(R.string.no, null).show();
    }
}
