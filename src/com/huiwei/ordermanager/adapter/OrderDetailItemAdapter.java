/*****************************************************
 * Copyright(c)2014-2015 北京汇为永兴科技有限公司
 * OrderDetailItemAdapter.java
 * 创建人：高亚妮
 * 日     期：2014-6-23
 * 描     述：订单详情列表适配器
 * 版     本：v6.0
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

	public OrderDetailItemAdapter(Context context, Handler handler) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		this.handler = handler;
	}
	
	public void setData(OrderInfo info) {
		if (info != null) {
			curOrderInfo.dishesInfo.clear();
			curOrderInfo.dishesInfo.addAll(info.dishesInfo);
			curOrderInfo.isVip = info.isVip;
			curOrderInfo.isTakeOutOrder = info.isTakeOutOrder;
			notifyDataSetChanged();
		}
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return curOrderInfo.dishesInfo.size()+1;
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
					dishesInfo.isInput ? dishesInfo.dishes.name : 
					dishesInfo.dishes.id, handler);
			viewHolder.numberView.setEnable(curOrderInfo.isTakeOutOrder ? false : true);
			
			viewHolder.prefer.setText(getPrefers(dishesInfo.dishes));
			viewHolder.soldOut.setVisibility(dishesInfo.dishes.isSoldOut ? 
					View.VISIBLE : View.GONE);
		}
		
		return convertView;
	}
	
	/*****************************************************
	 * 函数名：getPrefers
	 * 输     入：DishesInfo dishesInfo -- 菜品数据 
	 * 输     出：String -- 按制定格式显示的偏好字符串
	 * 描     述：获取制定菜品的偏好
	 * 创建人：高亚妮
	 * 日     期：2014-6-23
	 *****************************************************/
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