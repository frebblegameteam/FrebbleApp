package com.hollandhaptics.frebble;

<<<<<<< HEAD
import com.google.example.games.basegameutils.BaseGameActivity;
import com.microsoft.windowsazure.mobileservices.*;
=======
>>>>>>> 47333ae7966d262d1e65d7c281eb5c6cd7ee268e
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

<<<<<<< HEAD
public class HomeActivity extends Activity
=======
public class HomeActivity extends Activity 
>>>>>>> 47333ae7966d262d1e65d7c281eb5c6cd7ee268e
{

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
<<<<<<< HEAD
		
		
		
		
	}
	
	
	
	
=======
	}
>>>>>>> 47333ae7966d262d1e65d7c281eb5c6cd7ee268e

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch(item.getItemId())
		{
		case R.id.action_settings:
			openSettingsActivity();
			return true;
		case R.id.action_connect:
			openConnectActivity();
			return true;
		default:
			return super .onOptionsItemSelected(item);
			
		}
		
	}
	
<<<<<<< HEAD

=======
	
>>>>>>> 47333ae7966d262d1e65d7c281eb5c6cd7ee268e
	
	public void openSettingsActivity()
	{
		Intent intent = new Intent(this, SettingsActivity.class);
    	startActivity(intent);
	}
	
	public void openConnectActivity()
	{
		Intent intent = new Intent(this, ConnectActivity.class);
    	startActivity(intent);
	}
<<<<<<< HEAD
	

=======
>>>>>>> 47333ae7966d262d1e65d7c281eb5c6cd7ee268e
}
