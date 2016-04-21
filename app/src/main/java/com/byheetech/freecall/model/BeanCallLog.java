package com.byheetech.freecall.model;

import java.text.Collator;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class BeanCallLog {
    private int id;
    private String name;// 名称
    private String number;// 号码
    private Date date;// 日期
    private int type;// 来电:1,拨出:2,未接:3
    private String place; // 归属地
    private int duration;// 通话时长
    private int count;//同一天同类型出现的次数

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private static Comparator<Object> mChineseComparator = Collator.getInstance(Locale.CHINA);
    public static Comparator<BeanCallLog> mDescComparator = new Comparator<BeanCallLog>() {

        @Override
        public int compare(BeanCallLog lhs, BeanCallLog rhs) {
            return mChineseComparator.compare(String.valueOf(rhs.getDate().getTime()), String.valueOf(lhs.getDate().getTime()));
        }
    };
}
