/*****************************************************
 * Copyright(c)2014-2015 ������Ϊ���˿Ƽ����޹�˾
 * OrderDetailItemAdapter.java
 * �����ˣ�������
 * ��     �ڣ�2014-6-23
 * ��     �������������б�������
 * ��     ����v6.0
 *****************************************************/

package com.huiwei.ordermanager.adapter;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huiwei.ordermanager.R;
import com.huiwei.ordermanager.baseclass.SubListView;
import com.huiwei.ordermanager.info.DishesInfo;
import com.huiwei.ordermanager.info.OrderInfo;
import com.huiwei.ordermanager.info.OrderedDishesInfo;
import com.huiwei.ordermanager.info.PreferInfo;
import com.huiwei.ordermanager.view.NumberView;

public class ConfirmOrderDetialItemAdapter extends BaseExpandableListAdapter {
	private Context context;
	private List<OrderInfo> orderList;
	private LayoutInflater inflater;
	private Handler dishesChangeHandler;

	public ConfirmOrderDetialItemAdapter(Context context, Handler dishesChangeHandler) {
		this.context = context;
		this.dishesChangeHandler = dishesChangeHandler;
		inflater = LayoutInflater.from(context);
	}
	
	public void setData(List<OrderInfo> orderList) {
		this.orderList = orderList;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return orderList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		// return childList.get(groupPosition).size();
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return orderList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return orderList.get(groupPosition).dishesInfo.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View groupView = null;
		if (convertView == null) {
			groupView = newGroupView(parent);
		} else {
			groupView = convertView;
		}
		bindGroupView(groupPosition, groupView);
		return groupView;
	}

	private View newGroupView(ViewGroup parent) {
		return inflater.inflate(R.layout.group_item, null);
	}

	private void bindGroupView(int groupPosition, View groupView) {
		TextView tv = (TextView) groupView.findViewById(R.id.tv_group_name);
		tv.setText(groupView.getResources().getString(R.string.order_id)+
				orderList.get(groupPosition).content);
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View childView = null;
		if (convertView == null) {
			childView = newChildView(parent, groupPosition);
		} else {
			childView = convertView;
		}
		bindChildView(groupPosition, childPosition, childView);
		return childView;
	}

	private View newChildView(ViewGroup parent, final int groupPosition) {
		View v = inflater.inflate(R.layout.child_view, null);
		SubListView listView = (SubListView) v.findViewById(R.id.lv_order_detail);
		
		// final SubListAdapter adapter = new
		// SubListAdapter(treeNodes.get(groupPosition).childs,layoutInflater);
		final OrderDetailItemAdapter adapter = new OrderDetailItemAdapter(
				context, dishesChangeHandler);
		adapter.setData(orderList.get(groupPosition), (orderList.size() == groupPosition+1));
		listView.setAdapter(adapter);// 设置菜单Adapter
//		listView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				// TODO Auto-generated method stub
//				// if(position ==
//				// treeNodes.get(groupPosition).childs.size()){//foot的点击事件处理
//				// treeNodes.get(groupPosition).childs.add("New Add");
//				// adapter.notifyDataSetChanged();
//				// }else{
//				// Toast.makeText(parentContext, "当前选中的是:" + position,
//				// Toast.LENGTH_SHORT).show();
//				// }
//				if (position == childList.get(groupPosition).size()) {// foot的点击事件处理
//					childList.get(groupPosition).add("New Add");
//					adapter.notifyDataSetChanged();
//				} else {
//					Toast.makeText(ExpandableListViewActivity.this,
//							"当前选中的是:" + position, Toast.LENGTH_SHORT)
//							.show();
//				}
//			}
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				// TODO Auto-generated method stub
//				
//			}
//
//		});
		return v;
	}

	private void bindChildView(int groupPosition, int childPosition,
			View groupView) {
//		TextView tv = (TextView) groupView.findViewById(R.id.name_text);
//		tv.setText(childList.get(groupPosition).get(childPosition));
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

}