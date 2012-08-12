/*******************************************************************************
 * Copyright (c) 2012 Curtis Larson (QuackWare)
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package edu.richmond.bannerweb;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class NetworkManager {
	
	CookieStore mCookieStore;
	HttpContext mLocalContext;
	DefaultHttpClient mHttpClient;
	
	public NetworkManager()
	{
		//Bannerweb requires cookie handling, so we have to do all this stuff!
		mCookieStore = new BasicCookieStore();
		mLocalContext = new BasicHttpContext();
		mLocalContext.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);
		
		mHttpClient = new DefaultHttpClient();
		
		//We also need to make an initial GET to the Bannerweb login page (To get the cookies).
		getInitialCookies();
	}
	
	/*
	 * Dummy method which just performs a GET on the bannerweb login page in order to retrieve
	 * yummy authentication cookies that bannerweb needs to function.
	 */
	private void getInitialCookies()
	{
		try
		{
			HttpGet get = new HttpGet("https://bannerweb.richmond.edu/bannerweb/twbkwbis.P_WWWLogin");
			HttpResponse response = mHttpClient.execute(get,mLocalContext);
			HttpEntity entity = response.getEntity();
		}
		catch(Exception ex)
		{
			
		}
		
	}
	
	private String get(String url)
	{
		try
		{
			HttpGet get = new HttpGet(url);
			HttpResponse response = mHttpClient.execute(get,mLocalContext);
			return EntityUtils.toString(response.getEntity());
		}
		catch(Exception ex)
		{
			return null;
		}
	}
	
	public String getMealPlanBalance()
	{
		String finder = "The current balance of dining dollars on your meal plan is :  \n<b>$       ";
		String source = getMealPlanPage();
		int startIndex = source.indexOf(finder) + finder.length();
		int endIndex = source.indexOf("</b>",startIndex);
		return source.substring(startIndex, endIndex);
	}
	
	private String getMealPlanPage()
	{
		String page = this.get("https://bannerweb.richmond.edu/bannerweb/hwgxspdr.display_dining_dollars");
		return page;
	}
	
	/*
	 * Performs a post on the login url with the provided sid and pin.
	 * @param sid Your university Secure ID
	 * @param pin Your pin number
	 * @return boolean indicating success
	 */
	public boolean login(String sid,String pin)
	{
		//When posting to the login page we receive a meta refresh response similar to below:
		/*
		 * <HTML>
		 * <HEAD>
		 * <meta http-equiv="refresh" content="0;url=/bannerweb/twbkwbis.P_GenMenu?name=bmenu.P_MainMnu&amp;msg=WELCOME+<b>Welcome,+Curtis+M.+Larson,+to+BannerWeb,+the+University+of+Richmond+Web+Information+System!<%2Fb>Feb+08,+201212%3A29+am">
		 * </HEAD>
		 * </HTML>
		 */
		//If we receive this refresh response we know that the login was successful. We don't really have to act on this data
		//because we are already authenticated with the bannerweb server.
		try {
			HttpPost httpPost = new HttpPost("https://bannerweb.richmond.edu/bannerweb/twbkwbis.P_ValLogin");
			List<NameValuePair> postContent = new ArrayList<NameValuePair>(2);
			postContent.add(new BasicNameValuePair("sid", sid));
			postContent.add(new BasicNameValuePair("PIN", pin));
			HttpEntity httpEntity = new UrlEncodedFormEntity(postContent);
			httpPost.setEntity(httpEntity);
			httpPost.addHeader("Accept-Encoding", "text/html");
			
			HttpResponse response = mHttpClient.execute(httpPost,mLocalContext);
			//This response string could be the original login page if there was a problem logging in, or the
			//welcome page if we were successful...
			String responseString = EntityUtils.toString(response.getEntity());
			Log.i("Response:",responseString);
			//Pretty dumb way to check success until I find something better.
			if(responseString.contains("<meta http-equiv=\"refresh\""))
			{
				return true;
			}
			else
			{
				return false;
			}
		} catch (Exception ex) {
			Log.i("error", ex.getMessage());
			return false;
		}
	}


}
