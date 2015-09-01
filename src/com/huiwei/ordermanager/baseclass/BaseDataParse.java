package com.huiwei.ordermanager.baseclass;

import org.json.JSONException;
import org.json.JSONObject;

public class BaseDataParse 
{
	public JSONObject Parse(String strJson)
	{
		
		JSONObject jsonObj = null;
		try 
		{
			jsonObj = new JSONObject(strJson);
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
			jsonObj = null;
		}
		return jsonObj;
	}
}
