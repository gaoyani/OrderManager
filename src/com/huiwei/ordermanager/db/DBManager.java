package com.huiwei.ordermanager.db;

import java.util.ArrayList;

import com.huiwei.ordermanager.constant.TablesConstant;
import com.huiwei.ordermanager.info.OrderInfo;

import android.R.bool;
import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

//public class DBManager {
//	private Context context = null;
//	private SQLiteDatabase database = null;
//	private DBHelper dbHelper = null;
//	
//	public DBManager(Context context) {
//		this.context = context;
//		dbHelper = DBHelper.getInstance(context);
//	}
//	
//	public synchronized void close() {
//		dbHelper.close();
//		System.out.println("database close");
//	}
//
//	public synchronized long add(String tableName, ContentValues values) {
//		database = dbHelper.getWritableDatabase();
//		long rowID = database.insert(tableName, null, values);
//		return rowID;
//	}
//	
//	public synchronized Cursor searchMenu(Cursor oldCursor, String condition) {
//		if (oldCursor != null)
//			oldCursor = null;
//		
//		String sql = "";
//		if (condition == null) {
//			sql = "select * from " + TablesConstant.TB_MENU;
//		} else {
//			sql = "select * from " + TablesConstant.TB_MENU + " where "
//					+ condition;
//		}
//		
//		database = dbHelper.getWritableDatabase();
//		Cursor cursor = database.rawQuery(sql, null);
//		return cursor;
//	}
//	
//	public synchronized Cursor searchOrder(Cursor oldCursor, String condition) {
//		if (oldCursor != null)
//			oldCursor = null;
//		
//		String sql = "";
//		if (condition == null) {
//			sql = "select * from " + TablesConstant.TB_ORDER;
//		} else {
//			sql = "select * from " + TablesConstant.TB_ORDER + " where "
//					+ condition;
//		}
//		
//		database = dbHelper.getWritableDatabase();
//		Cursor cursor = database.rawQuery(sql, null);
//		return cursor;
//	}
//	
//	public synchronized Cursor searchPrefer(Cursor oldCursor, String menuID) {
//		if (oldCursor != null)
//			oldCursor = null;
//		
//		String sql = "select * from " + TablesConstant.TB_MENU_PREFER + " where "
//				+ TablesConstant.DISHES_ID + "=" + menuID;
//
//		database = dbHelper.getWritableDatabase();
//		Cursor cursor = database.rawQuery(sql, null);
//		return cursor;
//	}
//	
//	public synchronized int getNewOrderNum() {
//		database = dbHelper.getWritableDatabase();
//		String sql = "select * from " + TablesConstant.TB_ORDER + " where "
//				+ TablesConstant.ORDER_STATUS + "=" + OrderInfo.NEW;
//		Cursor cursor = database.rawQuery(sql, null);
//		int count = cursor.getCount();
//		cursor.close();
//		return count;
//	}
//
//	public synchronized void executeSql(String sql) {
//		database = dbHelper.getWritableDatabase();
//		database.execSQL(sql);
//	}
//}
