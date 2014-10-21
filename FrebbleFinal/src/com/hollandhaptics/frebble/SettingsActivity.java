package com.hollandhaptics.frebble;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
