package com.neusoft.iviradio;

import android.util.Log;

import com.neusoft.c3alfus.rpc.NativeRpc;
import com.neusoft.c3alfus.rpc.NativeRpc.OnRpcListener;
import com.neusoft.iviradio.IVIRadio.Source;

/**
 * @hide
 */
public class IVIRadioRpc {
    private static final String TAG = "IVIRadio";

    /** @hide */
    public static final String RPC_MODULE_NAME = "RADIO";

    private static final int RPC_CBACK_RADIO_ACT_FINISH = 0x011C;

    private NativeRpc mRpc = null;
    private IVIRadio mRadio = null;

    public IVIRadioRpc() {
        mRpc = NativeRpc.getInstance();
        mRpc.registerListener(RPC_MODULE_NAME, rpcListener);
    }

    public void attachCommon(IVIRadio radio) {
        mRadio = radio;
    }

    public NativeRpc getRpc() {
        return mRpc;
    }

    private OnRpcListener rpcListener = new OnRpcListener() {
        @Override
        public void onCallback(byte length, byte control, short opecode, byte[] dataOut) {
            Log.d(TAG, "opecode=0x" + Integer.toString(opecode, 16));
            if (mRadio != null) {
                if (opecode == RPC_CBACK_RADIO_ACT_FINISH) {
                    Log.d(TAG, "OnActionComplete : data[0]=0x" + Integer.toString(dataOut[0], 16) + " data[1]=" + dataOut[1]);
                    if (dataOut[0] == IVIRadio.ActionType.REFRESHING_STATION_LIST_REFRESH) {
                        Log.e("refresh_list", "callback----ActionType == REFRESHING_STATION_LIST_REFRESH");
                    } else {
                        Log.e("refresh_list", "callback----ActionType != REFRESHING_STATION_LIST_REFRESH");
                        Log.e("refresh_list", "callback----dataOut[0]" +dataOut[0]);
                        Log.e("refresh_list", "callback----dataOut[1]" +dataOut[1]);
                        mRadio.mActionCompleteListener.OnActionComplete(dataOut[0], dataOut[1]);
                    }
                }
            }
        }
    };

}
