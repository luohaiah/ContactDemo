package com.byheetech.freecall.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.byheetech.freecall.R;

public class QuickAlphabeticBar extends View {
    private static final String TAG = "QuickAlphabeticBar";
    public static final char DEFAULT_INDEX_CHARACTER = '#';
    private static char[] mSelectCharacters = {DEFAULT_INDEX_CHARACTER, 'A',
            'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    private SectionIndexer mSectionIndexer;
    private ListView mQuickAlphabeticLv;
    private TextView mSelectCharTv;

    private char mCurrentSelectChar;
    private OnQuickAlphabeticBar onQuickAlphabeticBar;

    interface OnQuickAlphabeticBar {
        abstract void onTouchDown();//手指按下

        abstract void onTouchUp();//手指抬起
    }

    public OnQuickAlphabeticBar getOnQuickAlphabeticBar() {
        return onQuickAlphabeticBar;
    }

    public void setOnQuickAlphabeticBar(OnQuickAlphabeticBar onQuickAlphabeticBar) {
        this.onQuickAlphabeticBar = onQuickAlphabeticBar;
    }

    public QuickAlphabeticBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static char[] getSelectCharacters() {
        return mSelectCharacters;
    }

	/*public static void setSelectCharacters(char[] mSelectCharacters) {
        QuickAlphabeticBar.mSelectCharacters = mSelectCharacters;
	}*/

    public SectionIndexer getSectionIndexer() {
        return mSectionIndexer;
    }

    public void setSectionIndexer(SectionIndexer sectionIndexer) {
        mSectionIndexer = sectionIndexer;
    }

    public ListView getQuickAlphabeticLv() {
        return mQuickAlphabeticLv;
    }

    public void setQuickAlphabeticLv(ListView quickAlphabeticLv) {
        mQuickAlphabeticLv = quickAlphabeticLv;
    }

    public TextView getSelectCharTv() {
        return mSelectCharTv;
    }

    public void setSelectCharTv(TextView selectCharTv) {
        mSelectCharTv = selectCharTv;
    }

    public char getCurrentSelectChar() {
        return mCurrentSelectChar;
    }

    public void setCurrentSelectChar(char currentSelectChar) {
        mCurrentSelectChar = currentSelectChar;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int index = getCurrentIndex(event);
        //Log.i(TAG,"index="+index);
        if ((event.getAction() == MotionEvent.ACTION_DOWN) || (event.getAction() == MotionEvent.ACTION_MOVE)) {
            setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray));
            //reference: http://blog.csdn.net/jack_l1/article/details/14165291
            if (null != mSectionIndexer) {
                int position = mSectionIndexer.getPositionForSection(mSelectCharacters[index]);
                if (position < 0) {
                    return true;
                }
                if (null != mSelectCharTv) {    //show select char
                    mSelectCharTv.setVisibility(View.VISIBLE);
                    mSelectCharTv.setText(String.valueOf(mSelectCharacters[index]));
                }
                if (null != mQuickAlphabeticLv) {
                    mQuickAlphabeticLv.setSelection(position);
                }
                if (onQuickAlphabeticBar != null) {
                    onQuickAlphabeticBar.onTouchDown();
                }
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
            if (null != mSelectCharTv) {    //hide select char
                mSelectCharTv.setVisibility(View.GONE);
            }
            if (onQuickAlphabeticBar != null) {
                onQuickAlphabeticBar.onTouchUp();
            }
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float xPos = getMeasuredWidth() / 2;
        float yPos = 0;
        if (mSelectCharacters.length > 0) {
            float sigleHeight = getMeasuredHeight() / mSelectCharacters.length;
            for (int i = 0; i < mSelectCharacters.length; i++) {
                yPos = (i + 1) * sigleHeight;
                if (mSelectCharacters[i] == mCurrentSelectChar) {
                    RectF rectF = new RectF(xPos - sigleHeight / 3, yPos - sigleHeight / 3, xPos + sigleHeight / 3, yPos + sigleHeight / 3);
                    canvas.drawRect(rectF, mBluePaint);
                    Paint.FontMetricsInt fontMetrics = mCurrentIndexPaint.getFontMetricsInt();
                    // 转载请注明出处：http://blog.csdn.net/hursing
                    int baseline = ((int) rectF.bottom + (int) rectF.top - fontMetrics.bottom - fontMetrics.top) / 2;//让文字竖直居中可以看下textview源码
                    canvas.drawText(String.valueOf(mSelectCharacters[i]), rectF.centerX(), baseline, mCurrentIndexPaint);
                } else {
                    RectF rectF = new RectF(xPos - sigleHeight / 3, yPos - sigleHeight / 3, xPos + sigleHeight / 3, yPos + sigleHeight / 3);
                    Paint.FontMetricsInt fontMetrics = mCurrentIndexPaint.getFontMetricsInt();
                    int baseline = ((int) rectF.bottom + (int) rectF.top - fontMetrics.bottom - fontMetrics.top) / 2;
                    canvas.drawText(String.valueOf(mSelectCharacters[i]), rectF.centerX(), baseline, mOtherIndexPaint);
                }
            }
        }
        super.onDraw(canvas);
    }

    private int getCurrentIndex(MotionEvent event) {
        if (null == event) {
            return 0;
        }

        int y = (int) event.getY();
        int index = y / (getMeasuredHeight() / mSelectCharacters.length);
        if (index < 0) {
            index = 0;
        } else if (index >= mSelectCharacters.length) {
            index = mSelectCharacters.length - 1;
        }

        return index;
    }

    private Paint mCurrentIndexPaint = new Paint();

    {
        mCurrentIndexPaint.setColor(Color.WHITE);
        mCurrentIndexPaint.setTextSize(24);
        mCurrentIndexPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mCurrentIndexPaint.setTextAlign(Paint.Align.CENTER);
    }

    private Paint mBluePaint = new Paint();

    {
        mBluePaint.setColor(ContextCompat.getColor(getContext(), R.color.blue2));
    }

    private Paint mOtherIndexPaint = new Paint();

    {
        mOtherIndexPaint.setColor(Color.BLACK);
        mOtherIndexPaint.setTextSize(24);
        mOtherIndexPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mOtherIndexPaint.setTextAlign(Paint.Align.CENTER);
    }
}
