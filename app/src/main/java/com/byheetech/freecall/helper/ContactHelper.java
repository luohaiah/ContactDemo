package com.byheetech.freecall.helper;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

import com.byheetech.freecall.model.BeanContact;
import com.byheetech.freecall.model.BeanContacts;
import com.byheetech.freecall.utils.RxBus;
import com.byheetech.freecall.view.QuickAlphabeticBar;
import com.pinyinsearch.model.PinyinSearchUnit;
import com.pinyinsearch.util.PinyinUtil;
import com.pinyinsearch.util.QwertyUtil;
import com.pinyinsearch.util.T9Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 联系人帮助者
 * Created by 西瓜 on 2016/3/31.
 */
public class ContactHelper {
    private static final String TAG = "ContactsHelper";
    private static ContactHelper mHelper;
    private List<BeanContact> mBaseContactList;//联系人集合

    public List<BeanContact> getBaseContactList() {
        return mBaseContactList;
    }

    public void setBaseContactList(List<BeanContact> baseContactList) {
        this.mBaseContactList = baseContactList;
    }

    private ContactHelper() {
    }

    public static ContactHelper getInstance() {
        if (mHelper == null) {
            mHelper = new ContactHelper();
        }
        return mHelper;
    }

    public void loadContact(final Context context) {
        Observable<List<BeanContact>> observable = Observable.fromCallable(new Callable<List<BeanContact>>() {
            @Override
            public List<BeanContact> call() throws Exception {
                mBaseContactList = getBaseContactList(context);
                ContactIndexHelper.getInstance().parseContact(mBaseContactList);
                return mBaseContactList;
            }
        });
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<BeanContact>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<BeanContact> beanBeanContact) {
                        BeanContacts beanContacts = new BeanContacts();
                        beanContacts.setList(beanBeanContact);
                        RxBus.getDefault().post(beanContacts);
                    }
                });
    }

    private List<BeanContact> getBaseContactList(Context context) {
        List<BeanContact> kanjiStartBeanContact = new ArrayList<BeanContact>();
        HashMap<String, BeanContact> kanjiStartBeanContactHashMap = new HashMap<String, BeanContact>();
        List<BeanContact> nonKanjiStartBeanContact = new ArrayList<BeanContact>();
        HashMap<String, BeanContact> nonKanjiStartBeanContactHashMap = new HashMap<String, BeanContact>();
        List<BeanContact> beanContactList = new ArrayList<BeanContact>();
        BeanContact cs = null;
        Cursor cursor = null;
        String sortkey = null;
        long startLoadTime = System.currentTimeMillis();
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
        try {
            cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, "sort_key");
            int idColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
            int dispalyNameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            while (cursor.moveToNext()) {
                String id = cursor.getString(idColumnIndex);
                String displayName = cursor.getString(dispalyNameColumnIndex);
                String phoneNumber = cursor.getString(numberColumnIndex);
                boolean kanjiStartBeanContactExist = kanjiStartBeanContactHashMap.containsKey(id);
                boolean nonKanjiStartBeanContactExist = nonKanjiStartBeanContactHashMap.containsKey(id);
                if (true == kanjiStartBeanContactExist) {
                    cs = kanjiStartBeanContactHashMap.get(id);
                    cs.addMultipleContact(cs, phoneNumber);
                } else if (true == nonKanjiStartBeanContactExist) {
                    cs = nonKanjiStartBeanContactHashMap.get(id);
                    cs.addMultipleContact(cs, phoneNumber);
                } else {
                    cs = new BeanContact(id, displayName, phoneNumber);
                    PinyinUtil.parse(cs.getmNamePinyinSearchUnit());
                    sortkey = PinyinUtil.getSortKey(cs.getmNamePinyinSearchUnit()).toUpperCase();
                    cs.setmSortKey(praseSortKey(sortkey));
                    boolean isKanji = PinyinUtil.isKanji(cs.getmName().charAt(0));
                    if (true == isKanji) {
                        kanjiStartBeanContactHashMap.put(id, cs);
                    } else {
                        nonKanjiStartBeanContactHashMap.put(id, cs);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor) {
                cursor.close();
                cursor = null;
            }
        }
        kanjiStartBeanContact.addAll(kanjiStartBeanContactHashMap.values());
        Collections.sort(kanjiStartBeanContact, BeanContact.mAscComparator);
        nonKanjiStartBeanContact.addAll(nonKanjiStartBeanContactHashMap.values());
        Collections.sort(nonKanjiStartBeanContact, BeanContact.mAscComparator);
        beanContactList.addAll(kanjiStartBeanContact);
        //merge nonKanjiStartBeanContact and kanjiStartBeanContact
        int lastIndex = 0;
        boolean shouldBeAdd = false;
        for (int i = 0; i < nonKanjiStartBeanContact.size(); i++) {
            String nonKanfirstLetter = PinyinUtil.getFirstLetter(nonKanjiStartBeanContact.get(i).getmNamePinyinSearchUnit());
            int j = 0;
            for (j = 0 + lastIndex; j < beanContactList.size(); j++) {
                String firstLetter = PinyinUtil.getFirstLetter(beanContactList.get(j).getmNamePinyinSearchUnit());
                lastIndex++;
                if (firstLetter.charAt(0) > nonKanfirstLetter.charAt(0)) {
                    shouldBeAdd = true;
                    break;
                } else {
                    shouldBeAdd = false;
                }
            }
            if (lastIndex >= beanContactList.size()) {
                lastIndex++;
                shouldBeAdd = true;
            }
            if (true == shouldBeAdd) {
                beanContactList.add(j, nonKanjiStartBeanContact.get(i));
                shouldBeAdd = false;
            }
        }
        long endLoadTime = System.currentTimeMillis();
        Log.i(TAG, "endLoadTime-startLoadTime=[" + (endLoadTime - startLoadTime) + "] BeanContact.size()=" + beanContactList.size());
        return beanContactList;
    }

    private String praseSortKey(String sortKey) {
        if (null == sortKey || sortKey.length() <= 0) {
            return null;
        }

        if ((sortKey.charAt(0) >= 'a' && sortKey.charAt(0) <= 'z')
                || (sortKey.charAt(0) >= 'A' && sortKey.charAt(0) <= 'Z')) {
            return sortKey;
        }

        return String.valueOf(QuickAlphabeticBar.DEFAULT_INDEX_CHARACTER)
                + sortKey;
    }

    /**
     * 通过手机号码或者姓氏搜索联系人
     *
     * @param keyword
     */
    /**
     * @param keyword
     * @return void
     * @description search base data according to string parameter
     */
    public List<BeanContact> qwertySearch(String keyword) {
        List<BeanContact> mSearchContacts = new ArrayList<BeanContact>();
        if (TextUtils.isEmpty(keyword)) {// add all base data to search
            for (int i = 0; i < mBaseContactList.size(); i++) {
                BeanContact currentContacts = null;
                for (currentContacts = mBaseContactList.get(i); null != currentContacts; currentContacts = currentContacts.getmNextContacts()) {
                    currentContacts.setSearchByType(BeanContact.SearchByType.SearchByNull);
                    currentContacts.setmMatchKeywords("");
                    if (true == currentContacts.ismFirstMultipleContacts()) {
                        mSearchContacts.add(currentContacts);
                    } else {
                        if (false == currentContacts.ismHideMultipleContacts()) {
                            mSearchContacts.add(currentContacts);
                        }
                    }
                }
            }
            return mSearchContacts;
        }
        int contactsCount = mBaseContactList.size();
        /**
         * search process: 1:Search by name (1)Search by original name (2)Search
         * by name pinyin characters(original name->name pinyin characters)
         * 2:Search by phone number
         */
        for (int i = 0; i < contactsCount; i++) {
            PinyinSearchUnit namePinyinSearchUnit = mBaseContactList.get(i).getmNamePinyinSearchUnit();
            if (true == QwertyUtil.match(namePinyinSearchUnit, keyword)) {// search by name;
                BeanContact currentContacts = null;
                currentContacts = mBaseContactList.get(i);
                currentContacts.setSearchByType(BeanContact.SearchByType.SearchByName);
                currentContacts.setmMatchKeywords(namePinyinSearchUnit.getMatchKeyword().toString());
                mSearchContacts.add(currentContacts);

//                BeanContact firstContacts = null;
//                for (currentContacts = mBaseContactList.get(i), firstContacts = currentContacts; null != currentContacts; currentContacts = currentContacts.getmNextContacts()) {
//                    currentContacts.setSearchByType(BeanContact.SearchByType.SearchByName);
//                    currentContacts.setmMatchKeywords(namePinyinSearchUnit.getMatchKeyword().toString());
////                    currentContacts.setMatchStartIndex(firstContacts.getName().indexOf(firstContacts.getMatchKeywords().toString()));
////                    currentContacts.setMatchLength(firstContacts.getMatchKeywords().length());
//                    mSearchContacts.add(currentContacts);
//                }

                continue;
            } else {
                BeanContact currentContacts = null;
                for (currentContacts = mBaseContactList.get(i); null != currentContacts; currentContacts = currentContacts.getmNextContacts()) {
                    if (currentContacts.getmPhoneNumber().contains(keyword)) {// search by phone number
                        currentContacts.setSearchByType(BeanContact.SearchByType.SearchByPhoneNumber);
                        currentContacts.setmMatchKeywords(keyword);
                        mSearchContacts.add(currentContacts);
                    }
                }
                continue;
            }
        }

        if (mSearchContacts.size() <= 0) {
        } else {
            Collections.sort(mSearchContacts, BeanContact.mAscComparator);
        }
        return mSearchContacts;
    }

    /**
     * @param keyword (valid characters include:'0'~'9','*','#')
     * @return void
     * @description search base data according to string parameter
     */
    public List<BeanContact> t9InputSearch(String keyword) {
        List<BeanContact> mSearchByNameContacts = new ArrayList<BeanContact>();
        List<BeanContact> mSearchByPhoneNumberContacts = new ArrayList<BeanContact>();
        List<BeanContact> mSearchContacts = new ArrayList<BeanContact>();
        if (null == keyword) {// add all base data to search

            return mSearchContacts;
        }
        int contactsCount = mBaseContactList.size();

        /**
         * search process: 1:Search by name (1)Search by name pinyin
         * characters(org name->name pinyin characters) ('0'~'9','*','#')
         * (2)Search by org name ('0'~'9','*','#') 2:Search by phone number
         * ('0'~'9','*','#')
         */
        for (int i = 0; i < contactsCount; i++) {
            PinyinSearchUnit namePinyinSearchUnit = mBaseContactList.get(i).getmNamePinyinSearchUnit();
            if (true == T9Util.match(namePinyinSearchUnit, keyword)) {// search by name;
                BeanContact currentContacts = null;
                currentContacts = mBaseContactList.get(i);
//                currentContacts.setSearchByType(BeanContact.SearchByType.SearchByName);
                currentContacts.setmMatchT9Keywords(namePinyinSearchUnit.getMatchKeyword().toString());
                mSearchByNameContacts.add(currentContacts);
//                BeanContact firstContacts = null;
//                for (currentContacts = mBaseContactList.get(i), firstContacts = currentContacts; null != currentContacts; currentContacts = currentContacts.getmNextContacts()) {
//                    currentContacts.setSearchByType(BeanContact.SearchByType.SearchByName);
//                    currentContacts.setmMatchKeywords(namePinyinSearchUnit.getMatchKeyword().toString());
////                    currentContacts.setMatchStartIndex(firstContacts.getName().indexOf(firstContacts.getMatchKeywords().toString()));
////                    currentContacts.setMatchLength(firstContacts.getMatchKeywords().length());
//                    mSearchByNameContacts.add(currentContacts);
//                }

                continue;
            } else {
                BeanContact currentContacts = null;
                for (currentContacts = mBaseContactList.get(i); null != currentContacts; currentContacts = currentContacts.getmNextContacts()) {
                    if (currentContacts.getmPhoneNumber().contains(keyword)) {// search by phone number
//                        currentContacts.setSearchByType(BeanContact.SearchByType.SearchByPhoneNumber);
                        currentContacts.setmMatchT9Keywords(keyword);
//                        currentContacts.setMatchStartIndex(currentContacts.getPhoneNumber().indexOf(keyword));
//                        currentContacts.setMatchLength(keyword.length());
                        mSearchByPhoneNumberContacts.add(currentContacts);
                    }
                }
                continue;
            }
        }
        if (mSearchByNameContacts.size() > 0) {
            Collections.sort(mSearchByNameContacts, BeanContact.mAscComparator);
        }
        if (mSearchByPhoneNumberContacts.size() > 0) {
            Collections.sort(mSearchByPhoneNumberContacts, BeanContact.mAscComparator);
        }
        mSearchContacts.clear();
        mSearchContacts.addAll(mSearchByNameContacts);
        mSearchContacts.addAll(mSearchByPhoneNumberContacts);
        return mSearchContacts;
    }
}
