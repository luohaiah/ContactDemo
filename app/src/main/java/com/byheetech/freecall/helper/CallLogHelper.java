package com.byheetech.freecall.helper;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.support.v4.content.ContextCompat;

import com.byheetech.freecall.base.BaseFragment;
import com.byheetech.freecall.fragment.CallFragment;
import com.byheetech.freecall.model.BeanCallLog;
import com.byheetech.freecall.utils.Numbercity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/4/15.
 */
public class CallLogHelper extends ContextWrapper {

    private SimpleDateFormat format;

    public List<BeanCallLog> getCalllogList(BaseFragment context) {
        HashMap<String, BeanCallLog> map = new HashMap<>();
        Numbercity nc = new Numbercity();
        List<BeanCallLog> list_bCallLogs = new ArrayList<BeanCallLog>();
        String[] projection = {CallLog.Calls.DATE, // 通话时间
                CallLog.Calls.NUMBER,// 电话号码
                CallLog.Calls.TYPE, // 通话类型，主拨，被叫，未接
                CallLog.Calls.CACHED_NAME,// 显示名称
                CallLog.Calls.DURATION, // 通话时长
                CallLog.Calls._ID}; // 查询的列
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            context.requestPermissions(new String[]{Manifest.permission.READ_CALL_LOG}, CallFragment.READ_CALL_LOG_REQUEST_CODE);
            return list_bCallLogs;
        }
        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, null,
                null, CallLog.Calls.DEFAULT_SORT_ORDER);
        int id;
        String name;// 名称
        String number;// 号码
        String date_str;// 日期
        int type;// 来电:1,拨出:2,未接:3
        int duration; // 通话时长
        while (cursor.moveToNext()) {
            Date date = new Date(cursor.getLong(cursor
                    .getColumnIndex(CallLog.Calls.DATE)));
            number = cursor.getString(cursor
                    .getColumnIndex(CallLog.Calls.NUMBER));
            type = cursor.getInt(cursor.getColumnIndex(CallLog.Calls.TYPE));
            name = cursor.getString(cursor
                    .getColumnIndex(CallLog.Calls.CACHED_NAME));// 缓存的名称与电话号码，如果它的存在
            id = cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID));
            duration = cursor.getInt(cursor
                    .getColumnIndex(CallLog.Calls.DURATION));
            date_str = format.format(date);
            BeanCallLog clb = new BeanCallLog();
            clb.setId(id);
            if (name == null) {
                name = "";
            }
            clb.setName(name);
            clb.setNumber(number);
            clb.setDate(date);
            clb.setType(type);
            clb.setDuration(duration);
            clb.setCount(1);
            clb.setPlace(nc.getCityByNum(number));
            if (!number.equals("-1")) {// 未知的号码不加入通话记录里面
                if (map.containsKey(clb.getName() + clb.getType() + date_str)) {
                    BeanCallLog beanCallLog = map.get(clb.getName() + clb.getType() + date_str);
                    int count = beanCallLog.getCount() + 1;
                    beanCallLog.setCount(count);
                } else {
                    map.put(clb.getName() + clb.getType() + date_str, clb);
                }
            }
        }
        cursor.close();
        nc.close();
        list_bCallLogs.addAll(map.values());
        Collections.sort(list_bCallLogs, BeanCallLog.mDescComparator);
        return list_bCallLogs;
    }

    public CallLogHelper(Context base) {
        super(base);
        format = new SimpleDateFormat("MM-dd");
    }
}
