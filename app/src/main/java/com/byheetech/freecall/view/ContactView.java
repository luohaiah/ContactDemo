package com.byheetech.freecall.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.byheetech.freecall.R;
import com.byheetech.freecall.adapter.AdapterContactLv;
import com.byheetech.freecall.helper.ContactHelper;
import com.byheetech.freecall.model.BeanContact;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/3/31.
 */
public class ContactView extends RelativeLayout implements QuickAlphabeticBar.OnQuickAlphabeticBar, ContactIndexView.OnContactIndexView, ContactSearchView.OnContactSearchView {
    private ListView contactLv;
    private TextView contactTv;
    private AdapterContactLv adapterContactLv;
    private QuickAlphabeticBar alphabeticBar;
    private ContactIndexView contactIndexView;
    private final String tag = ContactView.class.getSimpleName();
    private Subscription subscription;
    private Subscription subscriptionSearch;
    private ContactSearchView contactSearchView;


    public ContactView(Context context) {
        super(context);
        initData(context);
    }

    public ContactView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context);
    }

    public ContactView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context);
    }


    private void initData(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_contact, this);
        contactLv = (ListView) view.findViewById(R.id.contactLv);
        contactTv = (TextView) view.findViewById(R.id.contactTv);
        alphabeticBar = (QuickAlphabeticBar) view.findViewById(R.id.contactQuickBar);
        contactIndexView = (ContactIndexView) view.findViewById(R.id.contactIndex);
        contactSearchView = (ContactSearchView) view.findViewById(R.id.contactSearchView);
        adapterContactLv = new AdapterContactLv(context, R.layout.adapter_contactlv);
        contactLv.setAdapter(adapterContactLv);
        alphabeticBar.setQuickAlphabeticLv(contactLv);
        alphabeticBar.setSelectCharTv(contactTv);
        alphabeticBar.setSectionIndexer(adapterContactLv);
        alphabeticBar.setOnQuickAlphabeticBar(this);
        contactIndexView.setOnContactIndexView(this);
        contactSearchView.setOnContactSearchView(this);
        initListener();
    }

    private void initListener() {
        contactLv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (adapterContactLv.getCount() > 0) {
                    int currentIndex = (firstVisibleItem + visibleItemCount) < totalItemCount ? firstVisibleItem : totalItemCount - 1;
                    BeanContact beanContact = adapterContactLv.getItem(currentIndex);
                    alphabeticBar.setCurrentSelectChar(beanContact.getmSortKey().charAt(0));
                    contactIndexView.updateUi(String.valueOf(beanContact.getmSortKey().charAt(0)));
                    alphabeticBar.invalidate();
                }
            }
        });
        contactLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }

    public void updateListView(List<BeanContact> list) {
        adapterContactLv.clear();
        adapterContactLv.addAll(list);
    }


    @Override
    public void onTouchDown() {
        contactIndexView.setVisibility(View.VISIBLE);
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void onTouchUp() {
        subscription = Observable.timer(4, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Long>() {
            @Override
            public void call(Long aLong) {
                contactIndexView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        if (subscriptionSearch != null && !subscriptionSearch.isUnsubscribed()) {
            subscriptionSearch.unsubscribe();
        }
        super.onDetachedFromWindow();
    }

    @Override
    public void ContactIndexItemClick(BeanContact beanContact) {
        for (int i = 0; i < adapterContactLv.getCount(); i++) {
            if (String.valueOf(adapterContactLv.getItem(i).getmName().charAt(0)).equalsIgnoreCase(String.valueOf(beanContact.getmName().charAt(0)))) {
                contactLv.setSelection(i);
                break;
            }
        }
    }

    @Override
    public void onSearch(final String key) {
        if (subscriptionSearch != null && !subscriptionSearch.isUnsubscribed()) {
            subscriptionSearch.unsubscribe();
        }
        subscriptionSearch = Observable.fromCallable(new Callable<List<BeanContact>>() {

            @Override
            public List<BeanContact> call() throws Exception {
                return ContactHelper.getInstance().qwertySearch(key);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<BeanContact>>() {
            @Override
            public void call(List<BeanContact> list) {
                if (TextUtils.isEmpty(key)) {
                    alphabeticBar.setVisibility(View.VISIBLE);
                } else {
                    alphabeticBar.setVisibility(View.GONE);
                    contactIndexView.setVisibility(GONE);
                    contactTv.setVisibility(GONE);
                }
                adapterContactLv.clear();
                adapterContactLv.addAll(list);
            }
        });
    }
}
