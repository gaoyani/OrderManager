/*****************************************************
 * Copyright(c)2014-2015 北京汇为永兴科技有限公司
 * OrderItemAdapter.java
 * 创建人：高亚妮
 * 日     期：2014-6-23
 * 描     述：订单列表适配器
 * 版     本：v6.0
 *****************************************************/

package com.huiwei.ordermanager.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
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
import com.huiwei.ordermanager.activity.OrderFragment;
import com.huiwei.ordermanager.constant.UrlConstant;
import com.huiwei.ordermanager.info.OrderInfo;
import com.huiwei.ordermanager.task.ChangeOrderStatusTask;
import com.huiwei.ordermanager.task.OrderDeleteTask;

public class OrderItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private Handler handlerStatus, handlerDelte;
	private int listType = OrderFragment.UNCONFIRM_LIST;
	private List<OrderInfo> newOrderList = new ArrayList<OrderInfo>();
	private List<OrderInfo> confirmOrderList = new ArrayList<OrderInfo>();

	public OrderItemAdapter(Context context, Handler handlerStatus, Handler handlerDelte) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		this.handlerStatus = handlerStatus;
		this.handlerDelte = handlerDelte;
	}
	
	public void setData(List<OrderInfo> newOrderList, List<OrderInfo> confirmOrderList) {
		if (newOrderList != null) {
			this.newOrderList.clear();
			this.newOrderList.addAll(newOrderList);
			this.confirmOrderList.clear();
			this.confirmOrderList.addAll(confirmOrderList);
			notifyDataSetChanged();
		}
	}
	
	/*****************************************************
	 * 函数名：setListType
	 * 输     入：int type -- 订单列表类型（未处理、已处理）
	 * 输     出：无
	 * 描     述：设置订单列表类型
	 * 创建人：高亚妮
	 * 日     期：2014-6-23
	 *****************************************************/
	public void setListType(int type) {
		listType = type;
	}
	
	//获取列表项个数
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

	/*****************************************************
	 * 函数名：getView
	 * 输     入：int position -- 该视图在适配器数据中的位置 
	 * 		   View convertView -- 旧视图
	 * 			ViewGroup parent -- 此视图最终会被附加到的父级视图
	 * 输     出：View -- 指定position的列表视图
	 * 描     述：获取列表中制定position显示特定数据的视图
	 * 创建人：高亚妮
	 * 日     期：2014-6-23
	 *****************************************************/
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
			viewHolder.delete = (Button) convertView.findViewById(R.id.btn_delete);
			viewHolder.waimai = (ImageView) convertView.findViewById(R.id.iv_waimai);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		OrderInfo orderInfo = listType == OrderFragment.UNCONFIRM_LIST ? 
				newOrderList.get(position) : confirmOrderList.get(position);
			
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
		if (orderInfo.type == OrderInfo.TYPE_ORDER) {
			viewHolder.orderID.setText(convertView.getResources().getString(R.string.order_id)+
					orderInfo.content);
			lp.addRule(RelativeLayout.BELOW, R.id.layout_order_id);
			lp.setMargins(CommonFunction.dip2px(mContext, 15), 
					CommonFunction.dip2px(mContext, 5), 0, 0);
			viewHolder.tableName.setText(orderInfo.tableName
					+ mContext.getResources().getString(R.string.order_food));
			convertView.setBackgroundResource(R.drawable.order_item_bg);
		} else {
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
			viewHolder.accept.setVisibility(View.GONE);
			viewHolder.status.setText(convertView.
					getResources().getString(R.string.order_confirm));
			viewHolder.status.setTextColor(convertView.
					getResources().getColor(R.color.confirm_order_color));
		}
		
		return convertView;
	}
	
	//订单删除按钮点击消息处理
	OnClickListener deleteClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			final OrderInfo orderInfo = listType == OrderFragment.UNCONFIRM_LIST ? 
					newOrderList.get((Integer) v.getTag()) : 
					confirmOrderList.get((Integer) v.getTag());
					
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
	
	//订单状态修改按钮点击消息处理
	OnClickListener clickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			OrderInfo orderInfo = listType == OrderFragment.UNCONFIRM_LIST ? 
					newOrderList.get((Integer) v.getTag()) : 
					confirmOrderList.get((Integer) v.getTag());
			if (orderInfo.type == OrderInfo.TYPE_CALLING) {
				if (orderInfo.status == OrderInfo.NEW)
					ChangeStatus(orderInfo.id, orderInfo.isTakeOutOrder, OrderInfo.ACCEPT);
				else
					ChangeStatus(orderInfo.id, orderInfo.isTakeOutOrder, OrderInfo.FINASH);
			} else {
				ChangeStatus(orderInfo.id, orderInfo.isTakeOutOrder, OrderInfo.ACCEPT);
			}
		}
	};
	
	/*****************************************************
	 * 函数名：deleteOrder
	 * 输     入：int id -- 订单id 
	 * 		  int status -- 订单状态
	 * 输     出：无
	 * 描     述：修改服务器订单状态
	 * 创建人：高亚妮
	 * 日     期：2014-6-23
	 *****************************************************/
	private void ChangeStatus(int id, boolean isTackOutOrder, int status) {
		ChangeOrderStatusTask task = new ChangeOrderStatusTask(
				mContext, handlerStatus, id, isTackOutOrder, status);
		task.execute(UrlConstant.getServerUrl(mContext));
	}
	
	/*****************************************************
	 * 函数名：deleteOrder
	 * 输     入：int id -- 订单id
	 * 输     出：无
	 * 描     述：删除服务器订单
	 * 创建人：高亚妮
	 * 日     期：2014-6-23
	 *****************************************************/
	private void deleteOrder(int id) {
		OrderDeleteTask task = new OrderDeleteTask(mContext, handlerDelte, id);
		task.execute(UrlConstant.getServerUrl(mContext));
	}
	
	public static class ViewHolder {
		TextView tableName;
		TextView orderID;
		TextView status;
		Button accept;
		Button delete;
		ImageView waimai;
	}
}
