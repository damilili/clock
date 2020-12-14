package com.hoody.clockapplication;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.hoody.clockapplication.util.FlashlightUtil;
import com.hoody.clockapplication.util.LightSensorUtil;
import com.hoody.clockapplication.util.ScreenUtil;
import com.hoody.clockapplication.util.SpeachUtil;
import com.hoody.clockapplication.util.VoiceKeyUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private TextView mTextClock;
    private TextView mTextDate;
    private Runnable mScreenOffAction = new Runnable() {
        @Override
        public void run() {
            ScreenUtil.screenOff(MainActivity.this);
        }
    };
    private Runnable mRefreshAction = new Runnable() {
        @Override
        public void run() {
            refreshTime();
        }
    };
    private final int DelayScreenOffTimeInMillis = 20 * 1000;

    public static SpeechRecognizer mSpeechRecognizer;
    private static long LastCallOnRmsChanged;
    private boolean recognitionAvailable;
    private View mBaseView;
    private String mMonth;
    private String mDate;
    private String mHour = "0";
    private String mMinute;
    private String mSecond;
    private Runnable mRecordAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |               //这个在锁屏状态下
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mBaseView = View.inflate(this, R.layout.activity_main, null);
        setContentView(mBaseView);
        mTextDate = findViewById(R.id.text_date);
        mTextClock = findViewById(R.id.text_clock);
        refreshTime();
        mBaseView.postDelayed(mRefreshAction, 1000);
        mTextClock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (FlashlightUtil.hasClosed) {
                    FlashlightUtil.toggleLight(true);
                } else {
                    FlashlightUtil.toggleLight(false);
                }
            }
        });

        if (mSpeechRecognizer == null) {
            mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            mSpeechRecognizer.setRecognitionListener(new MyRecognitionListener());
            mRecordAction = new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run(ee) called");
                    if (System.currentTimeMillis() - LastCallOnRmsChanged <300) {
                        return;
                    }
                    Intent recognitionIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    recognitionIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
                    recognitionIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh-CN");
                    mSpeechRecognizer.startListening(recognitionIntent);
                }
            };
        }
        doSpeechRecognition();
    }
    private void refreshTime() {
        String format = simpleDateFormat.format(new Date());
        String[] s = format.split(" ");
        mTextDate.setText(s[0]);
        String[] split = s[0].split("-");
        mMonth = split[1];
        mDate = split[2];
        mTextClock.setText(s[1]);
        String[] time = s[1].split(":");
        mHour = time[0];
        mMinute = time[1];
        mSecond = time[2];
        mBaseView.postDelayed(mRefreshAction, 1000);
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - LastCallOnRmsChanged > 1000) {
            doSpeechRecognition();
        }
        if (!FlashlightUtil.hasClosed) {
            //关灯
            if ((currentTimeMillis - FlashlightUtil.LastOpenLightTime) > 30000) {
                FlashlightUtil.toggleLight(false);
            }
        }
    }
    public void doSpeechRecognition() {
        recognitionAvailable = SpeechRecognizer.isRecognitionAvailable(this);
        Log.d(TAG, "doSpeechRecognition() called recognitionAvailable = " + recognitionAvailable);
        if (recognitionAvailable) {
            mBaseView.removeCallbacks(mRecordAction);
            mBaseView.postDelayed(mRecordAction, 300);
        } else {
            Toast.makeText(MainActivity.this, "不支持语音命令", Toast.LENGTH_SHORT).show();
        }
    }

    private void postScreenOff() {
        mBaseView.removeCallbacks(mScreenOffAction);
        mBaseView.postDelayed(mScreenOffAction, DelayScreenOffTimeInMillis);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
        postScreenOff();
        LightSensorUtil.registerLightSensor(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LightSensorUtil.unregisterLightSensor(this);
    }

    private class MyRecognitionListener implements RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.d(TAG, "onReadyForSpeech() called with: params = [" + params + "]");
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.i(TAG, "onBeginningOfSpeech");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            Log.d(TAG, "onRmsChanged() called with: rmsdB = [" + rmsdB + "]"+LightSensorUtil.lightLevel);
            if (rmsdB > 10) {
                ScreenUtil.screenOn(MainActivity.this);
                if ((Integer.parseInt(mHour) > 20 || Integer.parseInt(mHour) < 6) && LightSensorUtil.lightLevel >= 0 && LightSensorUtil.lightLevel < 3) {
                    FlashlightUtil.toggleLight(true);
                }
                postScreenOff();
            }
            LastCallOnRmsChanged = System.currentTimeMillis();
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "onBufferReceived() called with: buffer = [" + buffer + "]");
        }

        @Override
        public void onEndOfSpeech() {
            Log.i(TAG, "onEndOfSpeech");
        }

        @Override
        public void onError(int error) {
            Log.i(TAG, "onError" + error);
            if (error != 8) {
                doSpeechRecognition();
            }
        }

        @Override
        public void onResults(Bundle results) {
            Log.i(TAG, "onResults");
            ArrayList<String> partialResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (partialResults != null && partialResults.size() > 0) {
                for (String partialResult : partialResults) {
                    Log.i(TAG, "onResults = " + partialResult);
                    handleCommendResult(partialResult);
                }
            }
            doSpeechRecognition();
        }

        @Override
        public void onPartialResults(Bundle bundle) {
            Log.i(TAG, "onPartialResults");
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent() called with: eventType = [" + eventType + "], params = [" + params + "]");
        }
    }

    /**
     * @param commend 处理指令
     */
    private boolean handleCommendResult(String commend) {
        Toast.makeText(MainActivity.this, commend, Toast.LENGTH_SHORT).show();
        //匹配指令
        if (VoiceKeyUtil.matchClockKey(commend)) {
            //播报时间
            SpeachUtil.speakTime(mHour, mMinute);
            return true;
        } else if (VoiceKeyUtil.matchDateKey(commend)) {
            //播报日期
            SpeachUtil.speakDate(mMonth, mDate);
            return true;
        } else if (VoiceKeyUtil.matchWeekKey(commend)) {
            //播报星期
            SpeachUtil.speakWeek();
            return true;
        } else if (VoiceKeyUtil.matchLightOnKey(commend)) {
            //开灯
            FlashlightUtil.toggleLight(true);
            return true;
        } else if (VoiceKeyUtil.matchLightOffKey(commend)) {
            //关灯
            FlashlightUtil.toggleLight(false);
            return true;
        } else if (VoiceKeyUtil.matchNameKey(commend)) {
            SpeachUtil.speak("我在");
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSpeechRecognizer.destroy();
    }
}