package com.byheetech.freecall.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/** 
 * 类说明 - SharedPreference工具类 
 * @author  zhwei
 * @version V1.0  创建时间：2013-12-24 下午1:34:48 
 */
public class SharedPreferenceUtil {
	private static SharedPreferenceUtil mSharedPreferencesUitl;
	
	private SharedPreferences mSharedPreferences;
	private Editor editor;
	private SharedPreferenceUtil(Context context) {
		String fileName = context.getPackageName() + "_shared";
		mSharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		editor = mSharedPreferences.edit();
	}
	
	public static synchronized SharedPreferenceUtil getInstance(Context context) {
		if(mSharedPreferencesUitl == null) {
			mSharedPreferencesUitl = new SharedPreferenceUtil(context);
		}
		return mSharedPreferencesUitl;
	}
	
	
	public void putString(String key, String value) {
		editor.putString(key, value);
	}
	
	public void putBoolean(String key, boolean value) {
		editor.putBoolean(key, value);
	}
	
	public void putInt(String key, int value) {
		editor.putInt(key, value);
	}
	
	public void putFloat(String key, float value) {
		editor.putFloat(key, value);
	}
	
	public void putString(String key, String value, boolean commit) {
		editor.putString(key, value);
		if(commit)
			editor.commit();
	}
	
	public void putBoolean(String key, boolean value, boolean commit) {
		editor.putBoolean(key, value);
		if(commit)
			editor.commit();
	}
	
	public void putInt(String key, int value, boolean commit) {
		editor.putInt(key, value);
		if(commit)
			editor.commit();
	}
	
	public void putFloat(String key, float value, boolean commit) {
		editor.putFloat(key, value);
		if(commit)
			editor.commit();
	}
	
	public void commit() {
		editor.commit();
	}
	
	/**
	 * getString	default value ""
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		return mSharedPreferences.getString(key, "");
	}
	
	/**
	 * getint	default value 0
	 * @param key
	 * @return
	 */
	public int getInt(String key) {
		return mSharedPreferences.getInt(key, 0);
	}
	
	/**
	 * getString	default value false
	 * @param key
	 * @return
	 */
	public boolean getBoolean(String key) {
		return mSharedPreferences.getBoolean(key, false);
	}
	
	/**
	 * getString	default value 0
	 * @param key
	 * @return
	 */
	public float getFloat(String key) {
		return mSharedPreferences.getFloat(key, 0);
	}
	
	public String getString(String key, String defaultValue) {
		return mSharedPreferences.getString(key, defaultValue);
	}
	
	public int getInt(String key, int defaultValue) {
		return mSharedPreferences.getInt(key, defaultValue);
	}
	
	public boolean getBoolean(String key, boolean defaultValue) {
		return mSharedPreferences.getBoolean(key, defaultValue);
	}
	
	public float getFloat(String key, float defaultValue) {
		return mSharedPreferences.getFloat(key, defaultValue);
	}
}
