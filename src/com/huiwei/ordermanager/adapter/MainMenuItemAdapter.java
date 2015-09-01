package com.huiwei.ordermanager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huiwei.ordermanager.R;

public class MainMenuItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private int selectItemId;

	public MainMenuItemAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
	}

	public void setSelectItemID(int id) {
		selectItemId = id;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 2;
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
			convertView = mInflater.inflate(R.layout.main_menu_item, null);
			
			viewHolder = new ViewHolder();
			viewHolder.icon = (ImageView) convertView.findViewById(R.id.iv_menu_icon);
			viewHolder.name = (TextView) convertView.findViewById(R.id.tv_menu_name);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		switch (position) {
		case 0:
			viewHolder.icon.setBackgroundResource(R.drawable.main_order);
			viewHolder.name.setText(mContext.getResources().getString(R.string.main_order));
			break;
			
//		case 1:
//			viewHolder.icon.setBackgroundResource(R.drawable.main_menu);
//			viewHolder.name.setText(mContext.getResources().getString(R.string.main_menu));
//			break;
			
		case 1:
			viewHolder.icon.setBackgroundResource(R.drawable.main_more);
			viewHolder.name.setText(mContext.getResources().getString(R.string.main_more_info));
			break;

		default:
			break;
		}
		
		if (position == selectItemId) {
//			convertView.setBackgroundColor(mContext.
//					getResources().getColor(R.color.selected));
			convertView.setBackgroundColor(Color.parseColor("#20000000"));
		} else {
			convertView.setBackgroundColor(mContext.
					getResources().getColor(R.color.transparent));
		}
		
		return convertView;
	}
	
	public static class ViewHolder {
		ImageView icon;
		TextView name;
	}
}
