package com.huiwei.ordermanager.task;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.huiwei.commonlib.MD5;
import com.huiwei.commonlib.Preferences;
import com.huiwei.ordermanager.common.SysApplication;
import com.huiwei.ordermanager.constant.CommonConstant;
import com.huiwei.ordermanager.info.DishesInfo;

public class PreferHistoryTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	int flag = 0;
	DishesInfo dishesInfo;
	String orderID;

	public PreferHistoryTask(Context context, Handler handler, 
			String orderID, DishesInfo dishesInfo) {
		this.context = context;
		this.handler = handler;
		this.dishesInfo = dishesInfo;
		this.orderID = orderID;
	}

	@Override
	protected Integer doInBackground(String... params) {

		String url = "" + params[0];
		try {

			HttpPost request = new HttpPost(url);
			JSONObject param = new JSONObject();
			param.put("order_id", orderID);
			param.put("food_id", dishesInfo.id);
			
			JSONObject ep = new JSONObject();
			ep.put("ver", "1.0");
			String serial = MD5.getRandomString(32);
			ep.put("serial", serial);
			ep.put("params", param);
			ep.put("method", "getpreference");
			ep.put("auth", MD5.md5s(serial + MD5.md5s("getpreference")));
			
			StringEntity se = new StringEntity(ep.toString());
			request.setEntity(se);
			String sessionID = Preferences.GetString(context, "session_id");
			request.addHeader("Cookie",
					sessionID.substring(0, (sessionID.indexOf(";"))));
			HttpResponse httpResponse = (new TaskHttpClient()).client.execute(request);
			String result = EntityUtils.toString(httpResponse.getEntity());
			JSONObject json = new JSONObject(result);

			if (result.equals("") && result.length() == 0) {
				flag = CommonConstant.OTHER_FAIL;
			} else {
				dishesInfo.preferHistory = json.getString("params");
				flag = CommonConstant.SUCCESS;
			}

		} catch (Exception e) {
			e.printStackTrace();
			flag=CommonConstant.OTHER_FAIL;
		} finally {
		}
		return flag;

	}

	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		if (handler != null) {
			message = new Message();
			message.obj = dishesInfo;
			message.what = result;
			handler.sendMessage(message);
		}
	}
}
