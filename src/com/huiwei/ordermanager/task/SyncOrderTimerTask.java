/*****************************************************
 * Copyright(c)2014-2015 ������Ϊ���˿Ƽ����޹�˾
 * SyncOrderTimerTask.java
 * �����ˣ�������
 * ��     �ڣ�2014-6-24
 * ��     ��������ͬ����ʱ���߳�
 * ��     ����v6.0
 *****************************************************/

package com.huiwei.ordermanager.task;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.huiwei.ordermanager.activity.MainActivity;
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
	 * ������doInBackground
	 * ��     �룺String... params -- ��������б�
	 * ��     ����Integer -- ִ�н��
	 * ��     �����߳�ִ�й���е��ã�������ʱ����ÿ5sִ��һ��ͬ�������̣߳�
	 * �����ˣ�������
	 * ��     �ڣ�2014-6-24
	 *****************************************************/
	@Override
	protected Integer doInBackground(String... params) {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					SyncOrderTask at = new SyncOrderTask(context, handler);
//					at.execute(UrlConstant.getServerUrl(context));
					at.executeOnExecutor(MainActivity.FULL_TASK_EXECUTOR, UrlConstant.getServerUrl(context));

				} catch (Exception e) {
					e.printStackTrace();
				}
				
				Log.d("SyncOrderService", "update order");
			}
		}, 0, 1000*10);
		
		return null;
	}

	/*****************************************************
	 * ������doInBackground
	 * ��     �룺��
	 * ��     ������
	 * ��     ����ֹͣͬ��������ʱ��
	 * �����ˣ�������
	 * ��     �ڣ�2014-6-24
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
