package com.XYW.easynote.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

public class WindowManager {

    private static Toast toast;

    /**
     * 获取状态栏高度
     * @param context 活动容器
     * @param activity 活动
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context, Activity activity) {
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && activity.isInMultiWindowMode()) {
            return result;
        }
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 设置页面全屏
     * @param activity 设置对象
     */
    public static void setFullScreen(Activity activity) {
        activity.getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN, android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 获取当前屏幕方向状态
     * @param context 活动容器
     * @return true为横屏, false为竖屏
     */
    public static boolean isScreenChange(Context context) {
        Configuration mConfiguration = context.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation ; //获取屏幕方向

        if(ori == Configuration.ORIENTATION_LANDSCAPE){
            //横屏
            return true;
        } else if (ori == Configuration.ORIENTATION_PORTRAIT){
            //竖屏
            return false;
        }
        return false;
    }

    public static void showToast(Context context, String content) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context,
                content,
                Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showToast(Context context, String content, int duration) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context,
                content,
                duration);
        toast.show();
    }

    public static void showToastWithGravity(Context context, String content, int gravity, int xOffset, int yOffset) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context,
                content,
                Toast.LENGTH_SHORT);
        toast.setGravity(gravity, xOffset, yOffset);
        toast.show();
    }

    public static void showToastWithGravity(Context context, String content, int gravity, int xOffset, int yOffset, int duration) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context,
                content,
                duration);
        toast.setGravity(gravity, xOffset, yOffset);
        toast.show();
    }

    public static void setWhiteStatusBar(Window window, boolean flag) {
        if (flag)
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        else {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    public static void setBlackStatusBar(Window window, boolean flag) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (flag)
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            else
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        } else {
            if (flag)
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            else
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    /**
     * 获取深色模式开启状态
     * @param activity 目标活动
     * @return true为深思模式, false为浅色模式
     */
    public static int getUIMode(Activity activity) {
        return activity.getApplicationContext().getResources().getConfiguration().uiMode;
    }

    public void KeyBoardListen(Context context, Activity activity) {
        //监听软键盘的状态
        View rootLayout = activity.getWindow().getDecorView().findViewById(android.R.id.content);
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            //获取当前窗口实际的可见区域
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            int height = r.height();
            ViewGroup.LayoutParams params = rootLayout.getLayoutParams();
            params.height = height + (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ? 0 : getStatusBarHeight(context, activity));
            rootLayout.setLayoutParams(params);
        });
    }
}
