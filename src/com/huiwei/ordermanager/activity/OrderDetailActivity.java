/*****************************************************
 * Copyright(c)2014-2015 ������Ϊ���˿Ƽ����޹�˾
 * OrderDetailActivity.java
 * �����ˣ�������
 * ��     �ڣ�2014-6-21
 * ��     ������������ҳ����ʾ�������޸��ļ�
 * ��     ����v6.0
 *****************************************************/
package com.huiwei.ordermanager.activity;

import java.math.BigDecimal;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huiwei.commonlib.NetCheck;
import com.huiwei.commonlib.Preferences;
import com.huiwei.ordermanager.R;
import com.huiwei.ordermanager.adapter.OrderDetailItemAdapter;
import com.huiwei.ordermanager.baseclass.BaseActivity;
import com.huiwei.ordermanager.common.SysApplication;
import com.huiwei.ordermanager.constant.CommonConstant;
import com.huiwei.ordermanager.constant.UrlConstant;
import com.huiwei.ordermanager.info.DishesInfo;
import com.huiwei.ordermanager.info.OrderInfo;
import com.huiwei.ordermanager.info.OrderedDishesInfo;
import com.huiwei.ordermanager.info.TableInfo;
import com.huiwei.ordermanager.task.AddOrderTask;
import com.huiwei.ordermanager.task.DishesDetailsTask;
import com.huiwei.ordermanager.task.GetTableListTask;
import com.huiwei.ordermanager.task.OrderUploadingTask;
import com.huiwei.ordermanager.task.PreferHistoryTask;
import com.huiwei.ordermanager.view.LoadingProgressView;
import com.huiwei.ordermanager.view.OrderUpdateView;
import com.huiwei.ordermanager.view.PreferChooseView;

public class OrderDetailActivity extends BaseActivity implements
		OnClickListener {

	private TextView title, totalPrice;
	private ImageView back, plus;
	private ListView listView;
	private RelativeLayout layoutBottm;
	private LoadingProgressView pbView;
	private OrderDetailItemAdapter adapter;

	private boolean isOrderChanged = false;
	private int dishesNum = 0;
	private AlertDialog preferDialog, updateOrderDialog;
	private PreferChooseView preferView;
	private OrderUpdateView orderUpdateView;
	private boolean isNewOrder = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Preferences.GetBoolean(this, "keep_screen_on", true)) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		}
		
		setContentView(R.layout.activity_order_detail);

		SysApplication.curOrderInfo.isAddOrder = false;
		
		isNewOrder = getIntent().getBooleanExtra("new_order", false);

		title = (TextView) findViewById(R.id.tv_title);
		title.setText(SysApplication.curOrderInfo.tableName);

		totalPrice = (TextView) findViewById(R.id.tv_price);
		pbView = (LoadingProgressView) findViewById(R.id.loading_view);

		back = (ImageView) findViewById(R.id.iv_return);
		back.setOnClickListener(this);
		plus = (ImageView) findViewById(R.id.iv_plus);
		plus.setOnClickListener(this);

		layoutBottm = (RelativeLayout) findViewById(R.id.layout_bottom);
		if (SysApplication.curOrderInfo.status == OrderInfo.ACCEPT) {
			layoutBottm.setOnClickListener(this);
		} else {
			plus.setVisibility(View.GONE);
			if (Preferences.GetBoolean(this, "enable_edit_confirm_order", true)) {
				layoutBottm.setOnClickListener(this);
			} else {
				((TextView)findViewById(R.id.tv_upload)).setVisibility(View.GONE);
			}
		}

		dishesNum = SysApplication.curOrderInfo.getDishesNum();

		if (!isNewOrder) {
			getOrderDetail();
		} else {
			initListView();
			pbView.hideView();
			
			Intent intent = new Intent();
			intent.setClass(this, AllDishesActivity.class);
			startActivity(intent);
		}
	}

	@Override
	protected void onResume() {
		if (adapter != null) {
			adapter.setData(SysApplication.curOrderInfo, true);;
			updatePrice();
		}

		super.onResume();
	}

	private void initListView() {
		listView = (ListView) findViewById(R.id.lv_order_detail);
		adapter = new OrderDetailItemAdapter(this, dishesChangeHandler);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == SysApplication.curOrderInfo.dishesInfo.size() || 
						SysApplication.curOrderInfo.isTakeOutOrder)
					return;

				if (preferDialog == null) {
					preferDialog = new AlertDialog.Builder(
							OrderDetailActivity.this).create();
					LayoutInflater inflater = LayoutInflater
							.from(OrderDetailActivity.this);
					preferView = (PreferChooseView) inflater.inflate(
							R.layout.prefor_pop_view, null);
				}

				DishesInfo dishesInfo = SysApplication.curOrderInfo.dishesInfo
						.get(position).dishes;
				if (dishesInfo.preferHistory == null
						&& !dishesInfo.id.equals("0")
						&& !SysApplication.curOrderInfo.content.equals("")) {
					PreferHistoryTask task = new PreferHistoryTask(
							OrderDetailActivity.this, preferHistoryHandler,
							SysApplication.curOrderInfo.content, dishesInfo);
					task.execute(UrlConstant
							.getServerUrl(OrderDetailActivity.this));
				} else {
					popPreferDialog(dishesInfo);
				}
			}
		});
	}

	private Handler preferHistoryHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == CommonConstant.SUCCESS) {
				popPreferDialog((DishesInfo) msg.obj);
			}
		}
	};

	private void popPreferDialog(DishesInfo dishesInfo) {
		preferView.setData(dishesInfo, preferHandler);
		preferDialog.setView(preferView, 0, 0, 0, 0);
		preferDialog.show();
	}

	private Handler preferHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			preferDialog.dismiss();
			adapter.setData(SysApplication.curOrderInfo, true);
		}
	};

	private Handler dishesChangeHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int num = msg.arg1;
			String dishesId = (String) msg.obj;
			final OrderedDishesInfo info = SysApplication.curOrderInfo
					.findOrderedDishes(dishesId);
			isOrderChanged = true;
			info.orderNum = String.valueOf(num);
//			if (num == 1) {
//				info.orderNum = String
//						.valueOf(Integer.parseInt(info.orderNum) + 1);
//			} else if (num == -1) {
//				info.orderNum = String
//						.valueOf(Integer.parseInt(info.orderNum) - 1);
//			}
			
//			if (Integer.parseInt(info.orderNum) == 0) {
//				AlertDialog.Builder builder = new AlertDialog.Builder(OrderDetailActivity.this);
//				String message = getResources().getString(R.string.delete_order_dishes)+
//						info.dishes.name+getResources().getString(R.string.delete_order_dishes_end);
//				builder.setMessage(message);
//				builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
//					
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						info.orderNum = "1";
//						adapter.setData(SysApplication.curOrderInfo);
//						updatePrice();
//					}
//				});
//				builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//					
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						SysApplication.curOrderInfo.dishesInfo.remove(info);
//						adapter.setData(SysApplication.curOrderInfo);
//						updatePrice();
//					}
//				});
//				builder.create().show();
//			} else {
				updatePrice();
//			}
		};
	};

	private void updatePrice() {
		float total = 0;
		for (OrderedDishesInfo info : SysApplication.curOrderInfo.dishesInfo) {
			float price = Float.parseFloat(SysApplication.curOrderInfo.isVip && 
					!info.dishes.vipPrice.equals("0") ? 
					info.dishes.vipPrice : info.dishes.price)
					* Integer.parseInt(info.orderNum);

			total += price;
		}

		float result = new BigDecimal(total).setScale(2,
				BigDecimal.ROUND_HALF_UP).floatValue();

		totalPrice.setText(getResources().getString(R.string.yuan)
				+ String.valueOf(result));
	}

	private void getOrderDetail() {
//		DishesDetailsTask ddt = new DishesDetailsTask(this, handler, 
//				SysApplication.curOrderInfo.content, SysApplication.curOrderInfo.isTakeOutOrder);
		DishesDetailsTask ddt = new DishesDetailsTask(this, handler, 
				SysApplication.curOrderInfo);
//		ddt.execute(UrlConstant.getServerUrl(this));
		ddt.executeOnExecutor(MainActivity.FULL_TASK_EXECUTOR, UrlConstant.getServerUrl(this));

		pbView.setHandler(handler);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == LoadingProgressView.RELOADING) {
				getOrderDetail();
				return;
			}

			pbView.hideView();
			if (msg.what == CommonConstant.SUCCESS) {
				initListView();
				adapter.setData(SysApplication.curOrderInfo, true);
				updatePrice();
				if (SysApplication.curOrderInfo.isTakeOutOrder) {
					((LinearLayout)findViewById(R.id.layout_detail)).setVisibility(View.VISIBLE);
					((TextView)findViewById(R.id.tv_contact_name)).setText(SysApplication.curOrderInfo.contactName);
					((TextView)findViewById(R.id.tv_contact_number)).setText(SysApplication.curOrderInfo.contactNumber);
					((TextView)findViewById(R.id.tv_contact_address)).setText(SysApplication.curOrderInfo.contactAddress);
					((TextView)findViewById(R.id.tv_send_type)).setText(SysApplication.curOrderInfo.sendType);
					((TextView)findViewById(R.id.tv_send_time)).setText(SysApplication.curOrderInfo.sendTime);
					plus.setVisibility(View.GONE);
					((LinearLayout)findViewById(R.id.layout_detail)).setVisibility(View.VISIBLE);
				}
			} else {
				pbView.showLoadingFailed();
			}
		};
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_return:
			returnBack();
			break;

		case R.id.layout_bottom:
			uploadOrder();
			break;

		case R.id.iv_plus: {
			Intent intent = new Intent();
			intent.setClass(this, AllDishesActivity.class);
			startActivity(intent);
		}
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			returnBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void returnBack() {
		if ((isOrderChanged
				|| dishesNum != SysApplication.curOrderInfo.getDishesNum()) && SysApplication.curOrderInfo.status <= OrderInfo.ACCEPT) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					OrderDetailActivity.this);
			builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.setMessage(R.string.submit_order_title);
			builder.setNegativeButton(R.string.submit_order_ok,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							uploadOrder();
						}
					});
			builder.setPositiveButton(R.string.submit_order_no,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			builder.create();
			builder.show();
		} else {
			finish();
		}
	}

	private void uploadOrder() {
//		if (isOrderEmpty()) {
//			Toast.makeText(getApplicationContext(),
//					getResources().getString(R.string.order_empty),
//					Toast.LENGTH_SHORT).show();
//			return;
//		}
		
		if (SysApplication.hasSoldOutDishes()) {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.order_has_sold_out),
					Toast.LENGTH_SHORT).show();
			return;
		}

		if (isNewOrder || SysApplication.curOrderInfo.status != OrderInfo.CONFIRM) {
			if (SysApplication.tableList.size() == 0) {
				pbView.showProgressBar();
				GetTableListTask task = new GetTableListTask(this, tableListHandler, "");
				task.execute(UrlConstant.getServerUrl(this));
			} else {
				popUpdateOrderDialg(SysApplication.tableList);
			}
		} else {
			pbView.showProgressBar();
			startUploadTask();
		}
	}
	
	private Handler tableListHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			if (msg.what == CommonConstant.SUCCESS) {
				pbView.hideView();
				popUpdateOrderDialg(SysApplication.tableList);
			}
		};
	};
	
	private boolean isOrderEmpty() {
		if (SysApplication.curOrderInfo.dishesInfo.size() == 0) {
			return true;
		} else {
			for (OrderedDishesInfo info : SysApplication.curOrderInfo.dishesInfo) {
				if (!info.orderNum.equals("0")) {
					return false;
				}
			}
			
			return true;
		}
	}

	private void popUpdateOrderDialg(List<TableInfo> tableList) {
		if (updateOrderDialog == null) {
			updateOrderDialog = new AlertDialog.Builder(this).create();
			LayoutInflater inflater = LayoutInflater
					.from(OrderDetailActivity.this);
			orderUpdateView = (OrderUpdateView) inflater.inflate(
					R.layout.order_update_view, null);
			updateOrderDialog.setView(orderUpdateView);
		}
		orderUpdateView.setData(tableList, updateOrderDlgHandler);
		updateOrderDialog.show();
	}

	private Handler updateOrderDlgHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			updateOrderDialog.dismiss();
			if (msg.what == CommonConstant.OK) {
				pbView.showProgressBar();
				if (isNewOrder) {
					startAddOrderTask();
				} else {
					startUploadTask();
				}
			}
		};
	};

	private void startAddOrderTask() {
		AddOrderTask addOrderTask = new AddOrderTask(this, uploadHandler);
//		addOrderTask.execute(UrlConstant.getServerUrl(this));
		addOrderTask.executeOnExecutor(MainActivity.FULL_TASK_EXECUTOR, UrlConstant.getServerUrl(this));
	}
	
	private void startUploadTask() {
		OrderUploadingTask uploadTask = new OrderUploadingTask(this,
				uploadHandler, SysApplication.curOrderInfo);
//		uploadTask.execute(UrlConstant.getServerUrl(this));
		uploadTask.executeOnExecutor(MainActivity.FULL_TASK_EXECUTOR, UrlConstant.getServerUrl(this));
	}

	private Handler uploadHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == CommonConstant.UPLOADING_SUCCESS) {
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.submit_success),
						Toast.LENGTH_SHORT).show();
				SysApplication.curOrderInfo.clearDishesList();
				finish();
			} else {
				Toast.makeText(getApplicationContext(),
						(String)msg.obj,
						Toast.LENGTH_SHORT).show();
			} 
			pbView.hideView();
		};
	};
}
