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
import android.util.Log;

import com.huiwei.commonlib.MD5;
import com.huiwei.commonlib.Preferences;
import com.huiwei.ordermanager.common.SysApplication;
import com.huiwei.ordermanager.constant.CommonConstant;

public class LoginTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	private String userName = null;
	private String passWrod = null;
	String isStr = "";

	int flag = 0;

	public LoginTask(Context context, Handler handler, String username,
			String password) {
		this.context = context;
		this.handler = handler;
		this.userName = username;
		this.passWrod = password;
	}

	@Override
	protected Integer doInBackground(String... params) {
		String url = "" + params[0];

		try {
			HttpPost request = new HttpPost(url);
			JSONObject param = new JSONObject();
			param.put("username", userName);
			param.put("password", passWrod);

			JSONObject ep = new JSONObject();
			ep.put("ver", "1.0");
			String serial = MD5.getRandomString(32);
			ep.put("serial", serial);
			ep.put("params", param);
			ep.put("method", "login");
			ep.put("auth", MD5.md5s(serial + MD5.md5s("login")));

			request.setEntity(new StringEntity(ep.toString()));
			request.addHeader("Content-Type", "application/json"); 

			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);

			String retSrc = EntityUtils.toString(httpResponse.getEntity());
			Log.d("login", retSrc);
			JSONObject result = new JSONObject(retSrc);
			String sessionID = httpResponse.getFirstHeader("Set-Cookie").getValue();

			if (result.equals("") && result.length() == 0) {
				flag = CommonConstant.OTHER_FAIL;
			} else {
				JSONObject jsonPhone = result.getJSONObject("params");
				String resultFlag = jsonPhone.getString("loginflag");
				if ("success".equals(resultFlag)) {
					Preferences.SetString(context, "session_id", sessionID);
					Preferences.SetString(context, "user_id", 
							String.valueOf(jsonPhone.getInt("waiterid")));
					flag = CommonConstant.SUCCESS;
				} else if ("logined".equals(resultFlag)){
					flag = CommonConstant.LOGIN_FAIL_LOGINED;
				} else {
					flag = CommonConstant.OTHER_FAIL;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			flag = CommonConstant.EXCEPTION_FAIL;
		}
		return flag;
	}

	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		message = new Message();
		message.what = result;
		handler.sendMessage(message);
	}
}
