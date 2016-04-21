package com.byheetech.freecall.model;

import android.text.TextUtils;

import com.pinyinsearch.model.PinyinSearchUnit;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by Administrator on 2016/4/6.
 */
public class BeanContact extends BaseContact {
    private BeanContact mNextContacts;//当一个联系人有多个号码时，第一个号码的下一个联系人
    private String mSortKey; // 排序的键
    private PinyinSearchUnit mNamePinyinSearchUnit;// save the mName converted to Pinyin characters.
    private boolean mFirstMultipleContacts;//whether the first multiple Contacts
    private boolean mHideMultipleContacts;        //whether hide multiple contacts
    private boolean mBelongMultipleContactsPhone; //whether belong multiple contacts phone, the value of the variable will not change once you set.
    private String mMatchQwertyKeywords;//传统键盘搜索关键字
    private String mMatchT9Keywords;//T9键盘搜索关键字
    private SearchByType searchByType = SearchByType.SearchByNull;

    public String getmMatchT9Keywords() {
        return mMatchT9Keywords;
    }

    public void setmMatchT9Keywords(String mMatchT9Keywords) {
        this.mMatchT9Keywords = mMatchT9Keywords;
    }

    public SearchByType getSearchByType() {
        return searchByType;
    }

    public void setSearchByType(SearchByType searchByType) {
        this.searchByType = searchByType;
    }

    public enum SearchByType {
        SearchByNull, SearchByName, SearchByPhoneNumber
    }

    public String getmMatchKeywords() {
        return mMatchQwertyKeywords;
    }

    public void setmMatchKeywords(String mMatchQwertyKeywords) {
        this.mMatchQwertyKeywords = mMatchQwertyKeywords;
    }

    public BeanContact(String id, String name, String phoneNumber) {
        this.setmId(id);
        this.setmName(name);
        this.setmPhoneNumber(phoneNumber);
        setmNamePinyinSearchUnit(new PinyinSearchUnit(name));
    }

    public boolean ismBelongMultipleContactsPhone() {
        return mBelongMultipleContactsPhone;
    }

    public void setmBelongMultipleContactsPhone(boolean mBelongMultipleContactsPhone) {
        this.mBelongMultipleContactsPhone = mBelongMultipleContactsPhone;
    }

    public boolean ismHideMultipleContacts() {
        return mHideMultipleContacts;
    }

    public void setmHideMultipleContacts(boolean mHideMultipleContacts) {
        this.mHideMultipleContacts = mHideMultipleContacts;
    }

    public boolean ismFirstMultipleContacts() {
        return mFirstMultipleContacts;
    }

    public void setmFirstMultipleContacts(boolean mFirstMultipleContacts) {
        this.mFirstMultipleContacts = mFirstMultipleContacts;
    }

    public PinyinSearchUnit getmNamePinyinSearchUnit() {
        return mNamePinyinSearchUnit;
    }

    public void setmNamePinyinSearchUnit(PinyinSearchUnit mNamePinyinSearchUnit) {
        this.mNamePinyinSearchUnit = mNamePinyinSearchUnit;
    }

    public String getmSortKey() {
        return mSortKey;
    }

    public void setmSortKey(String mSortKey) {
        this.mSortKey = mSortKey;
    }

    public BeanContact getmNextContacts() {
        return mNextContacts;
    }

    public void setmNextContacts(BeanContact mNextContacts) {
        this.mNextContacts = mNextContacts;
    }

    public BeanContact addMultipleContact(BeanContact contacts, String phoneNumber) {
        do {
            if ((TextUtils.isEmpty(phoneNumber)) || (null == contacts)) {
                break;
            }

            BeanContact currentContact = null;
            BeanContact nextContacts = null;
            for (nextContacts = contacts; null != nextContacts; nextContacts = nextContacts.getmNextContacts()) {
                currentContact = nextContacts;
                if (nextContacts.getmPhoneNumber().equals(phoneNumber)) {
                    break;
                }
            }
            BeanContact cts = null;
            if (null == nextContacts) {
                BeanContact cs = currentContact;
                cts = new BeanContact(cs.getmId(), cs.getmName(), phoneNumber);
                cts.setmSortKey(cs.getmSortKey());
                cts.setmNamePinyinSearchUnit(cs.getmNamePinyinSearchUnit());// not deep copy
                cts.setmFirstMultipleContacts(false);
                cts.setmHideMultipleContacts(true);
                cts.setmBelongMultipleContactsPhone(true);
                cs.setmBelongMultipleContactsPhone(true);
                cs.setmNextContacts(cts);
            }

            return cts;
        } while (false);
        return null;
    }

    private static Comparator<Object> mChineseComparator = Collator.getInstance(Locale.CHINA);
    public static Comparator<BeanContact> mAscComparator = new Comparator<BeanContact>() {

        @Override
        public int compare(BeanContact lhs, BeanContact rhs) {
            return mChineseComparator.compare(lhs.mSortKey, rhs.mSortKey);
        }
    };

}
