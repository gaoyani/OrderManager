/*****************************************************
 * Copyright(c)2014-2015 ������Ϊ���˿Ƽ����޹�˾
 * AddDishesItemAdapter.java
 * �����ˣ�������
 * ��     �ڣ�2014-6-23
 * ��     �����ӲͲ˵��б�������
 * ��     ����v6.0
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
	 * ������getView
	 * ��     �룺int position -- ����ͼ������������е�λ�� 
	 * 		   View convertView -- ����ͼ
	 * 			ViewGroup parent -- ����ͼ���ջᱻ���ӵ��ĸ�����ͼ
	 * ��     ����View -- ָ��position���б���ͼ
	 * ��     ������ȡ�б����ƶ�position��ʾ�ض���ݵ���ͼ
	 * �����ˣ�������
	 * ��     �ڣ�2014-6-23
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
					dishesInfo.id, tempOrderInfo.id, false, handler);
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
