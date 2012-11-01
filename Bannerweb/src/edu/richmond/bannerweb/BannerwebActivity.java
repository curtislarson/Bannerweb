/*******************************************************************************
 * Copyright (c) 2012 Curtis Larson (QuackWare)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package edu.richmond.bannerweb;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BannerwebActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
	
	//https://bannerweb.richmond.edu/bannerweb/twbkwbis.P_WWWLogin
	//<FORM ACTION="/bannerweb/twbkwbis.P_ValLogin" METHOD="POST" NAME="loginform" AUTOCOMPLETE="OFF">
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setupButtonListeners();
        
    }
    
    private void setupButtonListeners()
    {
    	((Button)findViewById(R.id.buttonRetrieveBalance)).setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		String userId = ((EditText)findViewById(R.id.userIdEditText)).getText().toString();
		String pin = ((EditText)findViewById(R.id.PINeditText)).getText().toString();
		NetworkManager nm = new NetworkManager();
		boolean success = nm.login(userId,pin);
		TextView tv = (TextView)findViewById(R.id.resultTextView);
        if(success)
        {
        	String balance = nm.getMealPlanBalance();
        	String spiderBalance = nm.getSpiderDollarBalance();
        	tv.setText("Meal Swipes: " + nm.getMealPlanSwipes() + "\n" + "Dining Dollars: $" + balance + "\n" + "Spider Dollars: $" + spiderBalance);
        }
        else
        {
        	tv.setText("Unable to retrieve balance, please try again.");
        	tv.setTextColor(Color.RED);
        }
	}
}
