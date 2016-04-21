package com.byheetech.freecall.fragment;


import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.byheetech.freecall.R;
import com.byheetech.freecall.activity.MainActivity;
import com.byheetech.freecall.base.BaseFragment;
import com.byheetech.freecall.helper.CallLogHelper;
import com.byheetech.freecall.helper.ContactHelper;
import com.byheetech.freecall.model.BeanCallLog;
import com.byheetech.freecall.model.BeanContact;
import com.byheetech.freecall.utils.AbViewUtil;
import com.byheetech.freecall.view.T9TelephoneDialpadView;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.Callable;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * 拨号
 *
 * @author 西瓜
 */
public class CallFragment extends BaseFragment implements T9TelephoneDialpadView.OnT9TelephoneDialpadView, AbsListView.OnScrollListener {
    @Bind(R.id.callListView)
    ListView callListView;
    @Bind(R.id.indexContactListView)
    ListView indexContactListView;
    @Bind(R.id.callT9Search)
    T9TelephoneDialpadView callT9Search;
    private Subscription subscription;
    private Subscription subscriptionCallLog;
    private QuickAdapter adapterContactLv;
    private QuickAdapter adapterCalllog;
    public static final int READ_CALL_LOG_REQUEST_CODE = 1;
    private SimpleDateFormat dateFormat;

    public CallFragment() {
        // Required empty public constructor
    }

    @Override
    protected int initLayout() {
        return R.layout.fragment_call;
    }

    @Override
    protected void initId() {

    }

    @Override
    protected void initData() {
        ButterKnife.bind(this, getView());
        dateFormat = new SimpleDateFormat("MM-dd");
        adapterContactLv = new QuickAdapter<BeanContact>(getContext(), R.layout.adapter_contactlv) {

            @Override
            protected void convert(BaseAdapterHelper helper, BeanContact item) {
                helper.setVisible(R.id.contactHead, false);
                AbViewUtil.showTextHighlight((TextView) helper.getView(R.id.contactName), item.getmName(), item.getmMatchT9Keywords());
                AbViewUtil.showTextHighlight((TextView) helper.getView(R.id.contactPhone), item.getmPhoneNumber(), item.getmMatchT9Keywords());
                helper.setVisible(R.id.contactPhone, true);
            }
        };
        indexContactListView.setAdapter(adapterContactLv);
        adapterCalllog = new QuickAdapter<BeanCallLog>(getContext(), R.layout.adapter_calllog) {
            @Override
            protected void convert(BaseAdapterHelper helper, BeanCallLog item) {
                if (TextUtils.isEmpty(item.getName())) {
                    helper.setText(R.id.callLogName, item.getCount() > 1 ? item.getNumber() + " (" + item.getCount() + ")" : item.getNumber());
                } else {
                    helper.setText(R.id.callLogName, item.getCount() > 1 ? item.getName() + " (" + item.getCount() + ")" : item.getName());
                }
                switch (item.getType()) {//来电:1,拨出:2,未接:3
                    case 1:
                        helper.setImageResource(R.id.callLogType, R.mipmap.calllog_comein);
                        break;
                    case 2:
                        helper.setImageResource(R.id.callLogType, R.mipmap.calllog_comeout);
                        break;
                    case 3:
                        helper.setImageResource(R.id.callLogType, R.mipmap.calllog_missed);
                        break;
                }
                helper.setText(R.id.callLogTime, dateFormat.format(item.getDate()));
                helper.setText(R.id.callLogAddress, TextUtils.isEmpty(item.getPlace()) ? "未知" : item.getPlace());
            }
        };
        callListView.setAdapter(adapterCalllog);
        getCallLogList();
    }

    @Override
    protected void initListener() {
        callT9Search.setOnT9TelephoneDialpadView(this);
        callListView.setOnScrollListener(this);
        indexContactListView.setOnScrollListener(this);
    }


    @Override
    public void onAddDialCharacter(String addCharacter) {
        ((MainActivity) getActivity()).updataCallContent(addCharacter);
    }

    public void onInputTextChanged(final String curCharacter) {
        if (TextUtils.isEmpty(curCharacter)) {//显示通话记录
            callListView.setVisibility(View.VISIBLE);
            indexContactListView.setVisibility(View.GONE);
            ((MainActivity) getActivity()).isShowCallView(View.GONE);
        } else {//显示T9搜索联系人结果
            ((MainActivity) getActivity()).isShowCallView(View.VISIBLE);
            if (subscription != null && !subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
            subscription = Observable.fromCallable(new Callable<List<BeanContact>>() {

                @Override
                public List<BeanContact> call() throws Exception {
                    return ContactHelper.getInstance().t9InputSearch(curCharacter);
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<BeanContact>>() {

                @Override
                public void call(List<BeanContact> list) {
                    if (list != null) {
                        adapterContactLv.clear();
                        adapterContactLv.addAll(list);
                        callListView.setVisibility(View.GONE);
                        indexContactListView.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_CALL_LOG_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                getCallLogList();
            } else {
                // Permission Denied
                Toast.makeText(getContext(), "权限被拒绝", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getCallLogList() {
        if (subscriptionCallLog != null && !subscriptionCallLog.isUnsubscribed()) {
            subscriptionCallLog.unsubscribe();
        }
        subscriptionCallLog = Observable.fromCallable(new Callable<List<BeanCallLog>>() {
            @Override
            public List<BeanCallLog> call() throws Exception {
                CallLogHelper logHelper = new CallLogHelper(getContext());
                return logHelper.getCalllogList(CallFragment.this);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<BeanCallLog>>() {
            @Override
            public void call(List<BeanCallLog> list) {
                adapterCalllog.addAll(list);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        if (subscriptionCallLog != null && !subscriptionCallLog.isUnsubscribed()) {
            subscriptionCallLog.unsubscribe();
        }
    }

    /**
     * 拨号tab被点击
     */
    public void onTabClick() {
        if (isHidden()) {
            callT9Search.show();
        } else {
            if (callT9Search.getVisibility() == View.VISIBLE) {
                Animation translateAnimation = new TranslateAnimation(0f, 0f, 0, callT9Search.getHeight());//xy是差值
                translateAnimation.setDuration(200);
                callT9Search.startAnimation(translateAnimation);
                callT9Search.hide();
            } else {
                Animation translateAnimation = new TranslateAnimation(0f, 0f, callT9Search.getHeight(), 0);
                translateAnimation.setDuration(200);
                callT9Search.startAnimation(translateAnimation);
                callT9Search.show();
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_TOUCH_SCROLL || scrollState == SCROLL_STATE_FLING) {
            if (callT9Search.getVisibility() == View.VISIBLE) {
                Animation translateAnimation = new TranslateAnimation(0f, 0f, 0, callT9Search.getHeight());//xy是差值
                translateAnimation.setDuration(200);
                callT9Search.startAnimation(translateAnimation);
                callT9Search.hide();
                ((MainActivity) getActivity()).isShowCallView(View.GONE);
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
