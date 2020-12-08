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
            String format = simpleDateFormat.format(new Date());
            String[] s = format.split(" ");
            mTextDate.setText(s[0]);
            mTextClock.setText(s[1]);
            mTextClock.postDelayed(this, 1000);
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
    };
    private final int DelayScreenOffTimeInMillis = 20 * 1000;

    public static SpeechRecognizer mSpeechRecognizer;
    private static long LastCallOnRmsChanged;
    private boolean recognitionAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |               //这个在锁屏状态下
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        mTextDate = findViewById(R.id.text_date);
        mTextClock = findViewById(R.id.text_clock);
        mTextClock.postDelayed(mRefreshAction, 1000);
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
        postScreenOff();
        if (mSpeechRecognizer == null) {
            mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            mSpeechRecognizer.setRecognitionListener(new MyRecognitionListener());
        }
        doSpeechRecognition();
    }

    public void doSpeechRecognition() {
        recognitionAvailable = SpeechRecognizer.isRecognitionAvailable(this);
        Log.d(TAG, "doSpeechRecognition() called recognitionAvailable = " + recognitionAvailable);
        if (recognitionAvailable) {
            mTextClock.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run(ee) called");
                    Intent recognitionIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    recognitionIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
                    recognitionIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh-CN");
                    mSpeechRecognizer.startListening(recognitionIntent);
                }
            }, 300);
        } else {
            Toast.makeText(MainActivity.this, "不支持语音命令", Toast.LENGTH_SHORT).show();
        }
    }

    private void postScreenOff() {
        mTextClock.removeCallbacks(mScreenOffAction);
        mTextClock.postDelayed(mScreenOffAction, DelayScreenOffTimeInMillis);
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
            Log.d(TAG, "onRmsChanged() called with: rmsdB = [" + rmsdB + "]");
            if (rmsdB > 12) {
                ScreenUtil.screenOn(MainActivity.this);
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
            SpeachUtil.speakTime(mTextClock.getText().toString());
            return true;
        } else if (VoiceKeyUtil.matchDateKey(commend)) {
            //播报日期
            SpeachUtil.speakDate(mTextDate.getText().toString());
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