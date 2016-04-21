package com.byheetech.freecall.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * 基类fragment
 * Created by 西瓜 on 2016/3/17.
 */
public abstract class BaseFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(initLayout(), null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        initId();
        initData();
        initListener();
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 设置布局
     *
     * @return 布局资源id
     */
    protected abstract int initLayout();

    abstract protected void initId();

    abstract protected void initData();

    abstract protected void initListener();

    /**
     * 显示扩展的view
     *
     * @param rootView 根节点view
     * @param view     需要显示的view
     * @param index    在根节点view中的位置索引
     */
    protected void showExtendView(LinearLayout rootView, View view, int index) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        rootView.addView(view, index, lp);
    }

    /**
     * 隐藏扩展的view
     *
     * @param rootView 根节点view
     * @param view     需要移除的view
     */
    protected void hideExtendView(LinearLayout rootView, View view) {
        rootView.removeView(view);
    }

    /**
     * 显示扩展的view
     *
     * @param rootView     根节点view
     * @param view         需要显示的view
     * @param topViewId    顶栏的id
     * @param bottomViewId 底栏的id
     */
    protected void showExtendView(RelativeLayout rootView, int topViewId, int bottomViewId, View view) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.BELOW, topViewId);
        lp.addRule(RelativeLayout.ABOVE, bottomViewId);
        rootView.addView(view, lp);
    }

    /**
     * 显示扩展的view
     *
     * @param rootView  根节点view
     * @param view      需要显示的view
     * @param topViewId 顶栏的id
     */
    protected void showExtendView(RelativeLayout rootView, int topViewId, View view) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.BELOW, topViewId);
        rootView.addView(view, lp);
    }

    /**
     * 隐藏扩展的view
     *
     * @param rootView 根节点view
     * @param view     需要移除的view
     */
    protected void hideExtendView(RelativeLayout rootView, View view) {
        rootView.removeView(view);
    }
}
