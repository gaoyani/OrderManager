package com.huiwei.ordermanager.info;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;

public class OrderInfo {
	public static final int NEW = 0;
	public static final int ACCEPT = 1;
	public static final int CONFIRM = 2;
	public static final int FINASH = 3;
	
	public static final int TYPE_ORDER = 1;
	public static final int TYPE_CALLING = 2;
	
	public int id = -1;
	public String tableId;
	public String tableName;
	public String content = "";
	public String notes = "";
	public int status;
	public int type;
	public boolean isVip;
	public String waiterId;
	public boolean isAddOrder = false;
	public boolean isTakeOutOrder = false;
	
	public String contactName;
	public String contactNumber;
	public String contactAddress;
	public String sendType;
	public String sendTime;
	
	public List<OrderedDishesInfo> dishesInfo = 
			new ArrayList<OrderedDishesInfo>();

	public String getPrice() {
		return "50";
	}
	
	public int getDishesNum() {
		int totalNum = 0;
		for (OrderedDishesInfo info : dishesInfo) {
			int num = Integer.parseInt(info.orderNum);
			totalNum += num;
		}
		
		return totalNum;
	}
	
	public OrderedDishesInfo findOrderedDishes(String dishesId) {
		for (int i = 0; i < dishesInfo.size(); i++) {
			
			OrderedDishesInfo info = dishesInfo.get(i);
			if (info.dishes.id.equals("0")) {
				if (info.dishes.name.equals(dishesId))
					return info;
			} else {
				if (info.dishes.id.equals(dishesId))
					return info;
			}
		}
		
		return null;
	}
	
	public void clearDishesList() {
		for (OrderedDishesInfo info : dishesInfo) {
			info.dishes.preferList.clear();
			if (info != null)
				info = null;
			
			dishesInfo.remove(info);
		}
		
		dishesInfo.clear();
	}
}
