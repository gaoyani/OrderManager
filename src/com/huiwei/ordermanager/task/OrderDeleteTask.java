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

public class OrderDeleteTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	int flag = 0;
	int id = 0;
	int status = 0;

	public OrderDeleteTask(Context context, Handler handler, int orderId) {
		this.context = context;
		this.handler = handler;
		this.id = orderId;
	}

	@Override
	protected Integer doInBackground(String... params) {
		String url = "" + params[0];
		try {
			HttpPost request = new HttpPost(url);
			JSONObject param = new JSONObject();
			param.put("order_id", id);

			JSONObject ep = new JSONObject();
			ep.put("ver", "1.0");
			String serial = MD5.getRandomString(32);
			ep.put("serial", serial);
			ep.put("params", param);
			ep.put("method", "orderrepeal");
			ep.put("auth", MD5.md5s(serial + MD5.md5s("orderrepeal")));

			request.setEntity(new StringEntity(ep.toString()));
			String sessionID = Preferences.GetString(context, "session_id");
			request.addHeader("Cookie",
					sessionID.substring(0, (sessionID.indexOf(";"))));
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);
			String retSrc = EntityUtils.toString(httpResponse.getEntity());
			JSONObject result = new JSONObject(retSrc);

			if (result.equals("") && result.length() == 0) {
				flag = CommonConstant.CONNECT_SERVER_FAIL;
			} else {
				if ("success".equals(result.getString("params"))) {
					flag = CommonConstant.SUCCESS;
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
