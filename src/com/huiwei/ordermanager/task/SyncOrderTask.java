/*****************************************************
 * Copyright(c)2014-2015 北京汇为永兴科技有限公司
 * SyncOrderTask.java
 * 创建人：高亚妮
 * 日     期：2014-6-24
 * 描     述：订单同步线程
 * 版     本：v6.0
 *****************************************************/

package com.huiwei.ordermanager.task;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;

import com.huiwei.commonlib.CommonFunction;
import com.huiwei.commonlib.MD5;
import com.huiwei.commonlib.Preferences;
import com.huiwei.ordermanager.R;
import com.huiwei.ordermanager.activity.MainActivity;
import com.huiwei.ordermanager.common.SysApplication;
import com.huiwei.ordermanager.constant.CommonConstant;
import com.huiwei.ordermanager.info.OrderInfo;

public class SyncOrderTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	Cursor cursor = null;
	int flag = 0;
	Vibrator vibrator = null;

	public SyncOrderTask(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}
	
	/*****************************************************
	 * 函数名：doInBackground
	 * 输     入：String... params -- 输入参数列表
	 * 输     出：Integer -- 执行结果
	 * 描     述：线程执行过程中调用：与服务器交互获取订单列表
	 * 创建人：高亚妮
	 * 日     期：2014-6-24
	 *****************************************************/
	@Override
	protected Integer doInBackground(String... params) {

		String url = "" + params[0];
		try {
			HttpPost request = new HttpPost(url);
			JSONObject param = new JSONObject();
			param.put("user_id", Preferences.GetString(context, "user_id"));
			param.put("mac", CommonFunction.getLocalMacAddress(context));
			
			JSONObject ep = new JSONObject();
			ep.put("ver", "1.0");
			String serial = MD5.getRandomString(32);
			ep.put("serial", serial);
			ep.put("params", param);
			ep.put("method", "listorder");
			ep.put("auth", MD5.md5s(serial + MD5.md5s("listorder")));

			request.setEntity(new StringEntity(ep.toString()));
			String sessionID = Preferences.GetString(context, "session_id");
			request.addHeader("Cookie",
					sessionID.substring(0, (sessionID.indexOf(";"))));
			HttpResponse httpResponse = (new TaskHttpClient()).client.execute(request);
			String retSrc = EntityUtils.toString(httpResponse.getEntity());
			JSONObject result = new JSONObject(retSrc);

			int oldNewNum = SysApplication.getNewOrderNum();
			SysApplication.clearOrderList();
			String curTableID = "";
			if (result.getString("params").length() != 0) {
				JSONObject jsonPhone = result.getJSONObject("params");
				JSONArray jsonarray = jsonPhone.getJSONArray("orders");
				int curNewNum = 0;
				boolean hasNewOrder = false;
				for (int i = 0; i < jsonarray.length(); i++) {
					OrderInfo orderInfo = new OrderInfo();
					JSONObject ls = jsonarray.getJSONObject(i);
					orderInfo.id = ls.getInt("id");
					orderInfo.content = ls.getString("content");
					orderInfo.tableId = ls.getString("table_id");
					orderInfo.tableName = ls.getString("table_name");
					orderInfo.peopleNum = ls.getInt("people_num");
					orderInfo.type = ls.getInt("type");
					orderInfo.status = ls.getInt("status");
					orderInfo.isVip = (ls.getInt("is_vip") == 1);
					orderInfo.isTakeOutOrder = (ls.getInt("waimai") == 1);
					
					if (orderInfo.type == OrderInfo.TYPE_ORDER && orderInfo.status == OrderInfo.CONFIRM) {
						if (curTableID.compareTo(orderInfo.tableId) != 0) {
							List<OrderInfo> orderList = new ArrayList<OrderInfo>();
							orderList.add(orderInfo);
							SysApplication.confirmOrderList.put(orderInfo.tableId, orderList);
							SysApplication.confirmTableIDList.add(orderInfo.tableId);
							curTableID = orderInfo.tableId;
						} else {
							SysApplication.confirmOrderList.get(orderInfo.tableId).add(orderInfo);
						}
//						SysApplication.confirmOrderList.add(orderInfo);
					} else {
						SysApplication.newOrderList.add(orderInfo);
					}
					
					if (orderInfo.status == OrderInfo.NEW) {
						hasNewOrder = true;
						curNewNum++;
					}
				}
				
				if (hasNewOrder) {
					tipAction();
					if (curNewNum > oldNewNum && CommonFunction.isBackground(context)) {
						addNotification();
					}
				}
			}

			flag = CommonConstant.SUCCESS;

		} catch (Exception e) {
			e.printStackTrace();
			flag = CommonConstant.OTHER_FAIL;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return flag;

	}
	
	/*****************************************************
	 * 函数名：tipAction
	 * 输     入：无
	 * 输     出：无
	 * 描     述：播放提示音
	 * 创建人：高亚妮
	 * 日     期：2014-6-24
	 *****************************************************/
	private void tipAction() {
		if (vibrator == null) {
			vibrator = (Vibrator) context
					.getSystemService(Context.VIBRATOR_SERVICE);
		}
		long[] pattern = { 100, 400, 100, 400 };
		vibrator.vibrate(pattern, -1);

		Uri notification = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Ringtone r = RingtoneManager.getRingtone(context, notification);
		r.play();
	}
	
	private void addNotification() {
		NotificationManager nm = (NotificationManager)context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		//实例化Notification
		Notification n = new Notification();
		//设置提示信息
		String tickerText ="无线点餐";
		//显示时间
		long when = System.currentTimeMillis();
		 
		n.icon = R.drawable.icon_notification;
		n.tickerText = tickerText;
		n.when = when;
		//显示在“正在进行中”
		//  n.flags = Notification.FLAG_ONGOING_EVENT;
		n.flags|=Notification.FLAG_AUTO_CANCEL|Notification.FLAG_INSISTENT; //自动终止
		//实例化Intent
		Intent it = new Intent(context, MainActivity.class);
		
		PendingIntent pi = PendingIntent.getActivity(context, 0, it, PendingIntent.FLAG_UPDATE_CURRENT);
		
		//设置事件信息，显示在拉开的里面
		n.setLatestEventInfo(context,"无线点餐", "有新订单", pi);
	 
		//发出通知
		nm.notify(1111,n);
	}

	/*****************************************************
	 * 函数名：onPostExecute
	 * 输     入：Integer result -- 执行结果
	 * 输     出：无
	 * 描     述：线程执行完毕后调用
	 * 创建人：高亚妮
	 * 日     期：2014-6-24
	 *****************************************************/
	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);

		if (handler != null) {
			message = new Message();
			message.what = result;
			handler.sendMessage(message);
		}
	}
}
