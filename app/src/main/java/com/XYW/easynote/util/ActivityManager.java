package com.XYW.easynote.util;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class ActivityManager {

    private static final List<Activity> activities = new ArrayList<>();

    public static void setActivity(Activity activity) {
        addActivity(activity);
        setStatusBar(activity);
    }

    public static void setActivity(Activity activity, View view) {
        addActivity(activity);
        setStatusBar(activity, view);
    }

    /**
     * 将活动添加入栈，记录处于非销毁状态的活动
     * @param activity 目标活动
     */
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }

    /**
     * 将已销毁的活动出栈
     * @param activity 目标活动
     */
    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }

    /**
     * 结束所有活动，退出程序
     */
    public static void finishAllActivity() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 设置状态栏为透明
     * @param activity 需要设置状态栏的活动
     */
    public static void setStatusBar(Activity activity) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }

        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //深色模式的值为:0x21
        //浅色模式的值为:0x11
        if (com.XYW.easynote.util.WindowManager.getUIMode(activity) == 0x21) {
            //恢复状态栏白色字体
            com.XYW.easynote.util.WindowManager.setWhiteStatusBar(window);
        } else if (com.XYW.easynote.util.WindowManager.getUIMode(activity) == 0x11) {
            //设置状态栏黑色字体
            com.XYW.easynote.util.WindowManager.setBlackStatusBar(window);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * 设置状态栏为透明
     * @param activity 需要设置状态栏的活动
     * @param view 填补状态栏位置的View
     */
    public static void setStatusBar(Activity activity, View view) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            view.setVisibility(View.GONE);
            return;
        }

        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //深色模式的值为:0x21
        //浅色模式的值为:0x11
        if (activity.getApplicationContext().getResources().getConfiguration().uiMode == 0x21) {
            //恢复状态栏白色字体
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        } else if (activity.getApplicationContext().getResources().getConfiguration().uiMode == 0x11) {
            //设置状态栏黑色字体
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = com.XYW.easynote.util.WindowManager.getStatusBarHeight(activity.getBaseContext(), activity);
        view.setLayoutParams(params);
    }
}
