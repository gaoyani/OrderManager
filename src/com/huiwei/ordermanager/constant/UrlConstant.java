package com.huiwei.ordermanager.constant;

import com.huiwei.commonlib.Preferences;

import android.content.Context;

public class UrlConstant 
{
	public static final String LOGIN_URL="/ordering/api/employee/login.php";
	public static final String  CANCEL_URL="/ordering/api/employee/logout.php";
	public static final String SERVICE_URL= "/ordering/api/employee/services.php"; 
	
	public static final String getLoginUrl(Context context) {
		return "http://" + Preferences.
				GetString(context, "server_ID")+LOGIN_URL;
	}

	public static final String getLogoutUrl(Context context) {
		return "http://" +Preferences.
				GetString(context, "server_ID")+CANCEL_URL;
	}
	
	public static final String getServerUrl(Context context) {
		return "http://" + Preferences.
				GetString(context, "server_ID")+SERVICE_URL;
	}
}
