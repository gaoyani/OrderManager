/*****************************************************
 * Copyright(c)2014-2015 北京汇为永兴科技有限公司
 * SyncOrderTimerTask.java
 * 创建人：高亚妮
 * 日     期：2014-6-24
 * 描     述：订单同步定时器线程
 * 版     本：v6.0
 *****************************************************/

package com.huiwei.ordermanager.task;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.huiwei.ordermanager.constant.UrlConstant;

public class SyncOrderTimerTask extends AsyncTask<String, Void, Integer> {
	Context context = null;
	Handler handler = null;
	Timer timer;

	public SyncOrderTimerTask(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	/*****************************************************
	 * 函数名：doInBackground
	 * 输     入：String... params -- 输入参数列表
	 * 输     出：Integer -- 执行结果
	 * 描     述：线程执行过程中调用：创建定时器（每5s执行一次同步订单线程）
	 * 创建人：高亚妮
	 * 日     期：2014-6-24
	 *****************************************************/
	@Override
	protected Integer doInBackground(String... params) {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					SyncOrderTask at = new SyncOrderTask(context, handler);
					at.execute(UrlConstant.getServerUrl(context));

				} catch (Exception e) {
					e.printStackTrace();
				}
				
				Log.d("SyncOrderService", "update order");
			}
		}, 0, 1000*5);
		
		return null;
	}

	/*****************************************************
	 * 函数名：doInBackground
	 * 输     入：无
	 * 输     出：无
	 * 描     述：停止同步订单定时器
	 * 创建人：高亚妮
	 * 日     期：2014-6-24
	 *****************************************************/
	public void stopTimer() {
		if (timer != null) {
			timer.cancel();
		}
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
	}

}
