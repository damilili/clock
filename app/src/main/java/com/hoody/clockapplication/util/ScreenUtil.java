package com.hoody.clockapplication.util;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.PowerManager;
import android.widget.Toast;

import com.hoody.clockapplication.receiver.ScreenOffAdminReceiver;

public class ScreenUtil {

    public static void screenOn(Context context) {
        if (checkScreenOn(context)) {
            return;
        }
        PowerManager mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, ScreenUtil.class.getName());
        mWakeLock.acquire();
        mWakeLock.release();

    }

    public static void screenOff(Context context) {
        DevicePolicyManager policyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminReceiver = new ComponentName(context, ScreenOffAdminReceiver.class);
        boolean admin = policyManager.isAdminActive(adminReceiver);
        if (admin) {
            policyManager.lockNow();
        } else {
            Toast.makeText(context, "请在设备管理器中激活沙漏时钟", Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean checkScreenOn(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        return pm.isScreenOn();
    }

}
