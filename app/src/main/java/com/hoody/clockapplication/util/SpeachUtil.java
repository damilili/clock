package com.hoody.clockapplication.util;

import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.hoody.clockapplication.app.Application;

import java.util.Calendar;
import java.util.Locale;

/**
 * 语音播报
 */
public class SpeachUtil {
    private static final String TAG = "SpeachUtil";
    private static TextToSpeech TextToSpeech;

    static  {
        //设置朗读语言
        TextToSpeech = new TextToSpeech(Application.Instance, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    //设置朗读语言
                    int supported = TextToSpeech.setLanguage(Locale.US);
//                    if ((supported != TextToSpeech.LANG_AVAILABLE) && (supported != TextToSpeech.LANG_COUNTRY_AVAILABLE)) {
//                    }
                }

            }
        });
    }

    public static void speakDate(String month, String date) {
        String result = "今天是";
        switch (month) {
            case "01":
                result += "一月";
                break;
            case "02":
                result += "二月";
                break;
            case "03":
                result += "三月";
                break;
            case "04":
                result += "四月";
                break;
            case "05":
                result += "五月";
                break;
            case "06":
                result += "六月";
                break;
            case "07":
                result += "七月";
                break;
            case "08":
                result += "八月";
                break;
            case "09":
                result += "九月";
                break;
            case "10":
                result += "十月";
                break;
            case "11":
                result += "十一月";
                break;
            case "12":
                result += "十二月";
                break;
        }
        String chinaNum = getChinaNum(date);
        if (chinaNum.startsWith("零")) {
            result += chinaNum.substring(1);
        } else {
            result += chinaNum;
        }
        result += "号";
        Log.d(TAG, "speakDate() called with: dateText = [" + date + "]");
        speak(result);
    }

    public static void speak(String content) {
        TextToSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null);
    }

    public static void speakTime(String hour, String minute) {
        StringBuilder result = new StringBuilder();
        switch (hour) {
            case "00":
                result.append("十二点");
                break;
            case "01":
                result.append("一点");
                break;
            case "02":
                result.append("两点");
                break;
            case "03":
                result.append("三点");
                break;
            case "04":
                result.append("四点");
                break;
            case "05":
                result.append("五点");
                break;
            case "06":
                result.append("六点");
                break;
            case "07":
                result.append("七点");
                break;
            case "08":
                result.append("八点");
                break;
            case "09":
                result.append("九点");
                break;
            case "10":
                result.append("十点");
                break;
            case "11":
                result.append("十一点");
                break;
            case "12":
                result.append("十二点");
                break;
            case "13":
                result.append("下午一点");
                break;
            case "14":
                result.append("下午两点");
                break;
            case "15":
                result.append("下午三点");
                break;
            case "16":
                result.append("下午四点");
                break;
            case "17":
                result.append("下午五点");
                break;
            case "18":
                result.append("下午六点");
                break;
            case "19":
                result.append("下午七点");
                break;
            case "20":
                result.append("下午八点");
                break;
            case "21":
                result.append("下午九点");
                break;
            case "22":
                result.append("下午十点");
                break;
            case "23":
                result.append("下午十一点");
                break;
        }
        if (minute.equals("00")) {
            result.append("整");
        } else if (minute.equals("30")) {
            result.append("半");
        } else {
            result.append(getChinaNum(minute));
            result.append("分");
        }
        Log.d("onResults", "speakTime() called with: hour = [" + result.toString()+ "]");
        speak(result.toString());
    }

    private static String getChinaNum(String num) {
        StringBuilder result = new StringBuilder();
        String substring = num.substring(0, 1);
        switch (substring) {
            case "0":
                result.append("零");
                break;
            case "1":
                result.append("十");
                break;
            case "2":
                result.append("二十");
                break;
            case "3":
                result.append("三十");
                break;
            case "4":
                result.append("四十");
                break;
            case "5":
                result.append("五十");
                break;
            case "6":
                result.append("六十");
                break;
            case "7":
                result.append("七十");
                break;
            case "8":
                result.append("八十");
                break;
            case "9":
                result.append("九十");
                break;
        }
        switch (num.substring(1, 2)) {
            case "1":
                result.append("一");
                break;
            case "2":
                result.append("二");
                break;
            case "3":
                result.append("三");
                break;
            case "4":
                result.append("四");
                break;
            case "5":
                result.append("五");
                break;
            case "6":
                result.append("六");
                break;
            case "7":
                result.append("七");
                break;
            case "8":
                result.append("八");
                break;
            case "9":
                result.append("九");
                break;

        }
        return result.toString();
    }

    public static void speakWeek() {
        String result = "今天是";
        int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        switch (week) {
            case 1:
                result += ("周天");
                break;
            case 2:
                result += ("周一");
                break;
            case 3:
                result += ("周二");
                break;
            case 4:
                result += ("周三");
                break;
            case 5:
                result += ("周四");
                break;
            case 6:
                result += ("周五");
                break;
            case 7:
                result += ("周六");
                break;
        }
        speak(result);
    }
}
