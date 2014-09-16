package com.hollandhaptics.test;


import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;

import java.io.IOException;

public class hiWorld {

    private Form current;

    public void init(Object context) 
    {
        try
        {
            Resources theme = Resources.openLayered("/theme");
            UIManager.getInstance().setThemeProps(theme.getTheme(theme.getThemeResourceNames()[0]));
       }catch(IOException e)
        {
            e.printStackTrace();
        }
        // Pro users - uncomment this code to get crash reports sent to you automatically
        /*Display.getInstance().addEdtErrorHandler(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                evt.consume();
                Log.p("Exception in AppName version " + Display.getInstance().getProperty("AppVersion", "Unknown"));
                Log.p("OS " + Display.getInstance().getPlatformName());
                Log.p("Error " + evt.getSource());
                Log.p("Current Form " + Display.getInstance().getCurrent().getName());
                Log.e((Throwable)evt.getSource());
                Log.sendLog();
            }
        });*/
    }
    
    public void start() 
    {
        if(current != null)
        {
            current.show();
            return;
        }
        Form hi = new Form("Hi World");
        Button d = new Button("show Dialog");
        hi.addComponent(new Label("Hi World"));
        hi.addComponent(d);
        d.addActionListener(new ActionListener()
        {
        	public void actionPerformed(ActionEvent ev)
        	{
        		Dialog.show("Hello", "Hi there", "ok", null);
        	}
        });
        hi.show();
    }

    public void stop() 
    {
        current = Display.getInstance().getCurrent();
    }
    
    public void destroy() 
    {
    	
    }

}
