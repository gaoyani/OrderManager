package com.huiwei.ordermanager.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huiwei.ordermanager.R;
import com.huiwei.ordermanager.info.CategoryInfo;

public class CategoryItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private int selectItemId;
	private List<CategoryInfo> categoryList = new ArrayList<CategoryInfo>();

	public CategoryItemAdapter(Context context) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
	}

	public void setSelectItemID(int id) {
		selectItemId = id;
	}
	
	public void setData(List<CategoryInfo> categoryList) {
		if (categoryList != null) {
			this.categoryList.clear();
			this.categoryList.addAll(categoryList);
			notifyDataSetChanged();
		}
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return categoryList.size();
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
			convertView = mInflater.inflate(R.layout.category_item, null);
			
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.tv_menu_name);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		viewHolder.name.setText(categoryList.get(position).name);
		
		if (position == selectItemId) {
			convertView.setBackgroundResource(R.drawable.background);
		} else {
			convertView.setBackgroundColor(mContext.
					getResources().getColor(R.color.transparent));
		}
		
		return convertView;
	}
	
	public static class ViewHolder {
		TextView name;
	}
}
