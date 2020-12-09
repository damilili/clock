package com.hoody.clockapplication.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

/**
 * @author bluewindtalker
 * @description 光线传感器工具
 * @date 2018/4/15-下午12:08
 */
public final class LightSensorUtil {
    private static final String TAG = "LightSensorUtil";
    public static float lightLevel = 0;
    private static SensorEventListener lightSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                //光线强度
                lightLevel = event.values[0];
                Log.e(TAG, "光线传感器得到的光线强度-->" + lightLevel);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    private LightSensorUtil() {
    }

    private static SensorManager getSenosrManager(Context context) {
        return (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

    }

    /**
     * 注册光线传感器监听器
     */
    public static void registerLightSensor(Context context) {
        SensorManager sensorManager = getSenosrManager(context);
        if (sensorManager == null) {
            return;
        }
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT); // 获取光线传感器
        if (lightSensor != null) { // 光线传感器存在时
            sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL); // 注册事件监听
        }
    }

    /**
     * 反注册光线传感器监听器
     */
    public static void unregisterLightSensor(Context context) {
        SensorManager sensorManager = getSenosrManager(context);
        if (sensorManager == null) {
            return;
        }
        sensorManager.unregisterListener(lightSensorListener);
        lightLevel = -1;
    }
}

