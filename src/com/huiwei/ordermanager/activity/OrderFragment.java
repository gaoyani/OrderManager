/*****************************************************
 * Copyright(c)2014-2015 北京汇为永兴科技有限公司
 * OrderFragment.java
 * 创建人：高亚妮
 * 日     期：2014-6-20
 * 描     述：订单页面显示及操作文件
 * 版     本：v6.0
 *****************************************************/
package com.huiwei.ordermanager.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huiwei.ordermanager.R;
import com.huiwei.ordermanager.adapter.OrderItemAdapter;
import com.huiwei.ordermanager.baseclass.BaseFragment;
import com.huiwei.ordermanager.common.SysApplication;
import com.huiwei.ordermanager.constant.CommonConstant;
import com.huiwei.ordermanager.constant.UrlConstant;
import com.huiwei.ordermanager.info.OrderInfo;
import com.huiwei.ordermanager.info.TableInfo;
import com.huiwei.ordermanager.task.GetTableListTask;
import com.huiwei.ordermanager.task.SyncOrderTimerTask;
import com.huiwei.ordermanager.view.LoadingProgressView;

public class OrderFragment extends BaseFragment implements OnClickListener {
	
	public static final int UNCONFIRM_LIST = 0;
	public static final int CONFIRM_LIST = 1;
	
	private ListView orderListView;
	private TextView groupConfirm, newNum;
	private ImageView groupUnconfirmBL, groupConfirmBL;
	private ImageView plusBtn;
	private RelativeLayout groupUnconfirm;
	private LoadingProgressView pbView;
	private OrderItemAdapter adapter;
	private Timer updateTimer;
	private boolean isOrderUpdate = false;
	private SyncOrderTimerTask sotTask;
	private int type = UNCONFIRM_LIST;

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_order, container, false);
	
		pbView = (LoadingProgressView)view.findViewById(R.id.loading_view);
		pbView.setHandler(handlerSyncOrder);
		
		((ImageView)view.findViewById(R.id.iv_menu)).setOnClickListener(this);
		plusBtn = ((ImageView)view.findViewById(R.id.iv_plus));
		plusBtn.setOnClickListener(this);
		
		initGroupBtns(view);
		initListView(view);
		
		startSyncOrderTimer();
		
		return view;
	}
	
	@Override
	public void onResume() {
//		startSyncOrderTimer();
		super.onResume();
	}
	
	@Override
	public void onPause() {
//		stopSyncOrder();
		super.onPause();
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
//		if (hidden) {
//			stopSyncOrder();
//		} else {
//			startSyncOrderTimer();
//		}
		
		Log.d("OrderFragment", "onHiddenChanged");
		Log.d("hidden", hidden ? "true" : "false");
		super.onHiddenChanged(hidden);
	}
	
	/*****************************************************
	 * 函数名：initGroupBtns
	 * 输     入：View view -- 页面对象实例
	 * 输     出：无
	 * 描     述：初始化tab按钮
	 * 调用接口：onCreateView
	 * 创建人：高亚妮
	 * 日     期：2014-6-20
	 *****************************************************/
	private void initGroupBtns(View view) {
		groupConfirm = (TextView)view.findViewById(R.id.tv_confirm);
		groupConfirm.setOnClickListener(this);
		groupUnconfirm = (RelativeLayout)view.findViewById(R.id.layout_unconfirm);
		groupUnconfirm.setOnClickListener(this);
		
		newNum = (TextView)view.findViewById(R.id.tv_new_num);
		groupUnconfirmBL = (ImageView)view.findViewById(R.id.iv_unconfirm);
		groupConfirmBL = (ImageView)view.findViewById(R.id.iv_confirm);
	}
	
	/*****************************************************
	 * 函数名：startSyncOrderTimer
	 * 输     入：无
	 * 输     出：无
	 * 描     述：启动订单同步线程计时器
	 * 调用接口：onCreateView
	 * 创建人：高亚妮
	 * 日     期：2014-6-20
	 *****************************************************/
	private void startSyncOrderTimer() {
		sotTask = new SyncOrderTimerTask(getActivity(), handlerSyncOrder);
		sotTask.execute(UrlConstant.getServerUrl(getActivity()));
		Log.d("OrderFragment", "startSyncOrderTimer");
	}
	
	//停止订单同步线程计时器
	private void stopSyncOrder() {
		Log.d("OrderFragment", "stopSyncOrder");
		if (!sotTask.isCancelled()) {
			sotTask.stopTimer();
			sotTask.cancel(true);
			sotTask = null;
		}
	}

	//同步订单线程消息处理
	Handler handlerSyncOrder = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			if (msg.what == LoadingProgressView.RELOADING) {
				startSyncOrderTimer();
				return;
			}
			
			pbView.hideView();
			if (msg.what == CommonConstant.SUCCESS) {
				updateListView();
				isOrderUpdate = true;
			} else {
				if (isOrderUpdate) {
					Toast.makeText(getActivity(), getResources().
							getString(R.string.order_update_fail), Toast.LENGTH_SHORT).show();
				} else {
					stopSyncOrder();
					isOrderUpdate = true;
					pbView.showLoadingFailed();
				}
			}
		}
	};
	
	/*****************************************************
	 * 函数名：initListView
	 * 输     入：View view -- 页面对象实例
	 * 输     出：无
	 * 描     述：初始化订单列表
	 * 调用接口：onCreateView
	 * 创建人：高亚妮
	 * 日     期：2014-6-20
	 *****************************************************/
	private void initListView(View view) {
		orderListView = (ListView)view.findViewById(R.id.lv_order_info);
		adapter = new OrderItemAdapter(getActivity(), handlerStatus, handlerDelete);
		orderListView.setAdapter(adapter);
		orderListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				OrderInfo info = type == UNCONFIRM_LIST ? SysApplication.newOrderList.get(position) : 
					SysApplication.confirmOrderList.get(position);
				if (info.type == OrderInfo.TYPE_CALLING || 
						(info.type == OrderInfo.TYPE_ORDER && info.status == OrderInfo.NEW))
					return;
				
				SysApplication.curOrderInfo.status = info.status;
				SysApplication.curOrderInfo.isVip = info.isVip;
				SysApplication.curOrderInfo.tableId = info.tableId;
				SysApplication.curOrderInfo.tableName = info.tableName;
				SysApplication.curOrderInfo.content = info.content;
				SysApplication.curOrderInfo.isTakeOutOrder = info.isTakeOutOrder;
				
				Intent intent = getActivity().getIntent();
				intent.setClass(getActivity(), OrderDetailActivity.class);
				getActivity().startActivity(intent);
			}
		});
	}
	
	//订单状态更改消息处理
	private Handler handlerStatus = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			int status = msg.arg1;
			String statusStr = getResources().getString(status == OrderInfo.ACCEPT ?
					R.string.accept : R.string.confirm);
			
			if (msg.what == CommonConstant.SUCCESS) {
				stopSyncOrder();
				startSyncOrderTimer();
				Toast.makeText(getActivity(),  statusStr + getResources().
						getString(R.string.success), Toast.LENGTH_SHORT).show();
			} else if (msg.what == CommonConstant.ORDER_COMPLATE){
				Toast.makeText(getActivity(), statusStr + getResources().
						getString(R.string.order_completed), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), statusStr + getResources().
						getString(R.string.failed), Toast.LENGTH_SHORT).show();
			}
		};
	};
	
	//订单删除消息处理
	private Handler handlerDelete = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == CommonConstant.SUCCESS) {
				stopSyncOrder();
				startSyncOrderTimer();
				Toast.makeText(getActivity(), getResources().getString(
						R.string.order_delete_success), Toast.LENGTH_SHORT).show();
			} else if (msg.what == CommonConstant.OTHER_FAIL){
				Toast.makeText(getActivity(), getResources().getString(
						R.string.order_delete_fail), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), getResources().getString(
						R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
			}
		};
	};
	
	//更新订单列表
	public void updateListView() {		
		resetOrderNum();
		adapter.setListType(type);
		adapter.setData(SysApplication.newOrderList, SysApplication.confirmOrderList);
	}
	
	//统计并显示未处理订单数
	private void resetOrderNum() {
		int num = 0;
		for (OrderInfo info : SysApplication.newOrderList) {
			if ((info.type == OrderInfo.TYPE_ORDER && 
					(info.status == OrderInfo.NEW || info.status == OrderInfo.ACCEPT)) || 
					info.type == OrderInfo.TYPE_CALLING) {
				num++;
			}
		}
		
		newNum.setText(String.valueOf(num));
		newNum.setVisibility(num == 0 ? View.GONE : View.VISIBLE);
	}
	
	//按钮的点击消息监听
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_menu:
			listener.onMenuPop();
			break;
			
		case R.id.iv_plus: {
			GetTableListTask task = new GetTableListTask(getActivity(), 
					tableListHandler, "");
			task.execute(UrlConstant.getServerUrl(getActivity()));
			plusBtn.setOnClickListener(null);
		}
			break;
			
		case R.id.tv_confirm:
		case R.id.layout_unconfirm: {
			type = UNCONFIRM_LIST;
			if (v.getId() == R.id.tv_confirm) {
				type = CONFIRM_LIST;
			} 
			
			resetTypeBtn();
			updateListView();
		}
			break;

		default:
			break;
		}
	}
	
	//设置tab按钮状态
	private void resetTypeBtn() {
		groupUnconfirmBL.setBackgroundColor(getResources().getColor(type == UNCONFIRM_LIST ? 
				R.color.selected : R.color.order_group_bl_color));
		groupConfirmBL.setBackgroundColor(getResources().getColor(type == CONFIRM_LIST ? 
				R.color.selected : R.color.order_group_bl_color));
	}
	
	private Handler tableListHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			if (msg.what == CommonConstant.SUCCESS) {
				@SuppressWarnings("unchecked")
				List<TableInfo> tableList = (List<TableInfo>)msg.obj;
				if (tableList.size() == 0) {
					Toast.makeText(getActivity(), R.string.order_table_all_used, 
							Toast.LENGTH_SHORT).show();
					plusBtn.setOnClickListener(OrderFragment.this);
				} else {
					addNewOrder(tableList);
				}
			}
		};
	};
	
	/*****************************************************
	 * 函数名：addNewOrder
	 * 输     入：List<TableInfo> tableList -- 桌号信息列表
	 * 输     出：无
	 * 描     述：手动添加新订单
	 * 创建人：高亚妮
	 * 日     期：2014-6-20
	 *****************************************************/
	private void addNewOrder(final List<TableInfo> tableList) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		List<String> tableNameList = new ArrayList<String>();
		for (int i=0; i<tableList.size(); i++) {
			tableNameList.add(tableList.get(i).name);
		}
		dialog.create();
		dialog.setTitle(R.string.input_ordered_table_id);
		dialog.setIcon(android.R.drawable.ic_dialog_info);            
		dialog.setSingleChoiceItems((String[]) tableNameList.toArray(
				new String[tableNameList.size()]), 0, 
		  new DialogInterface.OnClickListener() {
		                            
		     public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();
		        SysApplication.curOrderInfo.clearDishesList();
				SysApplication.curOrderInfo.tableId = tableList.get(which).id;
				SysApplication.curOrderInfo.tableName = tableList.get(which).name;
				SysApplication.curOrderInfo.status = OrderInfo.ACCEPT;
				SysApplication.curOrderInfo.notes = "";
				SysApplication.curOrderInfo.isVip = false;
				Intent intent = new Intent();
				intent.putExtra("new_order", true);
				intent.setClass(getActivity(), OrderDetailActivity.class);
				startActivity(intent);
				plusBtn.setOnClickListener(OrderFragment.this);
		     }
		  }
		);
		dialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				plusBtn.setOnClickListener(OrderFragment.this);
			}
		});
		dialog.show();
	}
	
	@Override
	public void onDetach() {
		stopSyncOrder();
		
		if (updateTimer != null)
			updateTimer.cancel();
		
		super.onDetach();
	}
}
