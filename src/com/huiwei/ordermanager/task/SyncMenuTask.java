/*****************************************************
 * Copyright(c)2014-2015 ������Ϊ���˿Ƽ����޹�˾
 * SyncMenuTask.java
 * �����ˣ�������
 * ��     �ڣ�2014-6-24
 * ��     �����˵�ͬ���߳�
 * ��     ����v6.0
 *****************************************************/

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
import com.huiwei.ordermanager.common.SysApplication;
import com.huiwei.ordermanager.constant.CommonConstant;
import com.huiwei.ordermanager.info.DishesInfo;
import com.huiwei.ordermanager.info.PreferInfo;

public class SyncMenuTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	int flag = 0;

	public SyncMenuTask(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	/*****************************************************
	 * ��������doInBackground
	 * ��     �룺String... params -- ��������б�
	 * ��     ����Integer -- ִ�н��
	 * ��     �����߳�ִ�й����е��ã��������������ȡ�˵��б�
	 * �����ˣ�������
	 * ��     �ڣ�2014-6-24
	 *****************************************************/
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
			ep.put("method", "rsyncmenu");
			ep.put("auth", MD5.md5s(serial + MD5.md5s("rsyncmenu")));

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
				String ver = result.getString("ver");
				JSONObject jsonPhone = result.getJSONObject("params");

				if (!"null".equals(jsonPhone.toString())
						&& jsonPhone.length() != 0) {

					SysApplication.searchDishesIDList.clear();
					JSONArray jsonarray = jsonPhone.getJSONArray("menulist");
					for (int i = 0; i < jsonarray.length(); i++) {
						JSONObject ls = jsonarray.getJSONObject(i);
						DishesInfo dishesInfo = new DishesInfo();
						dishesInfo.id = ls.getString("menuid");
						dishesInfo.name = ls.getString("menuname");
						dishesInfo.price = ls.getString("menuprice");
						dishesInfo.category = Integer.valueOf(ls.getString("groupid"));
						dishesInfo.isSoldOut = ls.getInt("soldout") == 0 ? false : true;
						dishesInfo.vipPrice = ls.getString("vipprice");
						dishesInfo.islimit = ls.getString("islimit");
						
						dishesInfo.quanpin = SysApplication.getFullPinYin(dishesInfo.name);
						dishesInfo.jianpin = SysApplication.getFirstPinYin(dishesInfo.name);
						
						JSONArray jsonPrefers = ls.getJSONArray("phids");
						for (int j=0; j<jsonPrefers.length(); j++) {
							PreferInfo preferInfo = new PreferInfo();
							preferInfo.id = jsonPrefers.getInt(j);
							dishesInfo.preferList.add(preferInfo);
						}
						
						SysApplication.dishesList.put(dishesInfo.id, dishesInfo);
						SysApplication.searchDishesIDList.add(dishesInfo.id);
					}

					flag = CommonConstant.SUCCESS;
				} else {
					flag = CommonConstant.CONNECT_SERVER_FAIL;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			flag=CommonConstant.OTHER_FAIL;
		} finally {
		}
		return flag;

	}

	/*****************************************************
	 * ��������onPostExecute
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
