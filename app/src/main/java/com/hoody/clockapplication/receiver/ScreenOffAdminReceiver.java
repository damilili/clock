package com.hoody.clockapplication.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ScreenOffAdminReceiver extends DeviceAdminReceiver {
    private static final String TAG = "ScreenOffAdminReceiver";

    @Override
    public void onEnabled(Context context, Intent intent) {
        Log.d(TAG, "设备管理器使用");
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        Log.d(TAG, "设备管理器没有使用");
        Toast.makeText(context, "请在设备管理器中激活沙漏时钟", Toast.LENGTH_SHORT).show();
    }
}