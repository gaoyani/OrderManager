package com.huiwei.ordermanager.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huiwei.ordermanager.R;
import com.huiwei.ordermanager.common.SysApplication;
import com.huiwei.ordermanager.info.DishesInfo;

public class DishesItemAdapter extends DishesBaseAdapter {

	public DishesItemAdapter(Context context, Handler handler) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		this.handler = handler;
	}
	
	@Override
	public int getCount() {
		return searchDishesIDList.size();
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
			convertView = mInflater.inflate(R.layout.dishes_menu_item, null);
			
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.tv_dishes_name);
			viewHolder.price = (TextView) convertView.findViewById(R.id.tv_dishes_price);
			viewHolder.soldOut = (ImageView) convertView.findViewById(R.id.iv_sold_out);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		DishesInfo dishesInfo = dishesList.get(
				searchDishesIDList.get(position));
		viewHolder.name.setText(dishesInfo.name);
		viewHolder.price.setText(mContext.getResources().getString(R.string.yuan)+ dishesInfo.price);
		viewHolder.soldOut.setVisibility(dishesInfo.isSoldOut ? View.VISIBLE : View.GONE);

		return convertView;
	}
	
	public static class ViewHolder {
		TextView name;
		TextView price;
		ImageView soldOut;
	}
}
