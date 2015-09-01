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

public class LogoutTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	int flag = 0;

	public LogoutTask(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;

	}

	@Override
	protected Integer doInBackground(String... params) {
		String url = "" + params[0];
		try {
			HttpPost request = new HttpPost(url);
			JSONObject param = new JSONObject();
			JSONObject ep = new JSONObject();
			param.put("user_id", Preferences.GetString(context, "user_id"));

			ep.put("ver", "1.0");
			String serial = MD5.getRandomString(32);
			ep.put("serial", serial);
			ep.put("params", param);
			ep.put("method", "logout");
			ep.put("auth", MD5.md5s(serial + MD5.md5s("logout")));

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
				JSONObject jsonPhone = result.getJSONObject("params");
				String loginflag = jsonPhone.getString("logoutflag");
				if ("success".equals(loginflag.toString())) {
					Preferences.SetString(context, "sessionID", "");
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
		if (handler != null) {
			message = new Message();
			message.what = result;
			handler.sendMessage(message);
		}
	}
}
