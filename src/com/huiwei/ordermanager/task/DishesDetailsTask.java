/*****************************************************
 * Copyright(c)2014-2015 北京汇为永兴科技有限公司
 * DishesDetailsTask.java
 * 创建人：高亚妮
 * 日     期：2014-6-24
 * 描     述：获取订单详情线程
 * 版     本：v6.0
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
import com.huiwei.ordermanager.info.OrderedDishesInfo;
import com.huiwei.ordermanager.info.PreferInfo;

public class DishesDetailsTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Message message = null;
	int flag = 0;
	String id = null;
	boolean isTakeOutOrder;

	public DishesDetailsTask(Context context, Handler handler, String id, boolean isTakeOutOrder) {
		this.context = context;
		this.handler = handler;
		this.id = id;
		this.isTakeOutOrder = isTakeOutOrder;
	}

	/*****************************************************
	 * 函数名：doInBackground
	 * 输     入：String... params -- 输入参数列表
	 * 输     出：Integer -- 执行结果
	 * 描     述：线程执行过程中调用：与服务器交互获取指定订单的详情
	 * 创建人：高亚妮
	 * 日     期：2014-6-24
	 *****************************************************/
	@Override
	protected Integer doInBackground(String... params) {

		String url = "" + params[0];
		try {

			HttpPost request = new HttpPost(url);
			JSONObject param = new JSONObject();
			param.put("orderid", id);
			param.put("waimai", isTakeOutOrder ? 1 : 0);
			JSONObject ep = new JSONObject();
			ep.put("ver", "1.0");
			String serial = MD5.getRandomString(32);
			ep.put("serial", serial);
			ep.put("params", param);
			ep.put("method", "getorderbyid");
			ep.put("auth", MD5.md5s(serial + MD5.md5s("getorderbyid")));

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
				SysApplication.curOrderInfo.clearDishesList();
				String ver = result.getString("ver");
				JSONObject jsonPhone = result.getJSONObject("params");
				if (!"null".equals(jsonPhone.toString())
						&& jsonPhone.length() != 0) {
					JSONArray jsonarray = jsonPhone.getJSONArray("menus");
					for (int i = 0; i < jsonarray.length(); i++) {
						JSONObject ls = jsonarray.getJSONObject(i);
						OrderedDishesInfo dishesInfo = new OrderedDishesInfo();
						dishesInfo.orderNum = ls.getString("count");
						dishesInfo.dishes.name = ls.getString("menuname");
						dishesInfo.dishes.price = ls.getString("menuprice");
						dishesInfo.dishes.vipPrice = ls.getString("vipprice");
						dishesInfo.dishes.islimit = ls.getString("islimit");
						dishesInfo.dishes.id = ls.getString("menuid");
						dishesInfo.dishes.isSoldOut = ls.getInt("soldout") == 1;
						dishesInfo.dishes.preferHistory = ls.getString("preference");
						dishesInfo.dishes.otherPrefer = ls.getString("other_prefer");
						if (dishesInfo.dishes.id.equals("0"))
							dishesInfo.isInput = true;
		
						for (PreferInfo preferInfo : SysApplication.dishesList.get(dishesInfo.dishes.id).preferList) {
							PreferInfo info = new PreferInfo();
							info.id = preferInfo.id;
							info.name = SysApplication.preferList.get(preferInfo.id).name;
							dishesInfo.dishes.preferList.add(info);
						}
						
						JSONArray jsonPrefers = ls.getJSONArray("phids");
						for (int j=0; j<jsonPrefers.length(); j++) {
							int preferId = jsonPrefers.getInt(j);
							dishesInfo.dishes.setPreferCheck(preferId);
						}
						
						SysApplication.curOrderInfo.dishesInfo.add(dishesInfo);
					}
					
					SysApplication.curOrderInfo.content = jsonPhone.getString("orderid");
					SysApplication.curOrderInfo.tableId = jsonPhone.getString("tableid");	
					SysApplication.curOrderInfo.notes = jsonPhone.getString("order_text");
					
					if (isTakeOutOrder) {
						SysApplication.curOrderInfo.contactName = jsonPhone.getString("customer");
						SysApplication.curOrderInfo.contactNumber = jsonPhone.getString("tel");
						SysApplication.curOrderInfo.contactAddress = jsonPhone.getString("addr");
						SysApplication.curOrderInfo.sendType = jsonPhone.getString("mode");
						SysApplication.curOrderInfo.sendTime = jsonPhone.getString("sendtime");
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
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		message = new Message();
		message.what = result;
		message.obj = SysApplication.curOrderInfo;
		handler.sendMessage(message);
	}
}
