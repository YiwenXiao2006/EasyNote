package com.XYW.easynote.util;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.XYW.easynote.R;
import com.XYW.easynote.ui.MessageBox;

import java.util.ArrayList;
import java.util.List;

public class PermissionManager {

    private static final String TAG = "PermissionManager";

    private final Context context;
    private final Activity activity;
    private String[] permissions;
    private final int REQUEST_CODE;
    private final List<String> mPermissionList = new ArrayList<>();

    public static final int PERMISSION_REQUEST = 0x001;

    public PermissionManager(Context context, Activity activity, String[] permissions) {
        this.context = context;
        this.activity = activity;
        this.permissions = permissions;
        this.REQUEST_CODE = PERMISSION_REQUEST;
    }

    public PermissionManager(Context context, Activity activity, String[] permissions, int REQUEST_CODE) {
        this.context = context;
        this.activity = activity;
        this.permissions = permissions;
        this.REQUEST_CODE = REQUEST_CODE;
    }


    public void checkPermissionWithRequest() {
        mPermissionList.clear();
        //判断哪些权限未授予
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permission);
            }
        }

        if (!mPermissionList.isEmpty()) {
            permissions = mPermissionList.toArray(new String[0]);//将List转为数组
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE);
        }
    }

    /**
     * 检查权限
     * @return true为全部已授权,false为未全部授权
     */
    public boolean checkPermission() {
        mPermissionList.clear();
        //判断哪些权限未授予
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permission);
            }
        }

        if (mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了
            return true;
        } else {
            permissions = mPermissionList.toArray(new String[0]);
            return false;
        }
    }

    public void onResult(int[] grantResults, String[] permissions, String message1, String message2, MessageBox.MessageBoxOnCkickListener negativelistener) {
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] == PERMISSION_GRANTED) {  //选择了“始终允许
            } else if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])) {  //用户选择了禁止不再询问
                Log.d(TAG, "Not Allowed: " + permissions[i]);
                new MessageBox.CreateMessageBox.Builder(context)
                        .setTitle(context.getString(R.string.title_messagebox_permission_denied))
                        .setMessage(message1)
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(false)
                        .setPositiveButton(context.getString(R.string.text_button_positive_allow_2), () -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                            intent.setData(uri);
                            activity.startActivityForResult(intent, 2);
                        })
                        .setNegativeButton(context.getString(R.string.text_button_negative_deny), negativelistener)
                        .create()
                        .show();
                break;
            } else {     //选择禁止
                Log.d(TAG, "Not Allowed: " + permissions[i]);
                new MessageBox.CreateMessageBox.Builder(context)
                        .setTitle(context.getString(R.string.title_messagebox_permission_denied))
                        .setMessage(message2)
                        .setCancelable(false)
                        .setCanceledOnTouchOutside(false)
                        .setPositiveButton(context.getString(R.string.text_button_positive_allow_1), () -> ActivityCompat.requestPermissions(activity, mPermissionList.toArray(new String[0]), 1))
                        .setNegativeButton(context.getString(R.string.text_button_negative_deny), negativelistener)
                        .create()
                        .show();
                break;
            }
        }
    }
}
