package com.hoody.clockapplication.util;

import android.util.Log;

import java.util.ArrayList;

public class VoiceKeyUtil {

    private static ArrayList<String> KeyClock = new ArrayList<>();
    private static ArrayList<String> KeyDate = new ArrayList<>();
    private static ArrayList<String> KeyLightON = new ArrayList<>();
    private static ArrayList<String> KeyLightOFF = new ArrayList<>();
    private static ArrayList<String> KeyWeek = new ArrayList<>();
    private static ArrayList<String> KeyName = new ArrayList<>();

    public static boolean matchClockKey(String content) {
        for (String s : KeyClock) {
            Log.d("onResults", "matchClockKey() called with: content = [" + content.contains(s) + "]");
            if (content.contains(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchDateKey(String content) {
        for (String s : KeyDate) {
            if (content.contains(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchLightOnKey(String content) {
        for (String s : KeyLightON) {
            if (content.contains(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchLightOffKey(String content) {
        for (String s : KeyLightOFF) {
            if (content.contains(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean matchWeekKey(String content) {
        for (String s : KeyWeek) {
            if (content.contains(s)) {
                return true;
            }
        }
        return false;
    }
    public static boolean matchNameKey(String content) {
        for (String s : KeyName) {
            if (content.contains(s)) {
                return true;
            }
        }
        return false;
    }
    static {
        KeyClock.add("几点");
        KeyClock.add("时间");

        KeyDate.add("几号");

        KeyWeek.add("星期几");
        KeyWeek.add("周几");

        KeyLightON.add("开电灯");
        KeyLightON.add("打开电灯");
        KeyLightON.add("开灯");
        KeyLightON.add("打开");

        KeyLightOFF.add("关灯");
        KeyLightOFF.add("关闭");

        KeyName.add("沙漏");
    }

}
