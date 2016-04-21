package com.byheetech.freecall;

import android.content.Intent;
import android.os.CountDownTimer;

import com.byheetech.freecall.activity.MainActivity;
import com.byheetech.freecall.base.BaseActivity;

public class WelcomeActivity extends BaseActivity {
    private MyCountDownTimer timer;

    @Override
    protected void initLayout() {
        setContentView(R.layout.activity_welcome);
    }

    @Override
    protected void initActionBar() {

    }

    @Override
    protected int setStatusBarColor() {
        return android.R.color.transparent;
    }

    @Override
    protected void initId() {

    }

    @Override
    protected void initData() {
        timer = new MyCountDownTimer(2000, 1000);
        timer.start();
    }

    @Override
    protected void initListener() {

    }

    class MyCountDownTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }
}
