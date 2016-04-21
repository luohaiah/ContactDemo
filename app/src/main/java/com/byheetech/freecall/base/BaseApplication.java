package com.byheetech.freecall.base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.byheetech.freecall.helper.ContactHelper;
import com.byheetech.freecall.utils.AssetsDatabaseManager;

import java.util.List;

/**
 * 自定义application
 * Created by 西瓜 on 2016/3/17.
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        String processName = getProcessName(this, android.os.Process.myPid());
        if (processName != null) {
            if (processName.equals(getPackageName())) {
                initAppForMainProcess();
            }
        }
    }

    /**
     * 在主进程中完成app初始化
     */
    private void initAppForMainProcess() {
        AssetsDatabaseManager.initManager(getApplicationContext());
        ContactHelper.getInstance().loadContact(getApplicationContext());
    }

    private String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }
}
