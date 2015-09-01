/*****************************************************
 * Copyright(c)2014-2015 ������Ϊ���˿Ƽ����޹�˾
 * AddDishesItemAdapter.java
 * �����ˣ�������
 * ��     �ڣ�2014-6-23
 * ��     �����ӲͲ˵��б�������
 * ��     ����v6.0
 *****************************************************/

package com.huiwei.ordermanager.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.huiwei.ordermanager.info.DishesInfo;

public class DishesBaseAdapter extends BaseAdapter {

	protected LayoutInflater mInflater;
	protected Context mContext;
	protected Handler handler;
	protected Map<String, DishesInfo> dishesList = new HashMap<String, DishesInfo>();
	protected List<String> searchDishesIDList = new ArrayList<String>();
	
	public void setData(Map<String, DishesInfo> dishesList, List<String> searchDishesIDList) {
		this.dishesList.clear();
		this.dishesList.putAll(dishesList);
		this.searchDishesIDList.clear();
		this.searchDishesIDList.addAll(searchDishesIDList);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return searchDishesIDList.size()+1;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

}
