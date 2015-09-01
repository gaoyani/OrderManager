package com.huiwei.ordermanager.task;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.huiwei.commonlib.MD5;
import com.huiwei.commonlib.Preferences;
import com.huiwei.ordermanager.R;
import com.huiwei.ordermanager.common.SysApplication;
import com.huiwei.ordermanager.constant.CommonConstant;
import com.huiwei.ordermanager.info.CategoryInfo;
import com.huiwei.ordermanager.info.PreferInfo;

public class CategoryTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	int flag = 0;

	public CategoryTask(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	@Override
	protected Integer doInBackground(String... params) {

		String url = "" + params[0];
		try {

			HttpPost request = new HttpPost(url);
			JSONObject ep = new JSONObject();
			ep.put("ver", "1.0");
			String serial = MD5.getRandomString(32);
			ep.put("serial", serial);
			ep.put("method", "typelist");
			ep.put("auth", MD5.md5s(serial + MD5.md5s("typelist")));
			StringEntity se = new StringEntity(ep.toString());
			request.setEntity(se);
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
				
				JSONObject jsonObject = result.getJSONObject("params");
				if (!"null".equals(jsonObject.toString())
						&& jsonObject.length() != 0) {
					JSONArray jsonCategory = jsonObject.getJSONArray("typelist");
					SysApplication.categoryList.clear();
					CategoryInfo infoAll = new CategoryInfo();
					infoAll.id = 0;
					infoAll.name = context.getResources().getString(R.string.all);
					SysApplication.categoryList.add(infoAll);
					
					for (int j = 0; j < jsonCategory.length(); j++) {
						JSONObject ls = jsonCategory.getJSONObject(j);
						CategoryInfo info = new CategoryInfo();
						info.id = ls.getInt("id");
						info.name = ls.getString("name");
						SysApplication.categoryList.add(info);
					}
					
					JSONArray jsonPrefer = jsonObject.getJSONArray("phlist");
					SysApplication.preferList.clear();					
					for (int j = 0; j < jsonPrefer.length(); j++) {
						JSONObject ls = jsonPrefer.getJSONObject(j);
						PreferInfo info = new PreferInfo();
						info.id = ls.getInt("id");
						info.name = ls.getString("name");
						SysApplication.preferList.put(info.id, info);
					}

					flag = CommonConstant.SUCCESS;
				} else {
					flag = CommonConstant.OTHER_FAIL;
				}
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
			message.what = result;
			handler.sendMessage(message);
		}
	}
}
