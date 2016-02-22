package com.huiwei.ordermanager.task;

import java.util.ArrayList;
import java.util.List;

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
import android.util.Log;

import com.huiwei.commonlib.MD5;
import com.huiwei.commonlib.Preferences;
import com.huiwei.ordermanager.common.SysApplication;
import com.huiwei.ordermanager.constant.CommonConstant;
import com.huiwei.ordermanager.info.OrderedDishesInfo;
import com.huiwei.ordermanager.info.PreferInfo;
import com.huiwei.ordermanager.info.TableInfo;

public class GetTableListTask extends
		AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	int flag = 0;
	String tableID;

	public GetTableListTask(Context context, Handler handler, String tableID) {
		this.context = context;
		this.handler = handler;
		this.tableID = tableID;
	}

	@Override
	protected Integer doInBackground(String... params) {
		message = new Message();
		String url = "" + params[0];
		try {
			HttpPost request = new HttpPost(url);
			JSONObject param = new JSONObject();
			param.put("table_id", tableID);
			JSONObject ep = new JSONObject();
			ep.put("ver", "1.0");
			String serial = MD5.getRandomString(32);
			ep.put("serial", serial);
			ep.put("params", param);
			ep.put("method", "tables");
			ep.put("auth", MD5.md5s(serial + MD5.md5s("tables")));
			
			request.setEntity(new StringEntity(ep.toString(), HTTP.UTF_8));
			String sessionID = Preferences.GetString(context, "session_id");
			request.addHeader("Cookie",
					sessionID.substring(0, (sessionID.indexOf(";"))));
			HttpResponse httpResponse = (new TaskHttpClient()).client.execute(request);
			String retSrc = EntityUtils.toString(httpResponse.getEntity());
			Log.d("tableList", retSrc);
			JSONObject jsonObject = new JSONObject(retSrc);
			JSONArray jsonArray = jsonObject.getJSONObject("params").getJSONArray("tables");
//			List<TableInfo> tableList = new ArrayList<TableInfo>();
			SysApplication.clearTableList();
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonTable = jsonArray.getJSONObject(i);
				TableInfo tableInfo = new TableInfo();
				tableInfo.id = jsonTable.getString("id");
				tableInfo.name = jsonTable.getString("name");
//				tableList.add(tableInfo);
				SysApplication.tableList.add(tableInfo);
			}
			
//			message.obj = tableList;
			flag = CommonConstant.SUCCESS;

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
			message.what = result;
			handler.sendMessage(message);
		}
	}
}
