package com.neusoft.radio.recevier;

import com.neusoft.radio.RadioActivity;
import com.neusoft.radio.RadioApplicationHelper;
import com.neusoft.radio.RadioUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class VRRadioReceiver extends BroadcastReceiver {

    private final static String TAG = "VRRadioReceiver";
    private final static String ACTION_VR_INTENT = "com.vrmms.intent.RADIO";
    private final static String OPERATE = "operate";
    private final static String FREQUENCY = "radio_frequency";
    private final static String BAND = "radio_band";
    private final static String OPEN = "app_open";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String operate = intent.getStringExtra(OPERATE);
        boolean isChangeBand = false;
        boolean isBadFreq = false;
        if (action.equals(ACTION_VR_INTENT)) {
            RadioUtils.setCurrentMediaSource(context);
            String radioType;
            String radioName;
            long timer = intent.getLongExtra("timer", System.currentTimeMillis());
            if (operate.equals(OPEN)) {
                RadioUtils.responseVR(context, true, timer);

                Intent openActivity = new Intent(context, RadioActivity.class);
                openActivity.putExtra("open", 1);
                openActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(openActivity);
                return;
            } else if (operate.equals(BAND)) {
                radioType = intent.getStringExtra("radio-type");
                radioName = "999999";
                isChangeBand = true;

            } else if (operate.equals(FREQUENCY)) {
                radioType = intent.getStringExtra("radio-type");
                radioName = intent.getStringExtra("radio-freq");
            } else {
                // operate 错误
                RadioUtils.responseVR(context, false, timer);
                return;
            }

            if (!RadioUtils.isBandAndFreqAvail(radioType, radioName)) {
//                RadioUtils.responseVR(context, false, timer);
                isBadFreq = true;
                radioType = "";
                radioName = "";
            } else {
                RadioUtils.responseVR(context, true, timer);
            }

            Log.i(TAG, "VR enter ----> radioType = " + radioType + ";radioName = " + radioName);
            ((RadioApplicationHelper) context.getApplicationContext())
                    .setRadioType(radioType);
            ((RadioApplicationHelper) context.getApplicationContext())
                    .setRadioName(radioName);
            ((RadioApplicationHelper) context.getApplicationContext())
                    .setTimer(timer);
            ((RadioApplicationHelper) context.getApplicationContext())
                    .setFlag(intent.getIntExtra("flag", 0));
            ((RadioApplicationHelper) context.getApplicationContext())
                    .setIsBadFreq(isBadFreq);

            Intent intent1 = new Intent(context, RadioActivity.class);
            intent1.putExtra("radio-type", radioType);
            intent1.putExtra("radio-freq", radioName);
            intent1.putExtra("timer", timer);
            intent1.putExtra("change-band", isChangeBand);
            intent1.putExtra("flag", intent.getIntExtra("flag", 0));
            intent1.putExtra("bad-freq", isBadFreq);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
    }

}
