package com.hollandhaptics.frebble;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class MainActivity extends ActionBarActivity 
{
	public final static String EXTRA_USER = "com.hollandhaptics.frebble.USER";
	public final static String EXTRA_PASS = "com.hollandhaptics.frebble.PASS";
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
       
        
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
    
    public void loginEvent(View view)
    {
    	//placeholder code
    	Intent intent = new Intent(this, HomeActivity.class);
    	EditText userField = (EditText) findViewById(R.id.userField);
    	EditText passwordField = (EditText) findViewById(R.id.passwordField);
    	String username = userField.getText().toString();
    	String password = passwordField.getText().toString();
    	intent.putExtra(EXTRA_USER, username);
    	intent.putExtra(EXTRA_PASS, password);
    	startActivity(intent);
    }
    
    public void signupEvent(View view)
    {
    	Intent intent = new Intent(this, SignUpActivity.class);
    	startActivity(intent);
    }
    
   
}