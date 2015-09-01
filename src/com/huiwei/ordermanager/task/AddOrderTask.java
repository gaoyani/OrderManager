package com.huiwei.ordermanager.task;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
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
import com.huiwei.ordermanager.info.OrderedDishesInfo;
import com.huiwei.ordermanager.info.PreferInfo;

public class AddOrderTask extends
		AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	int flag = 0;

	public AddOrderTask(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	@Override
	protected Integer doInBackground(String... params) {
		message = new Message();
		String url = "" + params[0];
		try {
			HttpPost request = new HttpPost(url);
			JSONObject param = new JSONObject();
			param.put("user_id", Preferences.GetString(context, "user_id"));
			param.put("table_id", SysApplication.curOrderInfo.tableId);
			param.put("order_text", SysApplication.curOrderInfo.notes);
			param.put("is_vip", SysApplication.curOrderInfo.isVip ? 1 : 0);
			param.put("is_add_order", SysApplication.curOrderInfo.isAddOrder);
			JSONObject ep = new JSONObject();
			ep.put("ver", "1.0");
			String serial = MD5.getRandomString(32);
			ep.put("serial", serial);
			ep.put("params", param);
			ep.put("method", "addorder");
			ep.put("auth", MD5.md5s(serial + MD5.md5s("addorder")));

			JSONArray ar = new JSONArray();
			for (OrderedDishesInfo info : SysApplication.curOrderInfo.dishesInfo) {
				JSONObject am = new JSONObject();
				am.put("menuid", info.isInput ? 0 : info.dishes.id);
				am.put("name", info.dishes.name);
				am.put("price", info.dishes.price);
				am.put("vipprice", info.dishes.vipPrice);
				am.put("islimit", info.dishes.islimit);
				am.put("count", info.orderNum);
				JSONArray preferArray = new JSONArray();
				for (PreferInfo preferInfo : info.dishes.preferList){
					if (preferInfo.isChecked) {
						preferArray.put(preferInfo.id);
					}
				}
				am.put("phids", preferArray);
				am.put("other_prefer", info.dishes.otherPrefer);
				ar.put(am);
			}

			param.put("menus", ar);
			request.setEntity(new StringEntity(ep.toString(), HTTP.UTF_8));
			String sessionID = Preferences.GetString(context, "session_id");
			request.addHeader("Cookie",
					sessionID.substring(0, (sessionID.indexOf(";"))));
			HttpResponse httpResponse = new DefaultHttpClient()
					.execute(request);
			String retSrc = EntityUtils.toString(httpResponse.getEntity());
			JSONObject resultJson = new JSONObject(retSrc);

			String result = resultJson.getString("params").toString();
			if ("success".equals(result)) {
				flag = CommonConstant.UPLOADING_SUCCESS;
			} else {
				message.obj = result;
				flag=CommonConstant.UPLOADING_FAIL;
			}

		} catch (Exception e) {
			e.printStackTrace();
			flag=CommonConstant.UPLOADING_FAIL;
		}
		return flag;

	}

	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		message.what = result;
		handler.sendMessage(message);
	}
}
