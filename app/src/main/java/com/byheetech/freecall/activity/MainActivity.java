package com.byheetech.freecall.activity;

import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.byheetech.freecall.R;
import com.byheetech.freecall.base.BaseActivity;
import com.byheetech.freecall.base.FragmentTabHost;
import com.byheetech.freecall.fragment.CallFragment;
import com.byheetech.freecall.fragment.ContactFragment;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {
    @Bind(R.id.mainToolBar)
    Toolbar mainToolBar;//标题栏
    @Bind(R.id.main_call_close)
    ImageView mainCallClose;
    @Bind(R.id.main_call_do)
    ImageView mainCallDo;
    @Bind(R.id.main_call_delete)
    ImageView mainCallDelete;
    @Bind(R.id.mainCallLayout)
    LinearLayout mainCallLayout;
    @Bind(R.id.mainCallContent)
    TextView mainCallContent;
    private FragmentTabHost mTabHost; // 定义FragmentTabHost对象

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void initActionBar() {
        mainToolBar.setTitle("");
        mainToolBar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.darkblack));
        mainToolBar.setTitleTextColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
        setSupportActionBar(mainToolBar);
    }

    @Override
    protected int setStatusBarColor() {
        return R.color.darkblack;
    }

    @Override
    protected void initId() {
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.mainContent);
    }

    @Override
    protected void initData() {
        initTabHost();
    }

    private void initTabHost() {
        // 定义数组来存放Fragment界面
        Class<?> fragmentArray[] = {CallFragment.class,
                ContactFragment.class};
        // 得到fragment的个数
        int count = fragmentArray.length;
        for (int i = 0; i < count; i++) {
            // 为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(String.valueOf(i))
                    .setIndicator(getTabItemView(i));
            // 将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            mTabHost.getTabWidget().setShowDividers(
                    LinearLayout.SHOW_DIVIDER_NONE);
        }
    }

    /**
     * 给Tab按钮设置图标和文字
     */
    private View getTabItemView(int index) {
        // 定义数组来存放按钮文字
        String mTextViewArray[] = {getString(R.string.call),
                getString(R.string.contact)};
        // 定义数组来存放按钮图片
        int mImageViewArray[] = {R.drawable.selector_call,
                R.drawable.selector_contact};
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View view = layoutInflater.inflate(R.layout.main_tab_item, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        TextView textView = (TextView) view.findViewById(R.id.textview);
        imageView.setImageResource(mImageViewArray[index]);
        textView.setText(mTextViewArray[index]);
        return view;
    }

    @Override
    protected void initListener() {
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                switch (tabId) {
                    case "0"://拨号
                        mainToolBar.setTitle("");
                        mainCallContent.setVisibility(View.VISIBLE);
                        break;
                    case "1"://联系人
                        mainToolBar.setTitle(getString(R.string.contact));
                        mainCallContent.setVisibility(View.GONE);
                        break;
                }
            }
        });
        mTabHost.getTabWidget().getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = getSupportFragmentManager().getFragments().get(0);
                if (fragment != null && fragment instanceof CallFragment) {
                    CallFragment callFragment = (CallFragment) fragment;
                    callFragment.onTabClick();
                }
                if (mainCallContent.getText().length() > 0) {
                    mainCallLayout.setVisibility(View.VISIBLE);
                }
                mTabHost.setCurrentTab(0);
            }
        });
        mainCallDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCharacter();
            }
        });
        mainCallDelete.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteAllCharacter();
                return true;
            }
        });
        mainCallContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    mainCallLayout.setVisibility(View.VISIBLE);
                } else {
                    mainCallLayout.setVisibility(View.GONE);
                }
                Fragment fragment = getSupportFragmentManager().getFragments().get(0);
                if (fragment != null && fragment instanceof CallFragment && fragment.isVisible()) {
                    CallFragment callFragment = (CallFragment) fragment;
                    callFragment.onInputTextChanged(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mainCallClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = getSupportFragmentManager().getFragments().get(0);
                if (fragment != null && fragment instanceof CallFragment) {
                    CallFragment callFragment = (CallFragment) fragment;
                    callFragment.onTabClick();
                }
                mainCallLayout.setVisibility(View.GONE);
            }
        });
    }

    private void deleteCharacter() {
        String curInputStr = mainCallContent.getText().toString();
        if (curInputStr.length() > 0) {
            String newCurInputStr = curInputStr.substring(0, curInputStr.length() - 1);
            mainCallContent.setText(newCurInputStr);
        }
    }

    private void deleteAllCharacter() {
        String curInputStr = mainCallContent.getText().toString();
        if (curInputStr.length() > 0) {
            mainCallContent.setText("");
        }
    }

    /**
     * 是否显示拨打view
     *
     * @param visiable
     */
    public void isShowCallView(int visiable) {
        if (visiable == View.VISIBLE) {
            mainCallLayout.setVisibility(View.VISIBLE);
        } else {
            mainCallLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 更新标题栏中拨号内容
     *
     * @param addCharacter
     */
    public void updataCallContent(String addCharacter) {
        String temp = mainCallContent.getText().toString();
        if (!TextUtils.isEmpty(addCharacter)) {
            mainCallContent.setText(temp + addCharacter);
        }
    }
}
