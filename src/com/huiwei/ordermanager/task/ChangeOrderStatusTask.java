package com.huiwei.ordermanager.task;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.huiwei.commonlib.MD5;
import com.huiwei.commonlib.Preferences;
import com.huiwei.ordermanager.common.SysApplication;
import com.huiwei.ordermanager.constant.CommonConstant;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

public class ChangeOrderStatusTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	int flag = 0;
	int id = 0;
	boolean isTakeOutOrder;
	int status = 0;

	public ChangeOrderStatusTask(Context context, Handler handler, int id, boolean isTakeOutOrder, int status) {
		this.context = context;
		this.handler = handler;
		this.id = id;
		this.isTakeOutOrder = isTakeOutOrder;
		this.status = status;
	}

	@Override
	protected Integer doInBackground(String... params) {
		String url = "" + params[0];
		try {
			HttpPost request = new HttpPost(url);
			JSONObject param = new JSONObject();
			JSONObject ep = new JSONObject();

			param.put("user_id", Preferences.GetString(context, "user_id"));
			param.put("id", id);
			param.put("waimai", isTakeOutOrder ? 1 : 0);
			param.put("status", status);

			ep.put("ver", "1.0");
			String serial = MD5.getRandomString(32);
			ep.put("serial", serial);
			ep.put("params", param);
			ep.put("method", "setinfostatus");
			ep.put("auth", MD5.md5s(serial + MD5.md5s("setinfostatus")));

			request.setEntity(new StringEntity(ep.toString()));
			String sessionID = Preferences.GetString(context, "session_id");
			request.addHeader("Cookie",
					sessionID.substring(0, (sessionID.indexOf(";"))));
			HttpResponse httpResponse = (new TaskHttpClient()).client.execute(request);
			String retSrc = EntityUtils.toString(httpResponse.getEntity());
			JSONObject result = new JSONObject(retSrc);

			if (result.equals("") && result.length() == 0) {
				flag = CommonConstant.CONNECT_SERVER_FAIL;
			} else {
				if ("success".equals(result.getString("params"))) {
					Preferences.SetString(context, "sessionID", "");
					flag = CommonConstant.SUCCESS;
				} else if ("opered".equals(result.getString("params"))) { //order completed
					flag = CommonConstant.ORDER_COMPLATE;
				} else {
					flag = CommonConstant.OTHER_FAIL;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			flag = CommonConstant.OTHER_FAIL;
		}
		return flag;

	}

	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		message = new Message();
		message.arg1 = status;
		message.what = result;
		handler.sendMessage(message);
	}
}
