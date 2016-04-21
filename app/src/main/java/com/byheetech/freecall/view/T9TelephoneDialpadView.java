package com.byheetech.freecall.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.LinearLayout;

import com.byheetech.freecall.R;

public class T9TelephoneDialpadView extends LinearLayout implements
        OnClickListener, OnLongClickListener {
    private static final char DIAL_1_SECOND_MEANING = ' ';
    private static final char DIAL_X_SECOND_MEANING = ',';
    private static final char DIAL_0_SECOND_MEANING = '+';
    private static final char DIAL_J_SECOND_MEANING = ';';

    /**
     * Interface definition for a callback to be invoked when a
     * T9TelephoneDialpadView is operated.
     */
    public interface OnT9TelephoneDialpadView {
        void onAddDialCharacter(String addCharacter);
    }

    private Context mContext;
    /**
     * Inflate Custom T9 phone dialpad View hierarchy from the specified xml
     * resource.
     */
    private View mDialpadView; // this Custom View As the T9TelephoneDialpadView
    // of children
    private OnT9TelephoneDialpadView mOnT9TelephoneDialpadView = null;

    public T9TelephoneDialpadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
        initData();
        initListener();

    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void hide() {
        setVisibility(GONE);
    }

    private void initData() {

    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDialpadView = inflater.inflate(R.layout.t9_telephone_dialpad_layout,
                this);
    }

    private void initListener() {
        /**
         * set click listener for button("0-9",'*','#')
         */
        for (int i = 0; i < 12; i++) {
            View v = mDialpadView.findViewById(R.id.dialNum1 + i);
            v.setOnClickListener(this);
        }

        /**
         * set long click listener for button('1','*','0','#')
         * */
        View view1 = mDialpadView.findViewById(R.id.dialNum1);
        view1.setOnLongClickListener(this);

        View viewX = mDialpadView.findViewById(R.id.dialx);
        viewX.setOnLongClickListener(this);

        View viewO = mDialpadView.findViewById(R.id.dialNum0);
        viewO.setOnLongClickListener(this);

        View viewJ = mDialpadView.findViewById(R.id.dialj);
        viewJ.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialNum0:
            case R.id.dialNum1:
            case R.id.dialNum2:
            case R.id.dialNum3:
            case R.id.dialNum4:
            case R.id.dialNum5:
            case R.id.dialNum6:
            case R.id.dialNum7:
            case R.id.dialNum8:
            case R.id.dialNum9:
            case R.id.dialx:
            case R.id.dialj:
                addSingleDialCharacter(v.getTag().toString());
                break;

            default:
                break;
        }

    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.dialNum1:
                addSingleDialCharacter(String.valueOf(DIAL_1_SECOND_MEANING));
                break;
            case R.id.dialx:
                addSingleDialCharacter(String.valueOf(DIAL_X_SECOND_MEANING));
                break;
            case R.id.dialNum0:
                addSingleDialCharacter(String.valueOf(DIAL_0_SECOND_MEANING));
                break;
            case R.id.dialj:
                addSingleDialCharacter(String.valueOf(DIAL_J_SECOND_MEANING));
                break;
            default:
                break;
        }
        return true;
    }

    public OnT9TelephoneDialpadView getOnT9TelephoneDialpadView() {
        return mOnT9TelephoneDialpadView;
    }

    public void setOnT9TelephoneDialpadView(
            OnT9TelephoneDialpadView onT9TelephoneDialpadView) {
        mOnT9TelephoneDialpadView = onT9TelephoneDialpadView;
    }

    private void addSingleDialCharacter(String addCharacter) {
        if (null != mOnT9TelephoneDialpadView) {
            mOnT9TelephoneDialpadView.onAddDialCharacter(addCharacter);
        }
    }

    public void showT9TelephoneDialpadView() {
        if (this.getVisibility() != View.VISIBLE) {
            this.setVisibility(View.VISIBLE);
        }
    }

    public void hideT9TelephoneDialpadView() {
        if (this.getVisibility() != View.GONE) {
            this.setVisibility(View.GONE);
        }
    }

    public int getT9TelephoneDialpadViewVisibility() {
        return this.getVisibility();
    }

}
