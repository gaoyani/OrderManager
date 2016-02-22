/*****************************************************
 * Copyright(c)2014-2015 ������Ϊ���˿Ƽ����޹�˾
 * SysApplication.java
 * �����ˣ�������
 * ��     �ڣ�2014-6-22
 * ��     ����ȫ����ݴ洢������
 * ��     ����v6.0
 *****************************************************/

package com.huiwei.ordermanager.common;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Application;
import android.content.Context;

import com.huiwei.commonlib.HanziToPinyin;
import com.huiwei.commonlib.HanziToPinyin.Token;
import com.huiwei.ordermanager.info.CategoryInfo;
import com.huiwei.ordermanager.info.DishesInfo;
import com.huiwei.ordermanager.info.OrderInfo;
import com.huiwei.ordermanager.info.OrderedDishesInfo;
import com.huiwei.ordermanager.info.PreferInfo;
import com.huiwei.ordermanager.info.TableInfo;

public class SysApplication extends Application {

//	public static int userID;
//	public static String sessionID;
	public static OrderInfo curOrderInfo = new OrderInfo();

	public static List<CategoryInfo> categoryList = new ArrayList<CategoryInfo>();
	public static Map<Integer, PreferInfo> preferList = new HashMap<Integer, PreferInfo>();
	public static List<TableInfo> tableList = new ArrayList<TableInfo>();
	public static List<OrderInfo> newOrderList = new ArrayList<OrderInfo>();
//	public static List<OrderInfo> confirmOrderList = new ArrayList<OrderInfo>();
	public static Map<String, List<OrderInfo>> confirmOrderList = new LinkedHashMap<String, List<OrderInfo>>();
	public static List<String> confirmTableIDList = new ArrayList<String>();
	public static Map<String, DishesInfo> dishesList = new LinkedHashMap<String, DishesInfo>();
	public static List<String> searchDishesIDList = new ArrayList<String>();
	
	public static void searchDishesGroup(int groupID) {
		searchDishesIDList.clear();
		if (groupID == 0) {
			for (String id : dishesList.keySet()) {  
				searchDishesIDList.add(id);
			} 
		} else {
			for (String id : dishesList.keySet()) {  
			    if (dishesList.get(id).category == groupID) {
			    	searchDishesIDList.add(id);
				}
			}  
		}
	}
	
	public static void resetSoldOut() {
		for (String id : dishesList.keySet()) {  
			dishesList.get(id).isSoldOut = false;
		}
	}
	
	public static void searchDishesName(String name) {
		searchDishesIDList.clear();
		for (String id : dishesList.keySet()) {  
			DishesInfo info = dishesList.get(id);
		    if (info.name.toUpperCase().contains(name) || 
		    		info.quanpin.toUpperCase().contains(name) ||
		    		info.jianpin.toUpperCase().contains(name)) {
		    	searchDishesIDList.add(id);
			}
		}  
	}
	
	public static int searchPeopleNum(String tableID) {
		if (!confirmOrderList.containsKey(tableID)) {
			return 0;
		}
		
		for (OrderInfo orderInfo : confirmOrderList.get(tableID)) {
			if (!orderInfo.isAddOrder && orderInfo.tableId.compareTo(tableID) == 0) {
				return orderInfo.peopleNum;
			}
		}
		
		return 0;
	}

	public static void clearSearch() {
		searchDishesIDList.clear();
		for (String id : dishesList.keySet()) {  
			searchDishesIDList.add(id);
		} 
	}

	public static int getNewOrderNum() {
		int num = 0;
		for (OrderInfo info : newOrderList) {
			if(info.status == OrderInfo.NEW) {
				num++;
			}
		}
		
		return num;
	}
	
	public static void clearTableList() {
		for (TableInfo info : tableList) {
			info = null;
		}
		
		tableList.clear();
	}

	public static void clearOrderList() {
		for (OrderInfo info : newOrderList) {
			info = null;
		}
		
//		for (String tableId : confirmOrderList.keySet()) {
//			confirmOrderList.get(tableId).clear();
//		}
		
		newOrderList.clear();
		confirmOrderList.clear();
		confirmTableIDList.clear();
	}

	public static boolean hasSoldOutDishes() {
		for (OrderedDishesInfo dishesInfo : curOrderInfo.dishesInfo) {
			if (dishesInfo.dishes.isSoldOut
					&& Integer.parseInt(dishesInfo.orderNum) > 0) {
				return true;
			}
		}

		return false;
	}

	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			String pkName = context.getPackageName();
			versionName = context.getPackageManager()
 					.getPackageInfo(pkName, 0).versionName;
			if (versionName == null) {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return versionName;
	}

	public static String getFullPinYin(String source) {
		if (!Arrays.asList(Collator.getAvailableLocales()).contains(
				Locale.CHINA)) {
			return source;
		}

		ArrayList<Token> tokens = HanziToPinyin.getInstance().get(source);
		if (tokens == null || tokens.size() == 0) {
			return source;
		}

		StringBuffer result = new StringBuffer();
		for (Token token : tokens) {
			if (token.type == Token.PINYIN) {
				result.append(token.target);
			} else {
				result.append(token.source);
			}
		}

		return result.toString();
	}

	public static String getFirstPinYin(String source) {
		if (!Arrays.asList(Collator.getAvailableLocales()).contains(
				Locale.CHINA)) {
			return source;
		}

		ArrayList<Token> tokens = HanziToPinyin.getInstance().get(source);
		if (tokens == null || tokens.size() == 0) {
			return source;
		}

		StringBuffer result = new StringBuffer();
		for (Token token : tokens) {
			if (token.type == Token.PINYIN) {
				result.append(token.target.charAt(0));
			} else {
				result.append("#");
			}
		}

		return result.toString();
	}
}
