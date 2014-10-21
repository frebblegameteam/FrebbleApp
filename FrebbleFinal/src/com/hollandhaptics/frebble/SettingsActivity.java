package com.hollandhaptics.frebble;

<<<<<<< HEAD
import android.support.v7.app.ActionBarActivity;
=======
import com.hollandhaptics.bluetoothapp.DeviceScanActivity;
import com.hollandhaptics.blueradio.MainActivity;

>>>>>>> ff606bd689d4e4d94d62285fff620e022b5be602
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
<<<<<<< HEAD
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.google.android.gms.common.api.*;
import com.google.example.games.basegameutils.BaseGameActivity;



public class SettingsActivity extends BaseGameActivity implements View.OnClickListener{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		findViewById(R.id.logoutButton).setOnClickListener(this);
		
		
=======

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
>>>>>>> ff606bd689d4e4d94d62285fff620e022b5be602
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
<<<<<<< HEAD
		return super.onOptionsItemSelected(item);
	}

	

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.logoutButton) {
			if(isSignedIn())
			{
				
				// start the asynchronous sign in flow
		        signOut();
			}
	        
	        Intent intentLogout = new Intent(getApplicationContext(),
	                MainActivity.class);
	        startActivity(intentLogout);
	    }	
		
	}
	
	@Override
	public void onSignInSucceeded() 
	{
		findViewById(R.id.logoutButton).setVisibility(View.VISIBLE);
		
	}

	@Override
	public void onSignInFailed() {
		
		findViewById(R.id.logoutButton).setVisibility(View.GONE);
	}
}

/*
 * 
 * */
=======
		
		if(id == R.id.action_scan){
			OpenScanActivity();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void OpenScanActivity(){
		Intent intent = new Intent(this, MainActivity.class);
    	startActivity(intent);
	}
}
>>>>>>> ff606bd689d4e4d94d62285fff620e022b5be602
