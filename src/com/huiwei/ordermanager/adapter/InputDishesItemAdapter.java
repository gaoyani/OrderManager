package com.huiwei.ordermanager.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huiwei.ordermanager.R;
import com.huiwei.ordermanager.info.OrderedDishesInfo;

public class InputDishesItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private List<OrderedDishesInfo> dishesList = null;

	public InputDishesItemAdapter(Context context, List<OrderedDishesInfo> dishesList) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		this.dishesList = dishesList;
	}

	@Override
	public int getCount() {
		if (dishesList == null)
			return 0;
		
		return dishesList.size();
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
			convertView = mInflater.inflate(R.layout.input_dishes_item, null);
			
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.tv_dishes_name);
			viewHolder.price = (TextView) convertView.findViewById(R.id.tv_dishes_price);
			viewHolder.delete = (ImageView) convertView.findViewById(R.id.iv_delete);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		OrderedDishesInfo info = dishesList.get(position);
		viewHolder.name.setText(info.dishes.name);
		viewHolder.price.setText(convertView.getResources().getString(
				R.string.yuan) + info.dishes.price);
		viewHolder.delete.setTag(position);
		viewHolder.delete.setOnClickListener(deleteClickListener);

		return convertView;
	}
	
	OnClickListener deleteClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			OrderedDishesInfo info = dishesList.get((Integer) v.getTag());
			dishesList.remove(info);
			info = null;
			notifyDataSetChanged();
		}
	};
	
	public static class ViewHolder {
		TextView name;
		TextView price;
		ImageView delete;
	}
}
