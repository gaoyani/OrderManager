package com.huiwei.ordermanager.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.huiwei.ordermanager.task.SyncOrderTask;
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
	private boolean isOrderUpdate = false;
	private SyncOrderTimerTask sotTask;
	private int type = UNCONFIRM_LIST;
	private boolean isFirstExc = true;

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
		if (isFirstExc) {
			isFirstExc = false;
		} else {
			getOrders();
		}
		
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

	private void initGroupBtns(View view) {
		groupConfirm = (TextView)view.findViewById(R.id.tv_confirm);
		groupConfirm.setOnClickListener(this);
		groupUnconfirm = (RelativeLayout)view.findViewById(R.id.layout_unconfirm);
		groupUnconfirm.setOnClickListener(this);
		
		newNum = (TextView)view.findViewById(R.id.tv_new_num);
		groupUnconfirmBL = (ImageView)view.findViewById(R.id.iv_unconfirm);
		groupConfirmBL = (ImageView)view.findViewById(R.id.iv_confirm);
	}

	private void startSyncOrderTimer() {
		sotTask = new SyncOrderTimerTask(getActivity(), handlerSyncOrder);
//		sotTask.execute(UrlConstant.getServerUrl(getActivity()));
		sotTask.executeOnExecutor(MainActivity.FULL_TASK_EXECUTOR, UrlConstant.getServerUrl(getActivity()));
		Log.d("OrderFragment", "startSyncOrderTimer");
	}
	
	private void getOrders() {
		SyncOrderTask at = new SyncOrderTask(getActivity(), handlerSyncOrder);
		at.executeOnExecutor(MainActivity.FULL_TASK_EXECUTOR, UrlConstant.getServerUrl(getActivity()));
	}
	
	private void stopSyncOrder() {
		Log.d("OrderFragment", "stopSyncOrder");
		if (!sotTask.isCancelled()) {
			sotTask.stopTimer();
			sotTask.cancel(true);
			sotTask = null;
		}
	}

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

	private void initListView(View view) {
		orderListView = (ListView)view.findViewById(R.id.lv_order_info);
		adapter = new OrderItemAdapter(getActivity(), handlerStatus, handlerDelete, handlerPrintAccount);
		orderListView.setAdapter(adapter);
		orderListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
		
				OrderInfo info = type == UNCONFIRM_LIST ? SysApplication.newOrderList.get(position) : 
					SysApplication.confirmOrderList.get(SysApplication.confirmTableIDList.get(position)).get(0);
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
				intent.setClass(getActivity(), type == UNCONFIRM_LIST ? OrderDetailActivity.class : ConfirmOrderDetailActivity.class);
				getActivity().startActivity(intent);
			}
		});
	}

	private Handler handlerStatus = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			int status = msg.arg1;
			String statusStr = getResources().getString(status == OrderInfo.ACCEPT ?
					R.string.accept : R.string.confirm);
			
			if (msg.what == CommonConstant.SUCCESS) {
				getOrders();
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

	private Handler handlerDelete = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			if (msg.what == CommonConstant.SUCCESS) {
				getOrders();
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
	
	private Handler handlerPrintAccount = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == CommonConstant.SUCCESS) {
				Toast.makeText(getActivity(),  getResources().
						getString(R.string.print_accounts) + getResources().
						getString(R.string.success), Toast.LENGTH_SHORT).show();
			} else if (msg.what == CommonConstant.OTHER_FAIL){
				Toast.makeText(getActivity(), (String)msg.obj, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), getResources().
						getString(R.string.print_accounts) + getResources().
						getString(R.string.failed), Toast.LENGTH_SHORT).show();
			}
		};
	};
	
	public void updateListView() {		
		resetOrderNum();
		adapter.setListType(type);
		adapter.setData(SysApplication.newOrderList, SysApplication.confirmOrderList, SysApplication.confirmTableIDList);
	}
	
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
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_menu:
			listener.onMenuPop();
			break;
			
		case R.id.iv_plus: {
			if (SysApplication.tableList.size() == 0) {
				GetTableListTask task = new GetTableListTask(getActivity(), 
						tableListHandler, "");
				task.execute(UrlConstant.getServerUrl(getActivity()));
				pbView.showProgressBar();
			} else {
				addNewOrder(SysApplication.tableList);
			}
			
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
	
	private void resetTypeBtn() {
		groupUnconfirmBL.setBackgroundColor(getResources().getColor(type == UNCONFIRM_LIST ? 
				R.color.selected : R.color.order_group_bl_color));
		groupConfirmBL.setBackgroundColor(getResources().getColor(type == CONFIRM_LIST ? 
				R.color.selected : R.color.order_group_bl_color));
	}
	
	private Handler tableListHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			pbView.hideView();
			if (msg.what == CommonConstant.SUCCESS) {
				if (SysApplication.tableList.size() == 0) {
					Toast.makeText(getActivity(), R.string.order_table_all_used, 
							Toast.LENGTH_SHORT).show();
					plusBtn.setOnClickListener(OrderFragment.this);
				} else {
					addNewOrder(SysApplication.tableList);
				}
			}
		};
	}; 
	
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
		super.onDetach();
	}
	
	@Override
	public void onDestroy() {
		stopSyncOrder();
		super.onDestroy();
	}
}
