
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
import android.util.Log;

import com.huiwei.commonlib.MD5;
import com.huiwei.commonlib.Preferences;
import com.huiwei.ordermanager.common.SysApplication;
import com.huiwei.ordermanager.constant.CommonConstant;
import com.huiwei.ordermanager.info.DishesInfo;
import com.huiwei.ordermanager.info.PreferInfo;

public class SoldOutDishesTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	int flag = 0;

	public SoldOutDishesTask(Context context, Handler handler) {
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
			ep.put("ver", "1.0");
			String serial = MD5.getRandomString(32);
			ep.put("serial", serial);
			ep.put("params", param);
			ep.put("method", "soldoutfood");
			ep.put("auth", MD5.md5s(serial + MD5.md5s("soldoutfood")));

			request.setEntity(new StringEntity(ep.toString()));
			String sessionID = Preferences.GetString(context, "session_id");
			request.addHeader("Cookie",
					sessionID.substring(0, (sessionID.indexOf(";"))));
			HttpResponse httpResponse = (new TaskHttpClient()).client.execute(request);
			String retSrc = EntityUtils.toString(httpResponse.getEntity());
			Log.d("SoldOutDishes", retSrc);
			
			if (retSrc.equals("") && retSrc.length() == 0) {
				flag = CommonConstant.CONNECT_SERVER_FAIL;
			} else {
				SysApplication.resetSoldOut();
				JSONObject result = new JSONObject(retSrc);
				JSONObject jsonParams = result.getJSONObject("params");
				if (!jsonParams.getString("menulist").equals("")) {
					String[] soldOutIDs = jsonParams.getString("menulist").split(",");
					for (int i=0; i<soldOutIDs.length; i++) {
						SysApplication.dishesList.get(soldOutIDs[i]).isSoldOut = true;
					}
				}

				flag = CommonConstant.SUCCESS;
			}
		} catch (Exception e) {
			e.printStackTrace();
			flag=CommonConstant.OTHER_FAIL;
		} finally {
		}
		return flag;

	}

	/*****************************************************
	 * ������onPostExecute
	 * ��     �룺Integer result -- ִ�н��
	 * ��     ������
	 * ��     �����߳�ִ����Ϻ����
	 * �����ˣ�������
	 * ��     �ڣ�2014-6-24
	 *****************************************************/
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
