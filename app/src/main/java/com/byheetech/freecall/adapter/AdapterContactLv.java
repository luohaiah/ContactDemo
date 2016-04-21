package com.byheetech.freecall.adapter;

import android.content.Context;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.byheetech.freecall.R;
import com.byheetech.freecall.model.BeanContact;
import com.byheetech.freecall.utils.AbViewUtil;
import com.byheetech.freecall.view.QuickAlphabeticBar;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;

import java.util.List;

/**
 * 联系人适配器
 * Created by 西瓜 on 2016/3/31.
 */
public class AdapterContactLv extends QuickAdapter<BeanContact> implements SectionIndexer {

    public AdapterContactLv(Context context, int layoutResId) {
        super(context, layoutResId);
    }

    public AdapterContactLv(Context context, int layoutResId, List<BeanContact> data) {
        super(context, layoutResId, data);
    }

    @Override
    protected void convert(BaseAdapterHelper helper, BeanContact item) {
        switch (item.getSearchByType()) {
            case SearchByNull:
                helper.setText(R.id.contactName, item.getmName());
                helper.setVisible(R.id.contactPhone, false);
                boolean isShow;
                String curAlphabet = getAlphabet(item.getmSortKey());
                if (helper.getPosition() > 0) {
                    isShow = curAlphabet.equals(getAlphabet(getItem(helper.getPosition() - 1).getmSortKey())) ? false : true;
                } else {
                    isShow = true;
                }
                if (isShow) {
                    helper.setText(R.id.contactHead, curAlphabet);
                    helper.setVisible(R.id.contactHead, true);
                } else {
                    helper.setVisible(R.id.contactHead, false);
                }
                break;
            case SearchByName:
                helper.setVisible(R.id.contactHead, false);
                helper.setVisible(R.id.contactPhone, false);
                AbViewUtil.showTextHighlight((TextView) helper.getView(R.id.contactName), item.getmName(), item.getmMatchKeywords());
                break;
            case SearchByPhoneNumber:
                helper.setText(R.id.contactName, item.getmName());
                helper.setVisible(R.id.contactHead, false);
                AbViewUtil.showTextHighlight((TextView) helper.getView(R.id.contactPhone), item.getmPhoneNumber(), item.getmMatchKeywords());
                helper.setVisible(R.id.contactPhone, true);
                break;
        }
    }

    private String getAlphabet(String str) {
        if ((null == str) || (str.length() <= 0)) {
            return String.valueOf(QuickAlphabeticBar.DEFAULT_INDEX_CHARACTER);
        }
        String alphabet = null;
        char chr = str.charAt(0);
        if (chr >= 'A' && chr <= 'Z') {
            alphabet = String.valueOf(chr);
        } else if (chr >= 'a' && chr <= 'z') {
            alphabet = String.valueOf((char) ('A' + chr - 'a'));
        } else {
            alphabet = String.valueOf(QuickAlphabeticBar.DEFAULT_INDEX_CHARACTER);
        }
        return alphabet;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        if (QuickAlphabeticBar.DEFAULT_INDEX_CHARACTER == sectionIndex) {
            return 0;
        }
        for (int i = 0; i < getCount(); i++) {
            BeanContact contact = getItem(i);
            if (contact.getmSortKey().charAt(0) == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }
}
