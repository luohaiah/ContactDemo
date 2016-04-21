package com.byheetech.freecall.view;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.byheetech.freecall.R;

/**
 * Created by Administrator on 2016/4/13.
 */
public class ContactSearchView extends LinearLayout {
    private OnContactSearchView onContactSearchView;

    interface OnContactSearchView {
        abstract void onSearch(String key);
    }

    public OnContactSearchView getOnContactSearchView() {
        return onContactSearchView;
    }

    public void setOnContactSearchView(OnContactSearchView onContactSearchView) {
        this.onContactSearchView = onContactSearchView;
    }

    public ContactSearchView(Context context) {
        super(context);
        initData(context);
    }

    public ContactSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context);
    }

    public ContactSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context);
    }

    private void initData(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_contact_search, this);
        EditText editText = (EditText) view.findViewById(R.id.contactSearchText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (onContactSearchView != null) {
                    onContactSearchView.onSearch(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
