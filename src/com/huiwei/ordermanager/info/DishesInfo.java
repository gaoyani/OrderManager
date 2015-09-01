package com.huiwei.ordermanager.info;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;

public class DishesInfo {
	public String id;
	public String name;
	public int category;
	public String price;
	public String vipPrice = "0";
	public String islimit = "0";
	public int preference;
	public boolean isSoldOut;
	public List<PreferInfo> preferList = new ArrayList<PreferInfo> ();
	public String preferHistory;
	public String otherPrefer;
	
	public String quanpin; //ping yin
	public String jianpin; //jian pin yin
	
	public void setPreferCheck(int id) {
		for (PreferInfo info : preferList) {
			if (info.id == id) {
				info.isChecked = true;
			}
		}
	}
	
	public void clone(DishesInfo info) {
		this.id = info.id;
		this.name = info.name;
		this.category = info.category;
		this.price = info.price;
		this.vipPrice = info.vipPrice;
		this.islimit = info.islimit;
		this.preference = info.preference;
		this.isSoldOut = info.isSoldOut;
		this.preferHistory = info.preferHistory;
		this.otherPrefer = info.otherPrefer;
	}
}
