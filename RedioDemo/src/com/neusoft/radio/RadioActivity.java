package com.neusoft.radio;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.neusoft.gemini.radio.adapter.PresetGridViewAdapter;
import com.neusoft.iviradio.IVIRadio;
import com.neusoft.iviradio.IVIRadio.Action;
import com.neusoft.iviradio.IVIRadio.ActionType;
import com.neusoft.iviradio.IVIRadio.Band;
import com.neusoft.iviradio.IVIRadio.Direction;
import com.neusoft.iviradio.IVIRadio.OnActionCompleteListener;
import com.neusoft.iviradio.IVIRadio.Source;
import com.neusoft.iviradio.IVIRadio.Switch;
import com.neusoft.iviradio.IVIRadioPresetStatus;
import com.neusoft.iviradio.IVIRadioStatus;
import com.neusoft.iviradio.IVIRadioStatus.Status;

/**
 * Class :RadioActivity
 * 
 * @author : neusoft
 */
public class RadioActivity extends Activity {
    private com.neusoft.popviewcontrol.ISourceControlService mISourceService;

    /**
     * TAG : the tag name
     */
    private final static String TAG = "RadioActivity";

    /**
     * NEU_GEMINI_RADIO_LAST_STATUS : sharepereferenc xml name
     */
    private final static String NEU_GEMINI_RADIO_LAST_STATUS = "radioLastStatus";

    /**
     * NEU_GEMINI_ACTION_POWER_OFF : 通知home关闭Tuner
     */
    private final static String NEU_GEMINI_ACTION_POWER_OFF = "com.neusoft.radio.poweroff";

    /**
     * NEU_GEMINI_ACTION_RESPONSE_STATION_INFO : from the home radio widget
     * response station info
     */
    private final static String NEU_GEMINI_ACTION_RESPONSE_STATION_INFO = "com.neusoft.radio.station";

    // VR cmd action，通知Radio上一台、下一台
    public final static String ACTION_VR_CMD = "com.neusoft.radio.vrcmd";
    // 系统ACC OFF广播
    public final static String ACTION_ACC_OFF = "android.intent.action.ACC_OFF";
    // 系统ACC ON广播
    public final static String ACTION_ACC_ON = "android.intent.action.ACC_ON";

    /**
     * NEU_GEMINI_BAND_AM : the am band value
     */
    private final static int NEU_GEMINI_BAND_AM = 0x00000003;

    /**
     * mFrequency : the textview to show the frequency
     */
    private TextView mFrequency;

    private String mPlayFreq = "";

    /**
     * mAselBtn : the a.sel button
     */
    private ImageButton mAselBtn;
    /**
     * mAutoPrevious : the auto seek previous button
     */
    private Button mAutoPrevious;
    /**
     * mAutoNext : the auto seek next button
     */
    private Button mAutoNext;
    /**
     * mManualPrevious : the manual previous button
     */
    private ImageButton mManualPrevious;
    /**
     * mManualNext : the manual next button
     */
    private ImageButton mManualNext;
    /**
     * mPowerOff : the power off button
     */
    private ImageButton mPowerOff;

    /**
     * mSourceIcon : the frequency unit
     */
    private TextView mSourceIcon;

    // 立体声图标
    private ImageView mStereoIcon;
    /**
     * mGraduation : the frequency ruler background
     */
    private ImageView mGraduation;
    /**
     * mSwitchBtn : the switch button
     */
    private ImageButton mSwitchBtn;
    /**
     * mFreqSeekBar : the freq ruler seek bar
     */
    private SeekBar mFreqSeekBar;
    /**
     * mAudioManager : the AudioManager object
     */
    private AudioManager mAudioManager;
    /**
     * mRadio : the IVIRadio object
     */
    private IVIRadio mRadio;

    /**
     * mLastSource : the current lastsource,is fm or am
     */
    public static int mLastSource = Source.FM;
    /**
     * mCurrentBand : the current band
     */
    private int mCurrentBand = Band.FM1;
    /**
     * mFMStep : the fm step
     */
    private int mFMStep = 0;
    /**
     * mAMStep : the am step
     */
    private int mAMStep = 0;

    /**
     * mCurrentStatus : the radio current status
     */
    private int mCurrentStatus = Status.NORMAL;

    /**
     * mOverTimeMsg : the message to overtime
     */
    private Message mOverTimeMsg;

    private boolean mCloseTuner = false;

    /**
     * mRecevieRadioFreq : the most small frequence of fm
     */
    private String mRecevieRadioFreq = "87.5";
    /**
     * mSpLastFreqDefault : the defualt value of frequence from sharepreference
     */
    private String mSpLastFreqDefault = "87.5";
    /**
     * mSpLastSource : the defualt vaule of source from sharepreference
     */
    private int mSpLastSource = Source.FM;
    /**
     * mSpLastFreq : the default value of the last frequence from
     * sharepreference
     */
    private String mSpLastFreq = "87.5";

    /**
     * mFmMultiple : the fm multiple
     */
    private int mFmSeekBarMax = 205;

    /**
     * mAmMultiple : the am multiple
     */
    private int mAmSeekBarMax = 119;

    /**
     * the fm preset save num
     */
    private int mSaveFMPresetNum = 0;

    /**
     * the am preset save num
     */
    private int mSaveAMPresetNum = 0;

    /**
     * mSearch : the search button
     */
    private Button mSearch;
    /**
     * mDialogView : the A.sel dialog view
     */
    private View mDialogView;
    /**
     * mPopupWindow : the popupwindow object
     */
    private PopupWindow mPopupWindow;

    /**
     * isScanOn : the flag of scan button status
     */
    private boolean isScanOn = false;

    private boolean isSeekOn = false;

    private boolean isASelOn = false;
    /**
     * @return the isASelOn
     */
    public boolean isASelOn() {
        return isASelOn;
    }
    /**
     * @param isASelOn the isASelOn to set
     */
    public void setASelOn(boolean isASelOn) {
        this.isASelOn = isASelOn;
    }
    private boolean isScanHoldOn = false;

    /**
     * mFmFreqSteps : the fm preset frequence list
     */
    private List<String> mFmFreqs;
    /**
     * mAmFreqSteps : the am preset frequence list
     */
    private List<String> mAmFreqs;
    /**
     * mFmPresetGridView : the fm preset gridview
     */
    private GridView mFmPresetGridView;
    /**
     * mAmPresetGridView : the am preset gridview
     */
    private GridView mAmPresetGridView;
    /**
     * mFmPresetAdapter : the fm preset listview adapter
     */
    private PresetGridViewAdapter mFmPresetAdapter;
    /**
     * mAmPresetAdapter : the am preset listview adapter
     */
    private PresetGridViewAdapter mAmPresetAdapter;
    /**
     * mScanBtn : the scan button
     */
    private ImageButton mScanBtn;

    /**
     * mFocusable : is the turner get the focus
     */
    private boolean mFocusable = false;

    private ServiceConnection mConnect = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mISourceService = com.neusoft.popviewcontrol.ISourceControlService.Stub
                    .asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mISourceService = null;
        }

    };
    private boolean isPowerOff = false;

    private BroadcastReceiver mRadioReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "comin---->onReceive, action = " + action);
            if (action.equals(ACTION_ACC_ON)) {
                // 接到ACC On通知，判断当前是否是Power On状态，0→Power Off；1→Power On
                // Power Off时不作处理
                int status = Settings.Global.getInt(
                        context.getContentResolver(), "phone_power_status", 0);
                if (status == 0) {
                    return;
                }
                
            }
            if (action.equals(Intent.ACTION_POWER_OFF) || action.equals(ACTION_ACC_OFF)) {
                isPowerOff = true;
                saveSourceStatus(mLastSource);
                saveFreqStatus(mPlayFreq);
                mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
                if (!mFocusable) {
                    return;
                }
                // 系统Power Off，停止一切动作，V850侧自动切为Power Off状态
                resetScanBtn();
                mStereoIcon.setVisibility(View.INVISIBLE);
                // source off时按钮不可点击
                if (Source.FM == mLastSource) {
                    mFmPresetAdapter.setValid(false);
                    mSwitchBtn
                            .setBackgroundResource(R.drawable.neu_gemini_fm_unclickable);
                    mSwitchBtn.setClickable(false);
                } else if (Source.AM == mLastSource) {
                    mAmPresetAdapter.setValid(false);
                    mSwitchBtn
                            .setBackgroundResource(R.drawable.neu_gemini_am_unclickable);
                    mSwitchBtn.setClickable(false);
                }
                isASelOn = false;
                isSeekOn = false;
                mAselBtn.setClickable(false);
                mAselBtn.setBackgroundResource(R.drawable.neu_gemini_asel_unclickable);
                setBtnClickable(false);
                // 频率条不可拖动
                mFreqSeekBar.setEnabled(false);
                mFocusable = false;
                return;
            }
            int result = 0;
            if (action.equals(Intent.ACTION_POWER_ON) || action.equals(ACTION_ACC_ON)) {
                isPowerOff = false;
                if (RadioUtils.isCurrentMediaSource(getApplicationContext())) {
                    Log.d(TAG, "ACTION_POWER_ON _ || ACTION_ACC_ON  378" + isPowerOff );
                        Log.d(TAG, "ACTION_POWER_ON _ || ACTION_ACC_ON  380");
                        result = mAudioManager.requestAudioFocus(
                                onAudioFocusChangeListener, AudioManager.STREAM_TUNER,
                                AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
                        Log.d(TAG, "ACTION_POWER_ON _ || ACTION_ACC_ON  384");
                    Log.d(TAG, "GeminiRadio requestAudioFocus");
                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        mFocusable = true;
                        mRadio.setSource(Switch.ON, mLastSource);
                    } else {
                        mFocusable = false;
                    }
                }
                if (!mFocusable) {
                    return;
                }
                // 系统Power On，通知V850侧切为Source On状态
                mRadio.setSource(Switch.ON, mLastSource);
                initRadioStatus();
                
                return;
            }
            if (action.equals(ACTION_VR_CMD)) {
                boolean isSuccess = false;
                String cmd = intent.getStringExtra("vr-ctrl-cmd");
                long recevieTimer = intent.getLongExtra("timer",
                        System.currentTimeMillis());
                Log.i(TAG, "comin---->onReceive, cmd = " + cmd + " ,mFocus = "
                        + mFocusable);
                if (cmd.equals("pre")) {
                    // VR 唤醒,先Source On,延时1s进行Seek
                    Message message = mHandler
                            .obtainMessage(MsgConst.MSG_RESUME_PRESET_PRE);
                    mHandler.sendMessageDelayed(message, 1000);
                    isSuccess = true;
                } else if (cmd.equals("next")) {
                    // VR 唤醒,先Source On,延时1s进行Seek
                    Message message = mHandler
                            .obtainMessage(MsgConst.MSG_RESUME_PRESET_NEXT);
                    mHandler.sendMessageDelayed(message, 1000);
                    isSuccess = true;
                }
                RadioUtils.responseVR(context, isSuccess, recevieTimer);
                return;
            }
            int keyCode = intent
                    .getIntExtra(Intent.EXTRA_C3_HARDKEY_KEYCODE, 0);
            // 在获得焦点的情况下，响应方向盘Key
            if (!mFocusable) {
                return;
            }
            // 在自动搜台过程中，不响应方向盘key
            if (isASelOn) {
                return;
            }

            switch (keyCode) {
            case KeyEvent.KEYCODE_VR:
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
                break;
            default:
                break;
            }
        }
    };

    /**
     * onAudioFocusChangeListener : the audio focus change listener
     */
    private OnAudioFocusChangeListener onAudioFocusChangeListener = new OnAudioFocusChangeListener() {

        @Override
        public void onAudioFocusChange(int focusChange) {
            Log.e(TAG, "onAudioFocusChange-->" + focusChange);
            if (AudioManager.AUDIOFOCUS_GAIN == focusChange 
                    && RadioUtils.isCurrentMediaSource(getApplication())) {

                if (((RadioApplicationHelper) getApplicationContext())
                        .getFlag() == 0) {
                    // 若VR没处理
                    String radioType = ((RadioApplicationHelper) getApplicationContext())
                            .getRadioType();
                    String radioFreq = ((RadioApplicationHelper) getApplicationContext())
                            .getRadioName();
                    boolean isBadFreq = ((RadioApplicationHelper) getApplicationContext())
                            .getIsBadFreq();
                    if (isBadFreq) {
                        // 无效周波数回复
                        long timer = ((RadioApplicationHelper) getApplicationContext())
                                .getTimer();
                        ((RadioApplicationHelper) getApplicationContext()).setFlag(1);
                        RadioUtils.responseVR(getApplicationContext(), false, timer);
                        return;
                    }
                    if (!answerVRRequest(false, radioFreq.equals("999999"),
                            radioType, radioFreq)) {
                        mRadio.setSource(Switch.ON, mLastSource);
                    }
                } else {
                    mRadio.setSource(Switch.ON, mLastSource);
                }
                mFocusable = true;
                Log.i(TAG, "comin---->get focus");
                Log.d(TAG, "GeminiRadio requestAudioFocus GAIN");
                initRadioStatus();

            } else {
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                    closeTuner(false);
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    return;
                }
                resetScanBtn();
                mStereoIcon.setVisibility(View.INVISIBLE);
                mFocusable = false;
                mRadio.setSource(Switch.OFF, mLastSource);
                Log.i(TAG, "comin---->lost focus");
                Log.d(TAG, "GeminiRadio requestAudioFocus failed");
                // source off时按钮不可点击
                if (Source.FM == mLastSource) {
                    mFmPresetAdapter.setValid(false);
                    mSwitchBtn
                            .setBackgroundResource(R.drawable.neu_gemini_fm_unclickable);
                    mSwitchBtn.setClickable(false);
                } else if (Source.AM == mLastSource) {
                    mAmPresetAdapter.setValid(false);
                    mSwitchBtn
                            .setBackgroundResource(R.drawable.neu_gemini_am_unclickable);
                    mSwitchBtn.setClickable(false);
                }
                isSeekOn = false;
                isASelOn = false;
                mAselBtn.setClickable(false);
                mAselBtn.setBackgroundResource(R.drawable.neu_gemini_asel_unclickable);
                setBtnClickable(false);
                // 频率条不可拖动
                mFreqSeekBar.setEnabled(false);
            }
        }
    };

    /**
     * mHandler : the handler object to refresh the UI
     */
    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MsgConst.MSG_ACTION_COMPLETE:
                try {
                    onActionComplete(msg.arg1, msg.arg2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MsgConst.MSG_REFRESHING_START:
                Log.d(TAG, "start=> mRadio.refreshStationList(Action.START, "
                        + mLastSource + ");");
                mRadio.refreshStationList(Action.START, mLastSource);
                Log.d(TAG, "end  => mRadio.refreshStationList(Action.START, "
                        + mLastSource + ");");
                break;
            case MsgConst.MSG_RESUME_PRESET_PRE:
                prePreset();
                break;
            case MsgConst.MSG_RESUME_PRESET_NEXT:
                nextPreset();
                break;
            default:
                break;
            }
        }
    };

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.neu_gemini_main_radio);
        Log.d(TAG, "onCreate");

        SharedPreferences sp = getSharedPreferences(
                 NEU_GEMINI_RADIO_LAST_STATUS, Context.MODE_WORLD_READABLE);
        mSpLastSource = sp.getInt("lastSource", Source.FM);
        mSpLastFreq = sp.getString("lastFreq", mSpLastFreqDefault);
        initViews();
        initRadio();
        getPresets();

        registerBroadcastReceiver();
        Log.i(TAG, "onCreate end");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!RadioUtils.isCurrentMediaSource(getApplicationContext())) {
            closeTuner(false);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
        if (mCloseTuner) {
            Log.i(TAG, "kill process");
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    };

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        } else {
            moveRadioToBack();
        }

    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_VR_CMD);
        intentFilter.addAction(Intent.ACTION_POWER_OFF);
        intentFilter.addAction(Intent.ACTION_POWER_ON);
        registerReceiver(mRadioReceiver, intentFilter);
    }
    /**
     * brief : init the views ,and set listenner to them
     * <p>
     * detail : Detailed description of the method (Complex method only) [BugID]
     * </p>
     * ProjectID ：NEU_GEMINI
     * 
     * @param
     * @return void
     * @throws
     */
    private void initRadio() {
        Log.i(TAG, "initRadio start");
        PowerManager mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        isPowerOff = !(mPowerManager.getCurrentPowerStatus() == PowerManager.NEU_C3_ALFUS_CURRENT_POWER_STATUS_ON);
        Intent connectIntent = new Intent();
        connectIntent
                .setAction("com.neusoft.sourcectrlservic.ISourceControllerService");
        bindService(connectIntent, mConnect, Service.BIND_AUTO_CREATE);
        
        RadioUtils.setCurrentMediaSource(this);
        int result = 0;
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Log.d(TAG, "initRadio() 723");
        if(!isPowerOff){
            Log.d(TAG, "initRadio() 725");
            result = mAudioManager.requestAudioFocus(
                    onAudioFocusChangeListener, AudioManager.STREAM_TUNER,
                    AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
            Log.d(TAG, "initRadio() 729");
        }
        Log.d(TAG, "GeminiRadio requestAudioFocus");
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mFocusable = true;
            Log.i(TAG, "comin---->get focus");
            Log.d(TAG, "GeminiRadio requestAudioFocus GAIN");
        } else {
            mFocusable = false;
            Log.i(TAG, "comin---->lost focus");
            Log.d(TAG, "GeminiRadio requestAudioFocus Failed");
        }

        mRadio = new IVIRadio();

        mRadio.setActionCompleteListener(new OnActionCompleteListener() {

            @Override
            public void OnActionComplete(int action, int result) {
                Log.d(TAG, "OnActionComplete===action=[" + action
                        + "] result=[" + result + "]");
                Message message = mHandler.obtainMessage(
                        MsgConst.MSG_ACTION_COMPLETE, action, result);
                mHandler.sendMessage(message);
            }

        });

        Log.d(TAG, "start getband");
        IVIRadioStatus status = mRadio.getRadioStatus(1);
        int band = status.getBand();
        mFrequency.setText(makeFmFrequency(status.getFrequencyStep()));
        Log.d(TAG, "Band = " + band);
        Intent intent = getIntent();
        if (intent != null) {
            // 通过VR启动Radio
            boolean isVROpen = intent.getIntExtra("open", 0) == 1;
            boolean isChangeBand = intent.getBooleanExtra("change-band", false);
            boolean isBadFreq = intent.getBooleanExtra("bad-freq", false);
            String radioType = intent.getStringExtra("radio-type");
            String radioFreq = intent.getStringExtra("radio-freq");
            if (isBadFreq) {
                // 无效周波数回复
                RadioUtils.responseVR(this, false, intent.getLongExtra("timer",
                        System.currentTimeMillis()));
            }
            if (!answerVRRequest(isVROpen, isChangeBand, radioType, radioFreq)) {
                mRadio.setSource(Switch.ON, mLastSource);
            }

        } else {
            mRadio.setSource(Switch.ON, mLastSource);
        }

        if (!mFocusable) {
            mAselBtn.setClickable(false);
            mAselBtn.setBackgroundResource(R.drawable.neu_gemini_asel_unclickable);
            setBtnClickable(false);
            // 频率条不可拖动
            mFreqSeekBar.setEnabled(false);
        }
        setTunerMode();
    }

    private void initViews() {
        Log.i(TAG, "initViews start");
        mScanBtn = (ImageButton) findViewById(R.id.radio_scan);
        mScanBtn.setClickable(true);

        mScanBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isScanOn) {
                    Log.e(TAG, "Scan Stop");
                    mRadio.scan(Action.STOP, 0x00);
                } else {
                    Log.e(TAG, "Scan Start");
                    mRadio.scan(Action.START, 0xff);
                }
            }
        });

        mAselBtn = (ImageButton) findViewById(R.id.radio_auto_select);
        mSwitchBtn = (ImageButton) findViewById(R.id.radio_band_switch);
        mFrequency = (TextView) findViewById(R.id.radio_frequency);
        mPlayFreq = mSpLastFreq;
        mFrequency.setText(mPlayFreq);
        mSourceIcon = (TextView) findViewById(R.id.radio_frequency_unit);
        mStereoIcon = (ImageView) findViewById(R.id.radio_stereo_icon);
        mGraduation = (ImageView) findViewById(R.id.radio_ruler_bg);
        mFreqSeekBar = (SeekBar) findViewById(R.id.radio_seekBar);
        mAutoPrevious = (Button) findViewById(R.id.radio_auto_select_down);
        mAutoNext = (Button) findViewById(R.id.radio_auto_select_up);
        mManualPrevious = (ImageButton) findViewById(R.id.radio_manual_down);
        mManualNext = (ImageButton) findViewById(R.id.radio_manual_up);
        mPowerOff = (ImageButton) findViewById(R.id.radio_off);
        mLastSource = mSpLastSource;
        if (Source.FM == mLastSource) {
            mSwitchBtn.setBackgroundResource(R.drawable.neu_gemini_radio_fm);
        } else {
            mSwitchBtn.setBackgroundResource(R.drawable.neu_gemini_radio_am);
        }
        mAmPresetGridView = (GridView) findViewById(R.id.radio_am_present);
        mFmPresetGridView = (GridView) findViewById(R.id.radio_fm_present);

        mAselBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                
                if (mPopupWindow == null) {
                    initDialog();
                }
                if (mPopupWindow != null && !mPopupWindow.isShowing()) {
                    mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);
                }
            }
        });

        mSwitchBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mAmPresetAdapter != null) {
                    Log.d(TAG, "mAmPresetAdapter.removePresetRunable()  + 957");
                    mAmPresetAdapter.removePresetRunable();
                } 
                if (mFmPresetAdapter != null) {
                    Log.d(TAG, "mFmPresetAdapter.removePresetRunable()  + 961");
                    mFmPresetAdapter.removePresetRunable();
                } 
                resetScanBtn();
                if (Source.FM == mLastSource) {
                    Log.e(TAG, "onClick changeAMSource()");
                    mRadio.changeBand(Band.AM);
                    mStereoIcon.setVisibility(View.INVISIBLE);
                } else {
                    Log.e(TAG, "onClick changeFMSource ");
                    mRadio.changeBand(Band.FM1);
                }
            }
        });

        mFreqSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!mPlayFreq.equals(mFrequency.getText().toString())) {
                    mPlayFreq = mFrequency.getText().toString();
                    Double step = Double.parseDouble(mPlayFreq);
                    setManualTuning(step);
                    if (isScanOn || isSeekOn) {
                        isSeekOn = false;
                        resetScanBtn();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (isScanOn) {
                    resetScanBtn();
                    mRadio.scan(Action.STOP, 0x00);
                }
                if (isSeekOn) {
                    isSeekOn = false;
                    mRadio.seek(Action.STOP, Direction.STOP, 0x00);
                }
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                    boolean fromUser) {
                if (fromUser) {
                    Log.d(TAG, "onProgressChanged isScanOn:"+isScanOn);
                    if (isScanOn) {
                        resetScanBtn();
                        mRadio.scan(Action.STOP, 0x00);
                        Log.d(TAG, "mRadio.scan(Action.STOP, 0x00)");
                    }
                    if (Source.FM == mLastSource) {
                        mFMStep = (int) (progress);
                        mFrequency.setText(makeFmFrequency(mFMStep));
                    } else {
                        mAMStep = (int) (progress);
                        mFrequency.setText(makeAmFrequency(mAMStep));
                    }
                }
            }
        });

        mAutoPrevious.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                seekDown();
            }
        });

        mManualPrevious.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                manualPrevious();
            }
        });

        mAutoNext.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                seekUp();
            }
        });

        mManualNext.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                manualNext();
            }
        });

        mPowerOff.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 点击一次后，不可再次点击，防止多次拉起Home卡顿
                mPowerOff.setEnabled(false);
                mCloseTuner = true;
                closeTuner(true);
                // 通知LastSource Tuner已关闭
                RadioUtils.updateLastAppSource(getApplicationContext(), "");
                moveRadioToBack();
            }
        });
        Log.i(TAG, "initViews end");
    }

    private void onActionComplete(int action, int result) {
        IVIRadioStatus radioSts = mRadio.getRadioStatus(mCurrentBand);

        switch (action) {
        case ActionType.FREQUENCY_CHANGE:
            Log.i(TAG, "onActionComplete ------ FREQUENCY_CHANGE");
            Log.i(TAG, "result = " + result);
            changeTextData(radioSts);
            break;
        case ActionType.SET_SOURCE:
            Log.e(TAG, "onActionComplete ---> ActionType.SET_SOURCE");
            if (Source.FM == mLastSource) {
                if (mFocusable) {
                    mSwitchBtn
                            .setBackgroundResource(R.drawable.neu_gemini_radio_fm);
                } else {
                    mSwitchBtn
                            .setBackgroundResource(R.drawable.neu_gemini_fm_unclickable);
                }
                mSourceIcon.setText(R.string.mhz);
                mGraduation.setImageResource(R.drawable.neu_gemini_fm_scale);
                mFreqSeekBar.setMax(mFmSeekBarMax);
                RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) mFreqSeekBar
                        .getLayoutParams();
                relativeParams.width = 530;
                relativeParams.leftMargin = 51;
                mFreqSeekBar.setLayoutParams(relativeParams);
                mFreqSeekBar.setVisibility(View.VISIBLE);

                changeTextData(radioSts);

                getPresets();
            } else {
                if (mFocusable) {
                    mSwitchBtn
                            .setBackgroundResource(R.drawable.neu_gemini_radio_am);
                } else {
                    mSwitchBtn
                            .setBackgroundResource(R.drawable.neu_gemini_am_unclickable);
                }
                mSourceIcon.setText(R.string.khz);
                mGraduation.setImageResource(R.drawable.neu_gemini_am_scale);
                mFreqSeekBar.setMax(mAmSeekBarMax);
                RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) mFreqSeekBar
                        .getLayoutParams();
                relativeParams.width = 552;
                relativeParams.leftMargin = 30;
                mFreqSeekBar.setLayoutParams(relativeParams);
                mFreqSeekBar.setVisibility(View.VISIBLE);

                changeTextData(radioSts);

                getPresets();
            }
            Log.i(TAG, "preset number = " + radioSts.mPresetNumber);
            changePresetItemBg(radioSts);
            break;
        case ActionType.SEEK_START:
            isSeekOn = true;
            changePresetItemBg(radioSts);
            notifiHomeStatusChange();
            break;
        case ActionType.PRESET_MEMORY:
            Log.d(TAG,"ActionType.PRESET_MEMORY");
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
            radioSts = mRadio.getRadioStatus(mCurrentBand);
            if (Source.FM == mLastSource) {
                String freq = makeFmFrequency(radioSts.getFrequencyStep());
                mFmFreqs.set(mSaveFMPresetNum, freq);
                mFmPresetAdapter.setData(mSaveFMPresetNum, freq);
            } else if (Source.AM == mLastSource) {
                String freq = makeAmFrequency(radioSts.getFrequencyStep());
                mAmFreqs.set(mSaveAMPresetNum, freq);
                mAmPresetAdapter.setData(mSaveAMPresetNum, freq);
            }
            changePresetItemBg(radioSts);
            if(isASelOn){
                onActionComplete(ActionType.REFRESHING_STATION_LIST_FINISH, 0);
            }
            break;
        case ActionType.PRESET_CALL:
            Log.d(TAG,"ActionType.PRESET_CALL");
            isSeekOn = false;
            changeTextData(radioSts);
            changePresetItemBg(radioSts);
            if(isASelOn){
                onActionComplete(ActionType.REFRESHING_STATION_LIST_FINISH, 0);
            }
            notifiHomeStatusChange();
            break;
        case ActionType.CHANGE_SEEK_MODE:
            changeTextData(radioSts);
            break;
        case ActionType.SCAN_START:
            Log.i(TAG, "onActionComplete ------ SCAN_START");
            if (!isScanOn) {
                isScanOn = true;
                mScanBtn.setBackgroundResource(R.drawable.neu_gemini_scan_ing);
            }
            changeTextData(radioSts);
            changePresetItemBg(radioSts);
            notifiHomeStatusChange();
            break;
        case ActionType.BAND_CHANGE:
            Log.d(TAG, "BAND_CHANGE lastSource = " + mLastSource);
            isSeekOn = false;
            mCurrentBand = radioSts.getBand();
            if (mCurrentBand == Band.FM1) {
                mLastSource = Source.FM;
            } else {
                mLastSource = Source.AM;
            }
            mFreqSeekBar.setVisibility(View.INVISIBLE);
            if (Source.FM == mLastSource) {
                changeFMSource();
                changeTextData(radioSts);
                Message message = mHandler.obtainMessage(
                        MsgConst.MSG_ACTION_COMPLETE,
                        ActionType.REFRESHING_STATION_LIST_FINISH, 0);
                mHandler.sendMessage(message);
            } else {
                changeAMSource();
                changeTextData(radioSts);
                Message message = mHandler.obtainMessage(
                        MsgConst.MSG_ACTION_COMPLETE,
                        ActionType.REFRESHING_STATION_LIST_FINISH, 0);
                mHandler.sendMessage(message);
            }
            mFreqSeekBar.setVisibility(View.VISIBLE);
            break;
        case ActionType.MANUAL_TUNING_FINISH:
            isSeekOn = false;
            Log.d(TAG, "MANUAL_TUNING_FINISH  changeTextData");
            changeTextData(radioSts);
            Log.i(TAG, "preset number = " + radioSts.mPresetNumber);
            changePresetItemBg(radioSts);
            break;
        case ActionType.SEEK_STOP:
            isSeekOn = false;
            changeTextData(radioSts);
            changePresetItemBg(radioSts);
            notifiHomeStatusChange();
            break;
        case ActionType.SEEK_CANCEL:
            isSeekOn = false;
            changeTextData(radioSts);
            changePresetItemBg(radioSts);
            notifiHomeStatusChange();
            break;
        case ActionType.PRESETNO_CHANGE:
            Log.i(TAG, "preset number = " + radioSts.mPresetNumber);
            Log.i(TAG, "result = " + result);
            changeTextData(radioSts);
            changePresetItemBg(radioSts);
            break;
        case ActionType.REFRESHING_STATION_LIST_START:
            Log.d(TAG, "REFRESHING_STATION_LIST_START");
            isASelOn = true;
            changePresetItemBg(radioSts);
            notifiHomeStatusChange();
            break;
        case ActionType.REFRESHING_STATION_LIST_STOP:
            Log.e(TAG, "ActionType.REFRESHING_STATION_LIST_STOP");
            isASelOn = false;
            changePresetItemBg(radioSts);
            break;
        case ActionType.REFRESHING_STATION_LIST_CANCEL:
            isASelOn = false;
            changePresetItemBg(radioSts);
            break;
        case ActionType.REFRESHING_STATION_LIST_FINISH:
            Log.d(TAG, "REFRESHING_STATION_LIST_FINISH");

            isASelOn = false;
            if (999 == result) {
                Log.e(TAG, "time out!");
            } else {
                mHandler.removeCallbacksAndMessages(mOverTimeMsg);
                changeTextData(radioSts);
            }

            if (Source.FM == mLastSource) {
                mFmPresetAdapter.setValid(true);
                mSwitchBtn.setBackgroundResource(R.drawable.neu_gemini_radio_fm);
                mSwitchBtn.setClickable(true);
            } else if (Source.AM == mLastSource) {
                mAmPresetAdapter.setValid(true);
                mSwitchBtn.setBackgroundResource(R.drawable.neu_gemini_radio_am);
                mSwitchBtn.setClickable(true);
            }
            mAselBtn.setClickable(true);
            mAselBtn.setBackgroundResource(R.drawable.neu_gemini_asel_btn);
            setBtnClickable(true);
            mFreqSeekBar.setEnabled(true);

            getPresets();
            changePresetItemBg(radioSts);
            break;
        case ActionType.SOURCE_ON_FINISH:
            Log.d(TAG, "SOURCE_ON_FINISH");

            if (Source.FM == mLastSource) {
                mSwitchBtn.setBackgroundResource(R.drawable.neu_gemini_radio_fm);
                mSourceIcon.setText(R.string.mhz);
                mGraduation.setImageResource(R.drawable.neu_gemini_fm_scale);
                mFreqSeekBar.setMax(mFmSeekBarMax);
                RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) mFreqSeekBar
                        .getLayoutParams();
                relativeParams.width = 530;
                relativeParams.leftMargin = 51;
                mFreqSeekBar.setLayoutParams(relativeParams);
                mFreqSeekBar.setVisibility(View.VISIBLE);

                getPresets();
            } else {
                mSwitchBtn.setBackgroundResource(R.drawable.neu_gemini_radio_am);
                mSourceIcon.setText(R.string.khz);
                mGraduation.setImageResource(R.drawable.neu_gemini_am_scale);
                mFreqSeekBar.setMax(mAmSeekBarMax);
                RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) mFreqSeekBar
                        .getLayoutParams();
                relativeParams.width = 552;
                relativeParams.leftMargin = 30;
                mFreqSeekBar.setLayoutParams(relativeParams);
                mFreqSeekBar.setVisibility(View.VISIBLE);

                getPresets();
            }
            Log.d(TAG, "SOURCE_ON_FINISH,isASelOn:"+isASelOn);
            if(isASelOn){
                onActionComplete(ActionType.REFRESHING_STATION_LIST_FINISH, 0);
            }
            notifiHomeStatusChange();
            break;
        case ActionType.SCAN_STOP:
            Log.i(TAG, "onActionComplete ------ SCAN_STOP");
            isScanOn = false;
            isScanHoldOn = false;
            mScanBtn.setBackgroundResource(R.drawable.neu_gemini_scan_btn);
            changePresetItemBg(radioSts);
            notifiHomeStatusChange();
            break;
        case ActionType.INF_STEREO:
            Log.i(TAG, "onActionComplete ------ INF_STEREO");
            Log.i(TAG, "stereo = " + radioSts.getStereo());
            if (radioSts.getStereo() == 0x00) {
                mStereoIcon.setVisibility(View.INVISIBLE);
            } else {
                mStereoIcon.setVisibility(View.VISIBLE);
            }
            break;
        case ActionType.SCAN_HOLD_START:
            Log.i(TAG, "onActionComplete ------ SCAN_HOLD_START");
            Log.i(TAG, "SCAN_HOLD_START preset number = " + radioSts.mPresetNumber);
            isScanHoldOn = true;
            changeTextData(radioSts);
            notifiHomeStatusChange();
            break;
        case ActionType.SCAN_HOLD_STOP:
            Log.i(TAG, "onActionComplete ------ SCAN_HOLD_STOP");
            Log.i(TAG, "SCAN_HOLD_STOP preset number = " + radioSts.mPresetNumber);
            isScanHoldOn = false;
            changeTextData(radioSts);
            notifiHomeStatusChange();
            break;

        default:
            break;
        }
    }

    /**
     * mFrequency seekBar同步更新
     * 
     * @param radioSts
     */
    private void changeTextData(IVIRadioStatus radioSts) {

        if (Source.FM == mLastSource) {
            mFrequency.setText(makeFmFrequency(radioSts.getFrequencyStep()));
            mFreqSeekBar.setProgress((int) (radioSts.getFrequencyStep()));
        } else {
            mFrequency.setText(makeAmFrequency(radioSts.getFrequencyStep()));
            mFreqSeekBar.setProgress((int) (radioSts.getFrequencyStep()));
        }

        if (!isASelOn && !(isScanOn && !isScanHoldOn) && !isSeekOn) {
            mPlayFreq = mFrequency.getText().toString();
            notifiHomeStatusChange();
        }
    }

    private String makeFmFrequency(int step) {
        if (step < 0) {
            return "(null)";
        } else {
            return "" + (step + 875.0) / 10.0;
        }
    }

    private String makeAmFrequency(int step) {
        if (step < 0) {
            return "(null)";
        } else {
            return "" + (step * 9 + 531);
        }
    }

    /**
     * change FM Source
     */
    private void changeFMSource() {
        try {
            if (mISourceService != null) {
                mISourceService.setAppSourceMode(AudioManager.STREAM_TUNER,
                        AudioManager.MODE_IN_TUNNER_FM);
                Log.d(TAG,
                        "GeminiRadio setAppSourceMode = AudioManager.MODE_IN_TUNNER_FM OK");
            }

        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d(TAG,
                    "GeminiRadio setAppSourceMode = AudioManager.MODE_IN_TUNNER_FM failed");
        }
        mSwitchBtn.setBackgroundResource(R.drawable.neu_gemini_radio_fm);
        mSourceIcon.setText(R.string.mhz);
        mGraduation.setImageResource(R.drawable.neu_gemini_fm_scale);
        mFreqSeekBar.setMax(mFmSeekBarMax);
        RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) mFreqSeekBar
                .getLayoutParams();
        relativeParams.width = 530;
        relativeParams.leftMargin = 51;
        mFreqSeekBar.setLayoutParams(relativeParams);

        getPresets();
        saveSourceStatus(mLastSource);
    }

    /**
     * change AM Source
     */
    private void changeAMSource() {
        try {
            if (mISourceService != null) {
                mISourceService.setAppSourceMode(AudioManager.STREAM_TUNER,
                        AudioManager.MODE_IN_TUNNER_AM);
                Log.d(TAG,
                        "GeminiRadio setAppSourceMode = AudioManager.MODE_IN_TUNNER_AM OK");
            }

        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d(TAG,
                    "GeminiRadio setAppSourceMode = AudioManager.MODE_IN_TUNNER_AM failed");
        }
        mSwitchBtn.setBackgroundResource(R.drawable.neu_gemini_radio_am);
        mSourceIcon.setText(R.string.khz);
        mGraduation.setImageResource(R.drawable.neu_gemini_am_scale);
        mFreqSeekBar.setMax(mAmSeekBarMax);
        RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams) mFreqSeekBar
                .getLayoutParams();
        relativeParams.width = 552;
        relativeParams.leftMargin = 30;
        mFreqSeekBar.setLayoutParams(relativeParams);

        getPresets();
        saveSourceStatus(mLastSource);
    }

    private void saveSourceStatus(int source) {
        SharedPreferences sp = getSharedPreferences(
                NEU_GEMINI_RADIO_LAST_STATUS, Context.MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("lastSource", source);

        editor.commit();
    }

    private void saveFreqStatus(String freq) {
        SharedPreferences sp = getSharedPreferences(
                NEU_GEMINI_RADIO_LAST_STATUS, Context.MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("lastFreq", freq);

        editor.commit();
    }
    /**
     * 跳到指定台
     * 
     * @param step
     */
    private void setManualTuning(double step) {
        Log.d(TAG, "跳转到 setManualTuning(" + step + ")");
        if (Source.FM == mLastSource) {
            mFMStep = (int) (step * 10 - 875);
            Log.d(TAG,
                    "start=> Source.FM mRadio.manualTuning(Direction.DIRECT, "
                            + mFMStep + ");");
            mRadio.manualTuning(Direction.DIRECT, mFMStep);
            Log.d(TAG,
                    "end  => Source.FM mRadio.manualTuning(Direction.DIRECT, "
                            + mFMStep + ");");

        } else {
            mAMStep = (int) ((step - 531) / 9);
            Log.d(TAG,
                    "start=> Source.AM mRadio.manualTuning(Direction.DIRECT, "
                            + mAMStep + ");");
            mRadio.manualTuning(Direction.DIRECT, mAMStep);
            Log.d(TAG,
                    "end  => Source.AM mRadio.manualTuning(Direction.DIRECT, "
                            + mAMStep + ");");
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        TextView saveText = (TextView) findViewById(R.id.radio_band_save);
        saveText.setText(R.string.save_message);
        TextView scanText = (TextView) findViewById(R.id.scan_text);
        scanText.setText(R.string.scan);
        TextView aselText = (TextView) findViewById(R.id.auto_select_text);
        aselText.setText(R.string.asel);
        if (mPopupWindow != null) {
            TextView aselTitle = (TextView) mDialogView.findViewById(R.id.auto_select_title);
            aselTitle.setText(R.string.auto_select_popup_title);
            TextView aselMessage = (TextView) mDialogView.findViewById(R.id.auto_select_message);
            aselMessage.setText(R.string.auto_select_popup_content);
            Button cancel = (Button) mDialogView.findViewById(R.id.cancel);
            cancel.setText(R.string.cancel);
            Button search = (Button) mDialogView.findViewById(R.id.search);
            search.setText(R.string.search);
        }

    };

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();

        this.unbindService(mConnect);
        unregisterReceiver(mRadioReceiver);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG,"comin---->OnNewIntent");

        // 从B区点击Radio图标，若将关闭进程过程打断，重新关闭进程(点击关闭按钮同时点击B区图标)
        if (mCloseTuner) {
            closeTuner(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            return;
        }

        RadioUtils.setCurrentMediaSource(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        int result = 0;
        if (!mFocusable && RadioUtils.isCurrentMediaSource(this)) {
            Log.d(TAG, "onResume() 1599");
            if(!isPowerOff){
                Log.d(TAG, "onResume() 1601");
             result = mAudioManager.requestAudioFocus(
                    onAudioFocusChangeListener, AudioManager.STREAM_TUNER,
                    AudioManager.AUDIOFOCUS_REQUEST_GRANTED);
             Log.d(TAG, "onResume() 1605");
            }
            Log.d(TAG, "GeminiRadio requestAudioFocus");
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mFocusable = true;
                mRadio.setSource(Switch.ON, mLastSource);
                Log.i(TAG, "comin---->get focus");
                Log.d(TAG, "GeminiRadio requestAudioFocus GAIN");
                initRadioStatus();
                setTunerMode();
            } else {
                mFocusable = false;
                Log.i(TAG, "comin---->lost focus");
                Log.d(TAG, "GeminiRadio requestAudioFocus Failed");
            }
        }
        Log.i(TAG, "onResume end");
    }

    private void notifiHomeStatusChange() {
        if (mCloseTuner || !mFocusable) {
            return;
        }

        Intent intentResponse = new Intent(
                NEU_GEMINI_ACTION_RESPONSE_STATION_INFO);
        Bundle bundle = new Bundle();
        if (Source.FM == mLastSource) {
            bundle.putString("RadioSource", "FM");
        } else {
            bundle.putString("RadioSource", "AM");
        }

        boolean isSeraching = isSeekOn || (isScanOn && !isScanHoldOn) || isASelOn;
        // 通知home的Widget正在搜索电台
        bundle.putBoolean("SerachStation", isSeraching);
        if (!isSeraching) {
            bundle.putString("StationName", mPlayFreq);
        }
        intentResponse.putExtras(bundle);
        sendBroadcast(intentResponse);
    }

    private void notifiHomeCloseTuner() {
        // 通知Home Radio关闭
        Log.i(TAG, "Widget broadcast----> close");
        Intent intent = new Intent(NEU_GEMINI_ACTION_POWER_OFF);
        sendBroadcast(intent);
        return;
    }

    /**
     * brief : get preset frequence
     * <p>
     * detail : Detailed description of the method (Complex method only) [BugID]
     * </p>
     * ProjectID ：NEU_GEMINI
     * 
     * @param
     * @return void
     * @throws
     */
    private void getPresets() {
        Log.i(TAG, "getPresets start");
        IVIRadioPresetStatus presetStatus = mRadio.getPresetStatus(mLastSource);
        if (Source.FM == mLastSource) {

            if (mFmFreqs != null && mFmFreqs.size() > 0) {
                mFmFreqs.clear();
                mFmFreqs.addAll(changeToFMPreset(presetStatus.getmFmFreqStep()));
            } else {
                mFmFreqs = changeToFMPreset(presetStatus.getmFmFreqStep());
            }
            if (mFmPresetAdapter == null) {
                mFmPresetAdapter = new PresetGridViewAdapter(
                        RadioActivity.this, mFmFreqs);
                mFmPresetGridView.setAdapter(mFmPresetAdapter);
                mFmPresetAdapter.setValid(true);
            } else {
                mFmPresetAdapter.notifyDataSetChanged();
            }
            mFmPresetGridView.setVisibility(View.VISIBLE);
            mAmPresetGridView.setVisibility(View.GONE);

        } else if (Source.AM == mLastSource) {

            if (mAmFreqs != null && mAmFreqs.size() > 0) {
                mAmFreqs.clear();
                mAmFreqs.addAll(changeToAMPreset(presetStatus.getmAmFreqStep()));
            } else {
                mAmFreqs = changeToAMPreset(presetStatus.getmAmFreqStep());
            }
            if (mAmPresetAdapter == null) {
                mAmPresetAdapter = new PresetGridViewAdapter(
                        RadioActivity.this, mAmFreqs);
                mAmPresetGridView.setAdapter(mAmPresetAdapter);
                mAmPresetAdapter.setValid(true);
            } else {
                mAmPresetAdapter.notifyDataSetChanged();
            }
            mFmPresetGridView.setVisibility(View.GONE);
            mAmPresetGridView.setVisibility(View.VISIBLE);
        }
        if (!mFocusable) {
            if (Source.FM == mLastSource) {
                mFmPresetAdapter.setValid(false);
                mSwitchBtn
                        .setBackgroundResource(R.drawable.neu_gemini_fm_unclickable);
                mSwitchBtn.setClickable(false);
            } else if (Source.AM == mLastSource) {
                mAmPresetAdapter.setValid(false);
                mSwitchBtn
                        .setBackgroundResource(R.drawable.neu_gemini_am_unclickable);
                mSwitchBtn.setClickable(false);
            }
        }
        Log.i(TAG, "getPresets end");
    }

    /**
     * brief : call the preset frequence by number
     * <p>
     * detail : Detailed description of the method (Complex method only) [BugID]
     * </p>
     * ProjectID ：NEU_GEMINI
     * 
     * @param @param num
     * @return void
     * @throws
     */
    public void presetCall(int num) {
        resetScanBtn();
        Log.e(TAG, "Preset call");
        mRadio.callPreset(num);
    }

    /**
     * brief : save the frequence by number
     * <p>
     * detail : Detailed description of the method (Complex method only) [BugID]
     * </p>
     * ProjectID ：NEU_GEMINI
     * 
     * @param @param num
     * @return void
     * @throws
     */
    public void presetSave(int num) {
        if (Source.FM == mLastSource) {
            mSaveFMPresetNum = num - 1;
        } else if (Source.AM == mLastSource) {
            mSaveAMPresetNum = num - 1;
        }
        Log.e(TAG, "Preset write");
        mRadio.savePreset(num);
    }

    /**
     * brief : show popupwindow
     * <p>
     * detail : Detailed description of the method (Complex method only) [BugID]
     * </p>
     * ProjectID ：NEU_GEMINI
     * 
     * @param
     * @return void
     * @throws
     */
    private void initDialog() {
        mDialogView = getLayoutInflater().inflate(R.layout.neu_gemini_dialog,
                null);
        mDialogView.getBackground().setAlpha(150);
        mPopupWindow = new PopupWindow(mDialogView, LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        final View dialogWindow = mDialogView.findViewById(R.id.dialog_view);
        mDialogView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int x = (int) event.getX();
                int y = (int) event.getY();
                Rect rect = new Rect();
                dialogWindow.getGlobalVisibleRect(rect);
                if (!rect.contains(x, y)) {
                    mPopupWindow.dismiss();
                }

                return false;
            }
        });

        Button submit = (Button) mDialogView.findViewById(R.id.cancel);
        submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mPopupWindow.dismiss();
            }
        });
        submit.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Button submit = (Button) v.findViewById(R.id.cancel);
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    submit.setTextColor(getResources().getColor(
                            R.color.common_font_blue));
                    break;
                case MotionEvent.ACTION_UP:
                    submit.setTextColor(getResources().getColor(
                            R.color.common_white));
                    break;
                }
                return false;
            }
        });

        mSearch = (Button) mDialogView.findViewById(R.id.search);
        mSearch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "PresetAdapter.removePresetRunable()  + 1843");
                if (mAmPresetAdapter != null) {
                    Log.d(TAG, "mAmPresetAdapter.removePresetRunable()  + 1845");
                    mAmPresetAdapter.removePresetRunable();
                } 
                if (mFmPresetAdapter != null) {
                    Log.d(TAG, "mFmPresetAdapter.removePresetRunable()  + 1849");
                    mFmPresetAdapter.removePresetRunable();
                } 
                mPopupWindow.dismiss();
                if (!mFocusable) {
                    return;
                }
                if (mCurrentStatus == Status.REFRESHING_STATION_LIST) {
                    Log.e(TAG, "refreshStationList stop");
                    mRadio.refreshStationList(Action.STOP, Source.IGNORE);
                    Log.e(TAG, "refreshStationList start");
                    mRadio.refreshStationList(Action.START, mLastSource);
                } else {
                    Log.e(TAG, "refreshStationList start");
                    mRadio.refreshStationList(Action.START, mLastSource);
                }
                isASelOn = true;
                resetScanBtn();
                // 搜索中 Scan、manual seek 、auto seek 、FM/AM切换、preset、A.sel无效显示
                if (Source.FM == mLastSource) {
                    mFmPresetAdapter.setValid(false);
                    mSwitchBtn
                            .setBackgroundResource(R.drawable.neu_gemini_fm_unclickable);
                    mSwitchBtn.setClickable(false);
                } else if (Source.AM == mLastSource) {
                    mAmPresetAdapter.setValid(false);
                    mSwitchBtn
                            .setBackgroundResource(R.drawable.neu_gemini_am_unclickable);
                    mSwitchBtn.setClickable(false);
                }
                mAselBtn.setClickable(false);
                mAselBtn.setBackgroundResource(R.drawable.neu_gemini_asel_unclickable);
                setBtnClickable(false);
                // 频率条不可拖动
                mFreqSeekBar.setEnabled(false);
            }
        });
        mSearch.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Button search = (Button) v.findViewById(R.id.search);
                switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    search.setTextColor(getResources().getColor(
                            R.color.common_font_blue));
                    break;
                case MotionEvent.ACTION_UP:
                    search.setTextColor(getResources().getColor(
                            R.color.common_white));
                    break;
                }
                return false;
            }
        });
        Log.i(TAG, "initDialog end");
    }

    /**
     * brief : change the button status
     * <p>
     * detail : Detailed description of the method (Complex method only) [BugID]
     * </p>
     * ProjectID ：NEU_GEMINI
     * 
     * @param flag
     */
    private void setBtnClickable(boolean flag) {
        if (flag) {
            mManualNext
                    .setBackgroundResource(R.drawable.neu_gemini_manual_btn_next);
            mManualNext.setClickable(true);
            mManualPrevious
                    .setBackgroundResource(R.drawable.neu_gemini_manual_btn_previous);
            mManualPrevious.setClickable(true);
            mAutoNext.setBackgroundResource(R.drawable.neu_gemini_auto_btn_next);
            mAutoNext.setClickable(true);
            mAutoPrevious
                    .setBackgroundResource(R.drawable.neu_gemini_auto_btn_previous);
            mAutoPrevious.setClickable(true);
            mScanBtn.setBackgroundResource(R.drawable.neu_gemini_scan_btn);
            mScanBtn.setClickable(true);
        } else {
            mManualNext
                    .setBackgroundResource(R.drawable.neu_gemini_manual_next_unclickable);
            mManualNext.setClickable(false);
            mManualPrevious
                    .setBackgroundResource(R.drawable.neu_gemini_manuala_previous_unclickable);
            mManualPrevious.setClickable(false);
            mAutoNext
                    .setBackgroundResource(R.drawable.neu_gemini_auto_next_unclickable);
            mAutoNext.setClickable(false);
            mAutoPrevious
                    .setBackgroundResource(R.drawable.neu_gemini_auto_previous_unclickable);
            mAutoPrevious.setClickable(false);
            mScanBtn.setBackgroundResource(R.drawable.neu_gemini_scan_unclickable);
            mScanBtn.setClickable(false);
        }
    }

    /**
     * brief : change the fm step to fm freq
     * <p>
     * detail : Detailed description of the method (Complex method only) [BugID]
     * </p>
     * ProjectID ：NEU_GEMINI
     * 
     * @param mFMStep
     * @param @return
     * @return List<String>
     * @throws
     */
    private List<String> changeToFMPreset(int[] mFMStep) {
        List<String> fmFreqs = new ArrayList<String>();
        if (mFMStep != null && mFMStep.length > 0) {
            for (int i = 0; i < mFMStep.length; i++) {
                fmFreqs.add(makeFmFrequency(mFMStep[i]));
            }
        }
        return fmFreqs;
    }

    /**
     * brief : change the am step to am freq
     * <p>
     * detail : Detailed description of the method (Complex method only) [BugID]
     * </p>
     * ProjectID ：NEU_GEMINI
     * 
     * @param mAMStep
     * @return List<String>
     * @throws
     */
    private List<String> changeToAMPreset(int[] mAMStep) {
        List<String> amFreqs = new ArrayList<String>();
        if (mAMStep != null && mAMStep.length > 0) {
            for (int i = 0; i < mAMStep.length; i++) {
                amFreqs.add(makeAmFrequency(mAMStep[i]));
            }
        }
        return amFreqs;
    }

    private void changePresetItemBg(IVIRadioStatus status) {
        if (Source.FM == mLastSource) {
            mFmPresetAdapter.changeItemBg(status.mPresetNumber - 1);
        } else {
            mAmPresetAdapter.changeItemBg(status.mPresetNumber - 1);
        }
    }

    private void seekUp() {

        if (mCurrentStatus == Status.SEEK_UP) {
            Log.d(TAG,
                    "start=> mRadio.seek(Action.STOP, Direction.STOP, 0x00);");
            mRadio.seek(Action.STOP, Direction.STOP, 0x00);
            Log.d(TAG,
                    "end  => mRadio.seek(Action.STOP, Direction.STOP, 0x00);");
        } else {
            resetScanBtn();
            Log.d(TAG, "start=> mRadio.seek(Action.START, Direction.UP, 0xff);");
            mRadio.seek(Action.START, Direction.UP, 0xff);
            Log.d(TAG, "end  => mRadio.seek(Action.START, Direction.UP, 0xff);");
        }
    }

    private void seekDown() {
        if (mCurrentStatus == Status.SEEK_DOWN) {
            Log.d(TAG,
                    "start=> mRadio.seek(Action.STOP, Direction.STOP, 0x00);");
            mRadio.seek(Action.STOP, Direction.STOP, 0x00);
            Log.d(TAG,
                    "end  => mRadio.seek(Action.STOP, Direction.STOP, 0x00);");
        } else {
            resetScanBtn();
            Log.d(TAG,
                    "start=> mRadio.seek(Action.START, Direction.DOWN, 0xff);");
            mRadio.seek(Action.START, Direction.DOWN, 0xff);
            Log.d(TAG,
                    "end  => mRadio.seek(Action.START, Direction.DOWN, 0xff);");
        }
    }

    private void manualPrevious() {

        resetScanBtn();
        Log.d(TAG, "start=> mRadio.manualTuning(Direction.DOWN, 1);");
        Log.e(TAG, "manualTuning down");
        mRadio.manualTuning(Direction.DOWN, 1);
        Log.d(TAG, "end  => mRadio.manualTuning(Direction.DOWN, 1);");

    }

    private void manualNext() {
        resetScanBtn();
        Log.d(TAG, "start=> mRadio.manualTuning(Direction.UP, 1);");
        mRadio.manualTuning(Direction.UP, 1);
        Log.d(TAG, "end  => mRadio.manualTuning(Direction.UP, 1);");
    }

    private void initRadioStatus() {
        resetScanBtn();
        mStereoIcon.setVisibility(View.INVISIBLE);

        if (Source.FM == mLastSource) {
            if (mFmPresetAdapter != null) {
                mFmPresetAdapter.setValid(true);
            }
            mSwitchBtn.setBackgroundResource(R.drawable.neu_gemini_radio_fm);
            mSwitchBtn.setClickable(true);
        } else if (Source.AM == mLastSource) {
            if (mAmPresetAdapter != null) {
                mAmPresetAdapter.setValid(true);
            }
            mSwitchBtn.setBackgroundResource(R.drawable.neu_gemini_radio_am);
            mSwitchBtn.setClickable(true);
        }
        mAselBtn.setClickable(true);
        mAselBtn.setBackgroundResource(R.drawable.neu_gemini_asel_btn);
        setBtnClickable(true);
        mFreqSeekBar.setEnabled(true);
    }

    private void closeTuner(boolean setModeNormal) {
        Log.d(TAG, "start=> mRadio.setSource(Switch.OFF, " + mLastSource + ");");
        mRadio.setSource(Switch.OFF, mLastSource);
        Log.d(TAG, "end  => mRadio.setSource(Switch.OFF, " + mLastSource + ");");

        if (setModeNormal) {
            mAudioManager.setMode(AudioManager.MODE_NORMAL);
        }

        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
        Log.d(TAG, "GeminiRadio abandonAudioFocus");
        SharedPreferences sp = getSharedPreferences(
        		NEU_GEMINI_RADIO_LAST_STATUS, Context.MODE_WORLD_READABLE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("lastSource", mLastSource);
        editor.putString("lastFreq", mPlayFreq);
        editor.commit();
        notifiHomeCloseTuner();

    }

    private int getCurrentPresetNumber() {
        IVIRadioStatus radioSts = mRadio.getRadioStatus(mCurrentBand);
        return radioSts.mPresetNumber;
    }

    private void resetScanBtn() {
        if (isScanOn) {
            Log.e(TAG, "scan stop");
            isScanOn = false;
            isScanHoldOn = false;
            mScanBtn.setBackgroundResource(R.drawable.neu_gemini_scan_btn);
        }
    }

    private void nextPreset() {
        int currentNum = getCurrentPresetNumber();
        int nextNum;
        if ((mLastSource == Source.FM && currentNum == 12)
                || (mLastSource == Source.AM && currentNum == 6)) {
            nextNum = 1;
        } else {
            nextNum = currentNum + 1;
        }

        presetCall(nextNum);
    }

    private void prePreset() {
        int currentNum = getCurrentPresetNumber();
        int preNum;
        if (currentNum == 1 || currentNum == 0) {
            preNum = mLastSource == Source.FM ? 12 : 6;
        } else {
            preNum = currentNum - 1;
        }

        presetCall(preNum);
    }

    private boolean answerVRRequest(boolean isOpen, boolean isChangeBand,
            String radioType, String radioFreq) {
        if (isOpen) {
            mRadio.setSource(Switch.ON, mLastSource);
            return true;
        }
        if (isChangeBand) {
            // VR 已处理
            ((RadioApplicationHelper) getApplicationContext()).setFlag(1);
            if (null == radioType || "".equals(radioType)) {
                Log.e(TAG, "answerVRRequest () ----> radioType is null");
                return false;
            }
            if ("FM".equals(radioType)) {
                mLastSource = Source.FM;
                mCurrentBand = Band.FM1;
            } else if ("AM".equals(radioType)) {
                mLastSource = Source.AM;
                mCurrentBand = Band.AM;
            } else {
                mRecevieRadioFreq = "";
                Log.e(TAG, "该波段不存在");
                return false;
            }

            mRadio.setSource(Switch.ON, mLastSource);
            return true;
        }

        // VR 已处理
        ((RadioApplicationHelper) getApplicationContext()).setFlag(1);

        // 进入指定波段、指定周波数
        mRecevieRadioFreq = radioFreq;

        if (null == radioType || "".equals(radioType)) {
            Log.e(TAG, "answerVRRequest () ----> radioType is null");
            return false;
        }

        if (!RadioUtils.isNumeric(mRecevieRadioFreq)) {
            Log.e(TAG,
                    "answerVRRequest () ----> mRecevieRadioFreq is wrong, mRecevieRadioFreq = "
                            + mRecevieRadioFreq);
            return false;
        }

        Log.d(TAG, "VR enter RadioActivity -- " + radioType + mRecevieRadioFreq);

        int step = 0;
        if ("FM".equals(radioType)) {
            if (87.5 > Double.parseDouble(mRecevieRadioFreq)
                    || 108 < Double.parseDouble(mRecevieRadioFreq)) {
                mRecevieRadioFreq = "";
                Log.e(TAG, "该频道不存在");
                return false;
            }
            mLastSource = Source.FM;
            mCurrentBand = Band.FM1;
            step = (int) (Double.parseDouble(mRecevieRadioFreq) * 10 - 875);
        } else if ("AM".equals(radioType)) {
            if (531 > Double.parseDouble(mRecevieRadioFreq)
                    || 1602 < Double.parseDouble(mRecevieRadioFreq)) {
                mRecevieRadioFreq = "";
                Log.e(TAG, "该频道不存在");
                return false;
            }
            mLastSource = Source.AM;
            mCurrentBand = Band.AM;
            step = (int) ((Double.parseDouble(mRecevieRadioFreq) - 531) / 9);
        } else {
            mRecevieRadioFreq = "";
            Log.e(TAG, "该波段不存在");
            return false;
        }

        mRadio.setSource(Switch.ON, mLastSource, step);
        return true;
    }

    private void setTunerMode() {
        try {
            int mode = mLastSource == Source.AM ? AudioManager.MODE_IN_TUNNER_AM
                    : AudioManager.MODE_IN_TUNNER_FM;
            if (mISourceService != null) {
                mISourceService.setAppSourceMode(AudioManager.STREAM_TUNER,
                        mode);
                Log.d(TAG,
                        "GeminiRadio setAppSourceMode = AudioManager.MODE_IN_TUNNER_FM OK");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
            Log.d(TAG,
                    "GeminiRadio setAppSourceMode = AudioManager.MODE_IN_TUNNER_FM failed");
        }
    }

    private void moveRadioToBack() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        List<RunningTaskInfo> runningInfos = am.getRunningTasks(20);

        for (RunningTaskInfo runningTaskInfo : runningInfos) {
            if (!"com.neusoft.radio".equals(runningTaskInfo.baseActivity.getPackageName())) {
                am.moveTaskToFront(runningTaskInfo.id, 0);
                moveTaskToBack(true);
                return;
            }
        }
        Intent launchIntent = new Intent();
        ComponentName name = new ComponentName("com.neusoft.c3home",
                "com.neusoft.c3home.NewMainActivity");
        launchIntent.setComponent(name);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(launchIntent);
        } catch (Exception e) {
            e.printStackTrace();
            moveTaskToBack(true);
        }

    }
}
