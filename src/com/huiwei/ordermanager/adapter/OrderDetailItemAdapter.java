/*****************************************************
 * Copyright(c)2014-2015 ������Ϊ���˿Ƽ����޹�˾
 * OrderDetailItemAdapter.java
 * �����ˣ�������
 * ��     �ڣ�2014-6-23
 * ��     �������������б�������
 * ��     ����v6.0
 *****************************************************/

package com.huiwei.ordermanager.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huiwei.ordermanager.R;
import com.huiwei.ordermanager.info.DishesInfo;
import com.huiwei.ordermanager.info.OrderInfo;
import com.huiwei.ordermanager.info.OrderedDishesInfo;
import com.huiwei.ordermanager.info.PreferInfo;
import com.huiwei.ordermanager.view.NumberView;

public class OrderDetailItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private Handler handler;
	private OrderInfo curOrderInfo = new OrderInfo();
	private boolean hasFooter = false;

	public OrderDetailItemAdapter(Context context, Handler handler) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		this.handler = handler;
	}
	
	public void setData(OrderInfo info, boolean hasFooter) {
		this.hasFooter = hasFooter;
		if (info != null) {
			curOrderInfo.dishesInfo.clear();
			curOrderInfo.dishesInfo.addAll(info.dishesInfo);
			curOrderInfo.id = info.id;
			curOrderInfo.status = info.status;
			curOrderInfo.isVip = info.isVip;
			curOrderInfo.isTakeOutOrder = info.isTakeOutOrder;
			notifyDataSetChanged();
		}
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return hasFooter ? curOrderInfo.dishesInfo.size()+1 : curOrderInfo.dishesInfo.size();
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
			convertView = mInflater.inflate(R.layout.order_detail_item, null);
			
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.tv_dishes_name);
			viewHolder.price = (TextView) convertView.findViewById(R.id.tv_dishes_price);
			viewHolder.numberView = (NumberView) convertView.findViewById(R.id.number_view);
			viewHolder.prefer = (TextView) convertView.findViewById(R.id.tv_prefer);
			viewHolder.split = (ImageView) convertView.findViewById(R.id.iv_split);
			viewHolder.soldOut = (ImageView) convertView.findViewById(R.id.iv_sold_out);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}

		boolean isBlackItem = position >= curOrderInfo.dishesInfo.size();
		viewHolder.name.setVisibility(isBlackItem ? View.GONE : View.VISIBLE);
		viewHolder.price.setVisibility(isBlackItem ? View.GONE : View.VISIBLE);
		viewHolder.numberView.setVisibility(isBlackItem ? View.GONE : View.VISIBLE);
		viewHolder.split.setVisibility(isBlackItem ? View.GONE : View.VISIBLE);
		viewHolder.prefer.setVisibility(isBlackItem ? View.GONE : View.VISIBLE);
		if (!isBlackItem) {
			OrderedDishesInfo dishesInfo = curOrderInfo.dishesInfo.get(position);
			viewHolder.name.setText(dishesInfo.dishes.name);
			if (curOrderInfo.isVip) {
				viewHolder.price.setText(convertView.getResources().getString(
						R.string.yuan)+ (dishesInfo.dishes.vipPrice.equals("0") ?
						dishesInfo.dishes.price : dishesInfo.dishes.vipPrice));
			} else {
				viewHolder.price.setText(convertView.getResources().getString(
						R.string.yuan)+dishesInfo.dishes.price);
			}
			
			viewHolder.numberView.setData(dishesInfo.orderNum, 
					dishesInfo.isInput ? dishesInfo.dishes.name : dishesInfo.dishes.id, 
					curOrderInfo.id, (curOrderInfo.status == OrderInfo.CONFIRM), handler);
			viewHolder.numberView.setEnable(curOrderInfo.isTakeOutOrder ? false : true);
			
			viewHolder.prefer.setText(getPrefers(dishesInfo.dishes));
			viewHolder.soldOut.setVisibility(dishesInfo.dishes.isSoldOut ? 
					View.VISIBLE : View.GONE);
		}
		
		return convertView;
	}
	
	private String getPrefers(DishesInfo dishesInfo) {
		String prefers = "";
		for (PreferInfo info : dishesInfo.preferList) {
			if (info.isChecked)
				prefers += info.name+" ";
		}
		
		if (dishesInfo.otherPrefer != null)
			prefers += dishesInfo.otherPrefer;
		
		return prefers;
	}
	
	public static class ViewHolder {
		TextView name;
		TextView price;
		NumberView numberView; 
		TextView prefer;
		ImageView soldOut;
		ImageView split;
	}
}