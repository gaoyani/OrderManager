package com.huiwei.ordermanager.db;

import com.huiwei.ordermanager.constant.TablesConstant;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//public class DBHelper extends SQLiteOpenHelper {
//
//	public DBHelper(Context context) {
//		super(context, "wireles_sorder.db", null, 4);
//	}
//	
//	private volatile static DBHelper dbHelper;  
//    public static DBHelper getInstance(Context context) {  
//      if (dbHelper == null) {  
//        synchronized (DBHelper.class) {  
//          if (dbHelper == null) {  
//        	  dbHelper = new DBHelper(context);  
//          }  
//        }  
//      }  
//      return dbHelper;  
//    }
//
//	@Override
//	public void onCreate(SQLiteDatabase db) {
//		createMenuTable(db);
//		createOrderTable(db);
//		createPreferTable(db);
//	}
//
//	@Override
//	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		
//		if (newVersion != oldVersion) {
//			db.execSQL("drop table " + TablesConstant.TB_MENU);
//			createMenuTable(db);
//			db.execSQL("drop table " + TablesConstant.TB_ORDER);
//			createOrderTable(db);
//			db.execSQL("drop table " + TablesConstant.TB_MENU_PREFER);
//			createPreferTable(db);
//		}
//	}
//
//	private void createMenuTable(SQLiteDatabase db) {
//		String sql_menu = "create table " + TablesConstant.TB_MENU + " ( " 
//				+ TablesConstant.ID + " integer primary key , "
//				+ TablesConstant.DISHES_ID + " text , "
//				+ TablesConstant.DISHES_NAME + " text , "
//				+ TablesConstant.DISHES_PRICE + " text , "
//				+ TablesConstant.DISHES_GROUP_ID + " INTEGER, "
//				+ TablesConstant.DISHES_SOLD_OUT + " INTEGER, " 
//				+ TablesConstant.DISHES_VIP_PRICE + " text , "
//				+ TablesConstant.DISHES_IS_LIMIT + " text , "
//				+ TablesConstant.DISHES_NAME_QUANPIN + " text , "
//				+ TablesConstant.DISHES_NAME_JIANPIN + " text )";
//		db.execSQL(sql_menu);
//	}
//	
//	private void createOrderTable(SQLiteDatabase db) {
//		String sql_order = "create table " + TablesConstant.TB_ORDER + " ( " 
//				+ TablesConstant.ID + " integer primary key , "
//				+ TablesConstant.ORDER_TABLE_ID + " text , "
//				+ TablesConstant.ORDER_TABLE_NAME + " text , "
//				+ TablesConstant.ORDER_CONTENT + " text , "
//				+ TablesConstant.ORDER_TYPE + " INTEGER, "
//				+ TablesConstant.ORDER_STATUS + " INTEGER, "
//				+ TablesConstant.ORDER_IS_VIP + " INTEGER) ";
//		db.execSQL(sql_order);
//	}
//	
//	private void createPreferTable(SQLiteDatabase db) {
//		String sql_menu_prefer = "create table " + TablesConstant.TB_MENU_PREFER + " ( " 
//				+ TablesConstant.ID + " integer primary key , "
//				+ TablesConstant.DISHES_ID + " text , "
//				+ TablesConstant.PREFER_ID + " INTEGER ) ";
//		db.execSQL(sql_menu_prefer);
//	}
//}
