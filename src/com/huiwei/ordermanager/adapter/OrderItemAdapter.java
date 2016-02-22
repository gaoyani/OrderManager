package com.huiwei.ordermanager.adapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.huiwei.commonlib.CommonFunction;
import com.huiwei.ordermanager.R;
import com.huiwei.ordermanager.activity.MainActivity;
import com.huiwei.ordermanager.activity.OrderFragment;
import com.huiwei.ordermanager.common.SysApplication;
import com.huiwei.ordermanager.constant.UrlConstant;
import com.huiwei.ordermanager.info.OrderInfo;
import com.huiwei.ordermanager.task.ChangeOrderStatusTask;
import com.huiwei.ordermanager.task.OrderDeleteTask;
import com.huiwei.ordermanager.task.PrintAccountTask;

public class OrderItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private Handler handlerStatus, handlerDelte, handlerPrintAccount;
	private int listType = OrderFragment.UNCONFIRM_LIST;
	private List<OrderInfo> newOrderList = new ArrayList<OrderInfo>();
	private Map<String, List<OrderInfo>> confirmOrderList = new LinkedHashMap<String, List<OrderInfo>>();
	private List<String> confirmTableIDList = new ArrayList<String>();

	public OrderItemAdapter(Context context, Handler handlerStatus, Handler handlerDelte, Handler handlerPrintAccount) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		this.handlerStatus = handlerStatus;
		this.handlerDelte = handlerDelte;
		this.handlerPrintAccount = handlerPrintAccount;
	}
	
	public void setData(List<OrderInfo> newOrderList, Map<String, List<OrderInfo>> confirmOrderList, List<String> confirmTableIDList) {
		if (newOrderList != null) {
			this.newOrderList.clear();
			this.newOrderList.addAll(newOrderList);
			this.confirmOrderList.clear();
			for (String key : confirmTableIDList) {
				List<OrderInfo> orderList = new ArrayList<OrderInfo>();
				orderList.addAll(confirmOrderList.get(key));
				this.confirmOrderList.put(key, orderList);
			}
			this.confirmTableIDList.clear();
			this.confirmTableIDList.addAll(confirmTableIDList);
			notifyDataSetChanged();
		}
	}
	
	public void setListType(int type) {
		listType = type;
	}
	
	@Override
	public int getCount() {
		return listType == OrderFragment.UNCONFIRM_LIST ? 
				newOrderList.size() : confirmOrderList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.order_menu_item, null);
			
			viewHolder = new ViewHolder();
			viewHolder.status = (TextView) convertView.findViewById(R.id.tv_status);
			viewHolder.tableName = (TextView) convertView.findViewById(R.id.tv_table_name);
			viewHolder.orderID = (TextView) convertView.findViewById(R.id.tv_order_id);
			viewHolder.accept = (Button) convertView.findViewById(R.id.btn_accept);
			viewHolder.printAccount = (Button) convertView.findViewById(R.id.btn_print_account);
			viewHolder.delete = (Button) convertView.findViewById(R.id.btn_delete);
			viewHolder.waimai = (ImageView) convertView.findViewById(R.id.iv_waimai);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		OrderInfo orderInfo = listType == OrderFragment.UNCONFIRM_LIST ? 
				newOrderList.get(position) : confirmOrderList.get(confirmTableIDList.get(position)).get(0);
			
		viewHolder.waimai.setVisibility(orderInfo.isTakeOutOrder ? View.VISIBLE : View.GONE);
		
		if (listType == OrderFragment.UNCONFIRM_LIST) {
			if (orderInfo.status == OrderInfo.NEW || orderInfo.status == OrderInfo.ACCEPT) {
				convertView.setVisibility(View.VISIBLE);
			} else {
				convertView.setVisibility(View.GONE);
				return convertView;
			}
		} else {
			if (orderInfo.type == OrderInfo.TYPE_ORDER || orderInfo.status == OrderInfo.CONFIRM) {
				convertView.setVisibility(View.VISIBLE);
			} else {
				convertView.setVisibility(View.GONE);
				return convertView;
			}
		}
		
		//set content and bk color
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		viewHolder.orderID.setVisibility(View.VISIBLE);
		if (orderInfo.type == OrderInfo.TYPE_ORDER) {    //order
			if (listType == OrderFragment.CONFIRM_LIST) {  //confirm list layout
				viewHolder.orderID.setVisibility(View.GONE);
				lp.addRule(RelativeLayout.CENTER_VERTICAL);
				lp.setMargins(CommonFunction.dip2px(mContext, 15), 0, 0, 0);
			} else {                                         //unconfirm list layout
				viewHolder.orderID.setText(convertView.getResources().getString(R.string.order_id)+
						orderInfo.content);
				lp.addRule(RelativeLayout.BELOW, R.id.layout_order_id);
				lp.setMargins(CommonFunction.dip2px(mContext, 15), 
						CommonFunction.dip2px(mContext, 5), 0, 0);
			}
			
			viewHolder.tableName.setText(orderInfo.tableName
					+ mContext.getResources().getString(R.string.order_food) 
					+ (orderInfo.isAddOrder ? mContext.getResources().getString(R.string.add_order) : ""));
			convertView.setBackgroundResource(R.drawable.order_item_bg);
		} else {             //calling layout
			viewHolder.orderID.setText("");
			lp.addRule(RelativeLayout.CENTER_VERTICAL);
			lp.setMargins(CommonFunction.dip2px(mContext, 15), 0, 0, 0);
			viewHolder.tableName.setText(orderInfo.tableName +" : "+orderInfo.content);
			convertView.setBackgroundColor(convertView.getResources().getColor(R.color.calling_color));
		}
		viewHolder.tableName.setLayoutParams(lp);
		
		viewHolder.accept.setTag(position);
		viewHolder.accept.setOnClickListener(clickListener);
		
		viewHolder.delete.setVisibility(View.GONE);
		if (orderInfo.type == OrderInfo.TYPE_ORDER && 
				(orderInfo.status == OrderInfo.NEW || orderInfo.status == OrderInfo.ACCEPT)) {
			viewHolder.delete.setVisibility(View.VISIBLE);
			viewHolder.delete.setTag(position);
			viewHolder.delete.setOnClickListener(deleteClickListener);
		}
		
		viewHolder.printAccount.setVisibility(View.GONE);
		//set status
		if (orderInfo.status == OrderInfo.NEW) {			
			viewHolder.accept.setVisibility(View.VISIBLE);
			viewHolder.accept.setText(convertView.getResources().getString(R.string.accept));
			viewHolder.accept.setBackgroundResource(R.drawable.button_logout_selector);

		} else if (orderInfo.status == OrderInfo.ACCEPT) {
			if (orderInfo.type == OrderInfo.TYPE_CALLING) {
				viewHolder.accept.setVisibility(View.VISIBLE);
				viewHolder.accept.setText(convertView.getResources().getString(R.string.confirm));
				viewHolder.accept.setBackgroundResource(R.drawable.button_confirm_selector);
			} else {
				viewHolder.accept.setVisibility(View.GONE);
				viewHolder.status.setText(convertView.
						getResources().getString(R.string.order_accept));
				viewHolder.status.setTextColor(convertView.
						getResources().getColor(R.color.new_order_color));
			}
		} else if (orderInfo.status == OrderInfo.CONFIRM) {
			viewHolder.printAccount.setVisibility(View.VISIBLE);
			viewHolder.printAccount.setTag(position);
			viewHolder.printAccount.setOnClickListener(clickListener);
			viewHolder.status.setText(convertView.
					getResources().getString(R.string.order_confirm));
			viewHolder.status.setTextColor(convertView.
					getResources().getColor(R.color.confirm_order_color));
		}
		
		return convertView;
	}
	
	OnClickListener deleteClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			final OrderInfo orderInfo = newOrderList.get((Integer) v.getTag());
					
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle(mContext.getResources().getString(R.string.order_delete_title));
			builder.setMessage(mContext.getResources().getString(R.string.order_delete_message)
					+orderInfo.tableName+mContext.getResources().getString(R.string.order_delete_message_end));
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					deleteOrder(orderInfo.id);
				}
			});
			builder.setNegativeButton(R.string.cancel, null);
			builder.create();
			builder.show();
		}
	};
	
	OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			OrderInfo orderInfo = listType == OrderFragment.UNCONFIRM_LIST ? 
					newOrderList.get((Integer) v.getTag()) : 
						confirmOrderList.get(confirmTableIDList.get((Integer) v.getTag())).get(0);
			if (orderInfo.type == OrderInfo.TYPE_CALLING) {
				if (orderInfo.status == OrderInfo.NEW)
					ChangeStatus(orderInfo.id, orderInfo.isTakeOutOrder, OrderInfo.ACCEPT);
				else
					ChangeStatus(orderInfo.id, orderInfo.isTakeOutOrder, OrderInfo.FINASH);
			} else {
				if (orderInfo.status == OrderInfo.CONFIRM) {
					printAccount(orderInfo.content, orderInfo.tableId);
				} else {
					ChangeStatus(orderInfo.id, orderInfo.isTakeOutOrder, OrderInfo.ACCEPT);
				}
			}
		}
	};

	private void ChangeStatus(int id, boolean isTackOutOrder, int status) {
		ChangeOrderStatusTask task = new ChangeOrderStatusTask(
				mContext, handlerStatus, id, isTackOutOrder, status);
//		task.execute(UrlConstant.getServerUrl(mContext));
		task.executeOnExecutor(MainActivity.FULL_TASK_EXECUTOR, UrlConstant.getServerUrl(mContext));
	}
	
	private void printAccount(String orderID, String tableID) {
		PrintAccountTask task = new PrintAccountTask(
				mContext, handlerPrintAccount, orderID, tableID);
//		task.execute(UrlConstant.getServerUrl(mContext));
		task.executeOnExecutor(MainActivity.FULL_TASK_EXECUTOR, UrlConstant.getServerUrl(mContext));
	}

	private void deleteOrder(int id) {
		OrderDeleteTask task = new OrderDeleteTask(mContext, handlerDelte, id);
//		task.execute(UrlConstant.getServerUrl(mContext));
		task.executeOnExecutor(MainActivity.FULL_TASK_EXECUTOR, UrlConstant.getServerUrl(mContext));
	}
	
	public static class ViewHolder {
		TextView tableName;
		TextView orderID;
		TextView status;
		Button accept;
		Button printAccount;
		Button delete;
		ImageView waimai;
	}
}
