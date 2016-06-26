package com.wcsn.irislock.app;

import android.app.Application;
import android.content.Context;

import com.ImaginationUnlimited.library.app.CoreLibrary;
import com.ImaginationUnlimited.library.utils.log.Logger;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by suiyue on 2016/6/7 0007.
 */
public class IrisLockApplication extends Application{
    private static Context sContext = null;
    public static Context getContext() {
        return sContext;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (IrisLockApplication.this) {
            if (null == sContext) {
                sContext = getApplicationContext();
            }
        }
        // 配置CoreLibrary
        CoreLibrary.getInstance().init(IrisLockApplication.this);

        // 初始化APP
        App.getInstance().initApplication();

//        EventBus.getDefault().register(this);
        JPushInterface.init(this);
        JPushInterface.setDebugMode(false);
        JPushInterface.setAlias(getContext(), "2d7c9d7c1c281da13804000000009200108f", new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                String logs;
                switch (i) {
                    case 0:
                        logs = "Set tag and alias success";
                        Logger.e(logs);
                        break;
                    case 6002:
                        logs = "Failed to set alias and tags due to timeout. Try again after 60s.";

                        Logger.e(logs);
                    default:
                        logs = "Failed with errorCode = " + i;
                        Logger.e(logs);
                }
            }
        });
    }
}
