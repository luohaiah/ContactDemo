package com.byheetech.freecall.fragment;


import com.byheetech.freecall.R;
import com.byheetech.freecall.base.BaseFragment;
import com.byheetech.freecall.helper.ContactHelper;
import com.byheetech.freecall.model.BeanContact;
import com.byheetech.freecall.model.BeanContacts;
import com.byheetech.freecall.utils.RxBus;
import com.byheetech.freecall.view.ContactView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.functions.Action1;

/**
 * 联系人
 *
 * @author 西瓜
 */
public class ContactFragment extends BaseFragment {
    @Bind(R.id.contactView)
    ContactView contactView;
    Subscription rxSubscription;

    @Override
    protected int initLayout() {
        return R.layout.fragment_contact;
    }

    @Override
    protected void initId() {
    }

    @Override
    protected void initData() {
        ButterKnife.bind(this, getView());
        List<BeanContact> list = ContactHelper.getInstance().getBaseContactList();
        if (list != null) {
            contactView.updateListView(list);
        } else {

        }
//        rxSubscription是一个Subscription的全局变量，这段代码可以在onCreate / onStart等生命周期内
        rxSubscription = RxBus.getDefault().toObserverable(BeanContacts.class)
                .subscribe(new Action1<BeanContacts>() {
                               @Override
                               public void call(BeanContacts beanContacts) {
                                   contactView.updateListView(beanContacts.getList());
                               }
                           },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                // TODO: 处理异常
                            }
                        });
    }

    @Override
    protected void initListener() {

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (!rxSubscription.isUnsubscribed()) {
            rxSubscription.unsubscribe();
        }
    }
}
