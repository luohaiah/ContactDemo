package com.byheetech.freecall.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.byheetech.freecall.R;
import com.byheetech.freecall.helper.ContactIndexHelper;
import com.byheetech.freecall.model.BeanContact;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/4/12.
 */
public class ContactIndexView extends LinearLayout {
    private ListView listView;
    private TextView textView;
    private QuickAdapter<BeanContact> quickAdapter;
    private OnContactIndexView onContactIndexView;

    interface OnContactIndexView {
        abstract void ContactIndexItemClick(BeanContact beanContact);
    }

    public OnContactIndexView getOnContactIndexView() {
        return onContactIndexView;
    }

    public void setOnContactIndexView(OnContactIndexView onContactIndexView) {
        this.onContactIndexView = onContactIndexView;
    }

    public ContactIndexView(Context context) {
        super(context);
        initData(context);
    }

    public ContactIndexView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context);
    }

    public ContactIndexView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context);
    }

    private void initData(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_contact_index, this);
        listView = (ListView) view.findViewById(R.id.contactIndexListView);
        textView = (TextView) view.findViewById(R.id.contactIndexName);
        quickAdapter = new QuickAdapter<BeanContact>(context, R.layout.adapter_contact_index) {
            @Override
            protected void convert(BaseAdapterHelper helper, BeanContact item) {
                helper.setText(R.id.contactIndexContent, String.valueOf(item.getmName().charAt(0)));
            }
        };
        listView.setAdapter(quickAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (onContactIndexView != null) {
                    onContactIndexView.ContactIndexItemClick(quickAdapter.getItem(position));
                }
            }
        });
    }

    public void updateUi(String letter) {
        textView.setText(letter);
        List<BeanContact> tempList = ContactIndexHelper.getInstance().getHashMap().get(letter);//获取对应字母的联系人集合
        List<BeanContact> resultList = new ArrayList<>();
        if (tempList.size() > 0 && tempList.get(0) != null) {
            resultList.add(tempList.get(0));
        }
        for (int i = 1; i < tempList.size(); i++) {//过滤这个联系人list，得到姓不重复的新联系人list
            if (!String.valueOf(tempList.get(i).getmName().charAt(0)).equalsIgnoreCase(String.valueOf(tempList.get(i - 1).getmName().charAt(0)))) {
                resultList.add(tempList.get(i));
            }
        }
        quickAdapter.clear();
        quickAdapter.addAll(resultList);
    }
}
