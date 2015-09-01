/*****************************************************
 * Copyright(c)2014-2015 北京汇为永兴科技有限公司
 * SysApplication.java
 * 创建人：高亚妮
 * 日     期：2014-6-22
 * 描     述：全局数据存储及操作
 * 版     本：v6.0
 *****************************************************/

package com.huiwei.ordermanager.common;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

public class SysApplication extends Application {

//	public static int userID;
//	public static String sessionID;
	public static OrderInfo curOrderInfo = new OrderInfo();

	public static List<CategoryInfo> categoryList = new ArrayList<CategoryInfo>();
	public static Map<Integer, PreferInfo> preferList = new HashMap<Integer, PreferInfo>();
	
	public static List<OrderInfo> newOrderList = new ArrayList<OrderInfo>();
	public static List<OrderInfo> confirmOrderList = new ArrayList<OrderInfo>();
	public static Map<String, DishesInfo> dishesList = new HashMap<String, DishesInfo>();
	public static List<String> searchDishesIDList = new ArrayList<String>();
	
	/*****************************************************
	 * 函数名：searchDishesGroup
	 * 输     入：int groupID -- 分类id
	 * 输     出：无
	 * 描     述：根据分类id筛选菜品，并存入搜索列表
	 * 创建人：高亚妮
	 * 日     期：2014-6-22
	 *****************************************************/
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
	
	/*****************************************************
	 * 函数名：searchDishesName
	 * 输     入：String name -- 菜品检索时的输入文本（包括菜品
	 * 		      名称，菜品名称的拼音，菜品名称的拼音首字母）
	 * 输     出：无
	 * 描     述：根据输入文本筛选菜品，并存入搜索列表
	 * 创建人：高亚妮
	 * 日     期：2014-6-22
	 *****************************************************/
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
	
	/*****************************************************
	 * 函数名：clearSearch
	 * 输     入：无
	 * 输     出：无
	 * 描     述：清空搜索列表
	 * 创建人：高亚妮
	 * 日     期：2014-6-22
	 *****************************************************/
	public static void clearSearch() {
		searchDishesIDList.clear();
		for (String id : dishesList.keySet()) {  
			searchDishesIDList.add(id);
		} 
	}
	
	/*****************************************************
	 * 函数名：clearSearch
	 * 输     入：无
	 * 输     出：新菜单的个数
	 * 描     述：获取订单中的新订单个数
	 * 创建人：高亚妮
	 * 日     期：2014-6-22
	 *****************************************************/
	public static int getNewOrderNum() {
		int num = 0;
		for (OrderInfo info : newOrderList) {
			if(info.status == OrderInfo.NEW) {
				num++;
			}
		}
		
		return num;
	}
	
	/*****************************************************
	 * 函数名：clearOrderList
	 * 输     入：无
	 * 输     出：无
	 * 描     述：清空订单列表
	 * 创建人：高亚妮
	 * 日     期：2014-6-22
	 *****************************************************/
	public static void clearOrderList() {
		for (OrderInfo info : newOrderList) {
			info = null;
		}
		
		for (OrderInfo info : confirmOrderList) {
			info = null;
		}
		
		newOrderList.clear();
		confirmOrderList.clear();
	}

	/*****************************************************
	 * 函数名：hasSoldOutDishes
	 * 输     入：无
	 * 输     出：boolean -- 是否有售罄菜品
	 * 描     述：检索菜单，是否存在售罄的菜品
	 * 创建人：高亚妮
	 * 日     期：2014-6-22
	 *****************************************************/
	public static boolean hasSoldOutDishes() {
		for (OrderedDishesInfo dishesInfo : curOrderInfo.dishesInfo) {
			if (dishesInfo.dishes.isSoldOut
					&& Integer.parseInt(dishesInfo.orderNum) > 0) {
				return true;
			}
		}

		return false;
	}

	/*****************************************************
	 * 函数名：getAppVersionName
	 * 输     入：Context context -- 上下文句柄
	 * 输     出：String -- app版本号
	 * 描     述：获取app的版本号
	 * 创建人：高亚妮
	 * 日     期：2014-6-22
	 *****************************************************/
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

	/*****************************************************
	 * 函数名：getFullPinYin
	 * 输     入：String source -- 中文字符串
	 * 输     出：String -- 全拼的拼音字符串
	 * 描     述：获取中文字符串的拼音
	 * 创建人：高亚妮
	 * 日     期：2014-6-22
	 *****************************************************/
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

	/*****************************************************
	 * 函数名：getFirstPinYin
	 * 输     入：String source -- 中文字符串
	 * 输     出：String -- 拼音首字母字符串
	 * 描     述：获取中文字符串的拼音首字母
	 * 创建人：高亚妮
	 * 日     期：2014-6-22
	 *****************************************************/
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
