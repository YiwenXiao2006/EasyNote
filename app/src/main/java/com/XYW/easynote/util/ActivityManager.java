package com.XYW.easynote.util;

import static android.content.Context.UI_MODE_SERVICE;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;
import java.util.List;

public class ActivityManager {

    private static final String TAG = "ActivityManager";

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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //深色模式的值为:0x21
        //浅色模式的值为:0x11 com.XYW.easynote.util.WindowManager.getUIMode(activity) == 0x11
        if ((activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            //恢复状态栏白色字体
            com.XYW.easynote.util.WindowManager.setWhiteStatusBar(window, true);
        } else if ((activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO) {
            //设置状态栏黑色字体
            com.XYW.easynote.util.WindowManager.setBlackStatusBar(window, true);
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    /**
     * 设置状态栏为透明
     * @param activity 需要设置状态栏的活动
     * @param view 填补状态栏位置的View
     */
    public static void setStatusBar(Activity activity, View view) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {  // LOLLIPOP:21
            view.setVisibility(View.GONE);
            return;
        }

        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //深色模式的值为:0x21
        //浅色模式的值为:0x11
        if ((activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            //恢复状态栏白色字体
            com.XYW.easynote.util.WindowManager.setWhiteStatusBar(window, true);
        } else if ((activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_NO) {
            //设置状态栏黑色字体
            com.XYW.easynote.util.WindowManager.setBlackStatusBar(window, true);
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);

        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = com.XYW.easynote.util.WindowManager.getStatusBarHeight(activity.getBaseContext(), activity);
        view.setLayoutParams(params);
    }

    public static void setDarkMode(Activity activity) {
        UiModeManager uiModeManager = (UiModeManager) activity.getSystemService(UI_MODE_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 || uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES) {
            return;
        }
        SharedPreferences preferences = activity.getSharedPreferences("Settings", Context.MODE_MULTI_PROCESS);
        boolean darkMode = preferences.getBoolean("darkMode", false);
        if (darkMode && (activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) != Configuration.UI_MODE_NIGHT_YES) {
            ((AppCompatActivity) activity).getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            activity.recreate();
        } else if (!darkMode && (activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            ((AppCompatActivity) activity).getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            activity.recreate();
        }
    }

    /*
     * 获取本地软件版本号
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
            if (TextUtils.isEmpty(versionName)) {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static int getAppVersionCode(Context context) {
        int versionCode = 0;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取应用程序名称
     */
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
