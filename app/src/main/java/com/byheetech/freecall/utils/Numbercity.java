package com.byheetech.freecall.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Numbercity {

	private SQLiteDatabase db;

	public String getCityByNum(String telNum) {
		String ret = "";
		if (db == null) {
			System.out.println("初始化归属地db");
			initDB();
		}
		if (telNum.length() == 11)
			ret = mobile(telNum);
		else if (telNum.length() == 5)
			ret = fuwu(telNum);
		else if (telNum.length() == 12) {
			ret = zuoji(telNum);
		}
		return ret;
	}

	private String mobile(String telNum) {
		String ret = "";
		try {
			String code = telNum.substring(0, 7);
			Cursor cursor = null;
			String sql = "select c.city from phonenumberwithcity p left join citywithnumber c on p.city=c.uid where p.uid="
					+ code;
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				System.out.println();
				ret = cursor.getString(cursor.getColumnIndex("city"));
				break;
			}
			cursor.close();
		} catch (Exception e) {
		}
		return ret;
	}

	private String zuoji(String telNum) {
		String ret = "";
		try {
			String code = telNum.substring(1, 4);
			Cursor cursor = null;
			String sql = "select city from citywithnumber t where t.zone="
					+ code;
			cursor = db.rawQuery(sql, null);
			if (cursor.getCount() < 1) {
				return zuoji1(telNum);
			}
			while (cursor.moveToNext()) {
				ret = cursor.getString(cursor.getColumnIndex("city"));
				break;
			}
			cursor.close();
		} catch (Exception e) {
		}
		return ret;
	}

	private String zuoji1(String telNum) {
		String ret = "";
		try {
			String code = telNum.substring(1, 3);
			Cursor cursor = null;
			String sql = "select city from citywithnumber t where t.zone="
					+ code;
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				ret = cursor.getString(cursor.getColumnIndex("city"));
				break;
			}
			cursor.close();
		} catch (Exception e) {
		}

		return ret;
	}

	private String fuwu(String telNum) {
		String ret = "";
		try {
			Cursor cursor = null;
			String sql = "select c.city from phonenumberwithcity p left join citywithnumber c on p.city=c.uid where p.uid="
					+ telNum;
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				System.out.println();
				ret = cursor.getString(cursor.getColumnIndex("city"));
				break;
			}
			cursor.close();
		} catch (Exception e) {
		}
		return ret;
	}

	private void initDB() {
		// 获取管理对象，因为数据库需要通过管理对象才能够获取
		AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
		db = mg.getDatabase("location_Numbercity_citynumber.db");
	}

	public void close() {
		if (db != null)
			db.close();
	}
}
