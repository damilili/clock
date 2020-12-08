package com.hoody.clockapplication.util;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;

/**
 * 闪光灯
 */
public class FlashlightUtil {
    private static final String TAG = FlashlightUtil.class.getSimpleName();


    private static Camera camera;
    private static Parameters parameters;
    public static boolean hasClosed = true;
    //开灯时间
    public static long LastOpenLightTime;
    /**
     * @param on true 开灯 false 关灯
     */
    public static void toggleLight(boolean on) {
        if (on) {
            if (!hasClosed) {
                return;
            }
            camera = Camera.open();
            parameters = camera.getParameters();
            parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);// 开启
            camera.setParameters(parameters);
            hasClosed = false;
            LastOpenLightTime = System.currentTimeMillis();
        } else {
            if (hasClosed) {
                return;
            }
            parameters.setFlashMode(Parameters.FLASH_MODE_OFF);// 关闭
            camera.setParameters(parameters);
            hasClosed = true;
            camera.release();
        }
    }
}

