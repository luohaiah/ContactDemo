package com.byheetech.freecall.base;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.byheetech.freecall.utils.SystemBarTintManager;

/**
 * 基类activity
 * Created by 西瓜 on 2016/3/17.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private SystemBarTintManager tintManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout();
        initStatusBar();
        applyStatusBarColor(setStatusBarColor());
        initId();
        initActionBar();
        initData();
        initListener();
    }

    /**
     * 初始化布局
     */
    abstract protected void initLayout();

    /**
     * 初始化标题栏
     */
    abstract protected void initActionBar();

    /**
     * 初始化状态栏
     */
    private void initStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
    }

    /**
     * 设置状态栏颜色
     *
     * @return 状态栏的颜色
     */
    abstract protected int setStatusBarColor();

    /**
     * 应用状态栏颜色
     *
     * @param color 状态栏的颜色
     */
    private void applyStatusBarColor(int color) {
        tintManager.setStatusBarTintResource(color);
    }


    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 初始化控件id
     */
    abstract protected void initId();

    /**
     * 初始化数据
     */
    abstract protected void initData();

    /**
     * 初始化监听器
     */
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
     * 显示扩展的view
     *
     * @param rootView 根节点view
     * @param view     需要显示的view
     */
    protected void showExtendView(RelativeLayout rootView, View view) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
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
