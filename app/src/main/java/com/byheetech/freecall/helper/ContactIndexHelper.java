package com.byheetech.freecall.helper;

import com.byheetech.freecall.model.BeanContact;
import com.byheetech.freecall.view.QuickAlphabeticBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/4/12.
 */
public class ContactIndexHelper {
    private HashMap<String, List<BeanContact>> hashMap;//映射对应字母的联系人集合
    private static ContactIndexHelper helper;

    public HashMap<String, List<BeanContact>> getHashMap() {
        return hashMap;
    }

    public void setHashMap(HashMap<String, List<BeanContact>> hashMap) {
        this.hashMap = hashMap;
    }

    private ContactIndexHelper() {
        hashMap = new HashMap<>();
        for (int i = 0; i < QuickAlphabeticBar.getSelectCharacters().length; i++) {
            hashMap.put(String.valueOf(QuickAlphabeticBar.getSelectCharacters()[i]), new ArrayList<BeanContact>());
        }
    }

    public static ContactIndexHelper getInstance() {
        if (helper == null) {
            helper = new ContactIndexHelper();
        }
        return helper;
    }

    public void parseContact(List<BeanContact> list) {
        for (int i = 0; i < list.size(); i++) {
            hashMap.get(String.valueOf(list.get(i).getmSortKey().charAt(0))).add(list.get(i));
        }
    }
}
