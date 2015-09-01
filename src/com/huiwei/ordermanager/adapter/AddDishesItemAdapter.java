/*****************************************************
 * Copyright(c)2014-2015 北京汇为永兴科技有限公司
 * AddDishesItemAdapter.java
 * 创建人：高亚妮
 * 日     期：2014-6-23
 * 描     述：加餐菜单列表适配器
 * 版     本：v6.0
 *****************************************************/

package com.huiwei.ordermanager.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.huiwei.ordermanager.R;
import com.huiwei.ordermanager.info.DishesInfo;
import com.huiwei.ordermanager.info.OrderInfo;
import com.huiwei.ordermanager.info.OrderedDishesInfo;
import com.huiwei.ordermanager.view.NumberView;

public class AddDishesItemAdapter extends DishesBaseAdapter {

	private OrderInfo tempOrderInfo;

	public AddDishesItemAdapter(Context context, Handler handler, OrderInfo orderInfo) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		this.handler = handler;
		this.tempOrderInfo = orderInfo;
	}
	
	@Override
	public int getCount() {
		return searchDishesIDList.size()+1;
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
			convertView = mInflater.inflate(R.layout.order_detail_item , null);
			
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.tv_dishes_name);
			viewHolder.price = (TextView) convertView.findViewById(R.id.tv_dishes_price);
			viewHolder.split = (ImageView) convertView.findViewById(R.id.iv_split);
			viewHolder.numberView = (NumberView) convertView.findViewById(R.id.number_view);
			viewHolder.soldOut = (ImageView) convertView.findViewById(R.id.iv_sold_out);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}

		boolean isBlackItem = position >= searchDishesIDList.size();
		viewHolder.name.setVisibility(isBlackItem ? View.GONE : View.VISIBLE);
		viewHolder.price.setVisibility(isBlackItem ? View.GONE : View.VISIBLE);
		viewHolder.split.setVisibility(isBlackItem ? View.GONE : View.VISIBLE);
		viewHolder.numberView.setVisibility(isBlackItem ? View.GONE
				: View.VISIBLE);
		((TextView) convertView.findViewById(R.id.tv_prefer)).setVisibility(View.GONE);;

		if (!isBlackItem) {
			DishesInfo dishesInfo = dishesList.get(
					searchDishesIDList.get(position));
			
			viewHolder.name.setText(dishesInfo.name);
			viewHolder.price.setText(mContext.getResources().getString(R.string.yuan)+ 
					dishesInfo.price);
			OrderedDishesInfo info = tempOrderInfo.findOrderedDishes(dishesInfo.id);
			viewHolder.numberView.setData(info == null ? "0" : info.orderNum,
					dishesInfo.id, handler);
			viewHolder.soldOut.setVisibility(dishesInfo.isSoldOut ? View.VISIBLE : View.GONE);
			viewHolder.numberView.setEnable(dishesInfo.isSoldOut ? false : true);
		}
			
		return convertView;
	}
	
	public static class ViewHolder {
		TextView name;
		TextView price;
		NumberView numberView; 
		ImageView split;
		ImageView soldOut;
	}
}
