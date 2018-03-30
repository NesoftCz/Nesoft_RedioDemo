package com.neusoft.iviradio;

import android.util.Log;

/**
 * This class provides the basic radio interface.<br/>
 * 
 * @author chenh<br/>
 *         <hr>
 *         History:<br/>
 *         No Name Date Comments<br/>
 *         #0001 chenh 2014-05-30 create<br/>
 *         #0002 chenh 2014-06-05 modified : the result type of
 *         {@link #getPresetStatus(int)}.<br/>
 */
public class IVIRadio {
    private static final String TAG = "IVIRadio";

    /** @hide */
    public static final byte RPC_CONTROL = 0x00;

    private static final short RPC_RADIO_SOURCE = 0x0100;
    private static final short RPC_RADIO_MANUAL_TUN = 0x0101;
    private static final short RPC_RADIO_SEEK = 0x0102;
    private static final short RPC_RADIO_MANUAL_MEMORY = 0x0105;
    private static final short RPC_RADIO_PRESET_CALL = 0x0106;
    private static final short RPC_RADIO_REFRESH_STATION_LIST = 0x0120;

    private static final short RPC_RADIO_SCAN = 0x010D;
    private static final short RPC_RADIO_BANDCHG = 0x010E;
    private static final short RPC_RADIO_GET_STATUS = 0x0112;
    private static final short RPC_RADIO_GET_PRESET_STS = 0x0113;
    
    private static final int SET_SOURCE_WITHOUT_FREQ = 0xFF;

    /** @hide */
    public IVIRadioRpc mRpc = null;
    /** @hide */
    public OnActionCompleteListener mActionCompleteListener = null;

    public IVIRadio() {
        mRpc = new IVIRadioRpc();
        mRpc.attachCommon(this);
    }

    /**
     * The constant values of sources.
     */
    public static class Source {
        /** Source FM. */
        public static final int FM = 1;
        /** Source AM. */
        public static final int AM = 2;
        /** Both FM and AM. */
        public static final int BOTH = 3;
        /** Sometimes we don't care about some of the parameters. */
        public static final int IGNORE = 0;
    }

    /**
     * The constant values of switch's type.
     */
    public static class Switch {
        /** ON. */
        public static final int ON = 0;
        /** OFF. */
        public static final int OFF = 1;
    }

    public boolean setSource(int status, int source) {
        return setSource(status, source, SET_SOURCE_WITHOUT_FREQ);
    }
    /**
     * Tell the radio chip which source would be used.
     * 
     * @param status
     *            Use {@link Switch#ON}, {@link Switch#OFF} of {@link Switch}.
     * @param source
     *            Use {@link Source#FM}, {@link Source#AM} of {@link Source}.
     * @return Current process result.
     */
    public boolean setSource(int status, int source, int step) {
        boolean result = false;
        byte[] dataIn = new byte[mRpc.getRpc().DATALEN];

        Log.d(TAG, "setSource(" + status + ", " + source + "), step = " +  step);

        if ((Switch.ON != status) && (Switch.OFF != status)) {
            Log.e(TAG, "radio source sts set wrong");
        } else if ((Source.FM != source) && (Source.AM != source)) {
            Log.e(TAG, "radio source set wrong");
        } else {
            for (int i = 0; i < dataIn.length; i++) {
                dataIn[i] = 0;
            }

            dataIn[0] = (byte) status;
            dataIn[1] = (byte) source;
            dataIn[2] = (byte) step;
            if (1 == mRpc.getRpc().sendCmd(RPC_CONTROL, RPC_RADIO_SOURCE,
                    dataIn)) {
                Log.e(TAG, "send interface report NG!");
            } else {
                result = true;
            }
        }
        return result;
    }

    /**
     * The constant values of directions.
     */
    public static class Direction {
        /** Action direction up. */
        public static final int UP = 0;
        /** Action direction down. */
        public static final int DOWN = 1;
        /** Action direction stop. */
        public static final int STOP = 0xff;
        /** Action direction direct. */
        public static final int DIRECT = 2;
        /** Action direction continuous up. */
        public static final int CONTINUOUS_UP = 3;
        /** Action direction continuous down */
        public static final int CONTINUOUS_DOWN = 4;
        /** Action direction stop continuous up/down. */
        public static final int STOP_CONTINUOUS = 5;
    }

    /**
     * Change the radio station by the given parameters, whatever the target
     * station was valid or not.
     * 
     * @param direct
     *            Manual tuning direction. Use {@link Direction#UP},
     *            {@link Direction#DOWN}, {@link Direction#DIRECT},
     *            {@link Direction#CONTINUOUS_UP},
     *            {@link Direction#CONTINUOUS_DOWN},
     *            {@link Direction#STOP_CONTINUOUS} of {@link Direction}.
     *            </br></br> If the parameter is {@link Direction#UP}/
     *            {@link Direction#DOWN}, radio just change one step on the
     *            given direction.</br> If the param is
     *            {@link Direction#CONTINUOUS_UP}/
     *            {@link Direction#CONTINUOUS_DOWN}, the radio will change the
     *            step one by one in each 500ms(based on the radio chip) until
     *            get {@link Direction#STOP_CONTINUOUS}.</br> If the param is
     *            {@link Direction#DIRECT}, the radio will change to the given
     *            step by parameter step directly.
     * @param step
     *            The step tuning to . Useful only if the direct is
     *            {@link Direction#DIRECT}. other status set it to 0xff.
     * @return Current process result.
     */
    public boolean manualTuning(int direct, int step) {
        boolean result = false;
        byte[] dataIn = new byte[mRpc.getRpc().DATALEN];

        Log.d(TAG, "manualTuning(" + direct + ", " + step + ")");

        if ((Direction.UP > direct) || (Direction.STOP_CONTINUOUS < direct)) {
            Log.e(TAG, "manual tuning direction wrong!");
        } else {
            for (int i = 0; i < dataIn.length; i++) {
                dataIn[i] = 0;
            }

            dataIn[0] = (byte) direct;
            dataIn[1] = (byte) step;
            if (1 == mRpc.getRpc().sendCmd(RPC_CONTROL, RPC_RADIO_MANUAL_TUN,
                    dataIn)) {
                Log.e(TAG, "send interface report NG!");
            } else {
                result = true;
            }
        }
        return result;
    }

    /**
     * The constant values of actions.
     */
    public static class Action {
        /** Seek action start. */
        public static final int START = 0;
        /**
         * Means the radio will stop at the current station, whatever the
         * current station is valid or not.
         */
        public static final int STOP = 1;
        /**
         * Means the radio will be back to the station before the action
         * started.
         */
        public static final int CANCEL = 2;
    }

    /**
     * Change the radio to the next valid station by the given parameters.
     * Sometimes named auto seek.
     * 
     * @param mode
     *            Seek mode. Use {@link Action#START}, {@link Action#STOP},
     *            {@link Action#CANCEL} of {@link Action}.
     * @param direct
     *            Seek direction. Use {@link Direction#UP},
     *            {@link Direction#DOWN}, {@link Direction#STOP} of
     *            {@link Direction}.
     * @param cycle
     *            Seek action's cycle before finished.<br/>
     *            set to 0x00 if the mode is Action#STOP or Action#CANCEL.<br/>
     *            set to 0x01-0xfe if the mode is Action#START and do not want
     *            to seek endless.<br/>
     *            set to 0xff if want to seek endless when there was no valid
     *            stations be found.
     * @return Current process result.
     */
    public boolean seek(int mode, int direct, int cycle) {
        boolean result = false;
        byte[] dataIn = new byte[mRpc.getRpc().DATALEN];

        Log.d(TAG, "seek(" + mode + ", " + direct + ", " + cycle + ")");

        if ((Action.START != mode) && (Action.STOP != mode)
                && (Action.CANCEL != mode)) {
            Log.e(TAG, "seek mode wrong!");
        } else if ((Direction.UP != direct) && (Direction.DOWN != direct)
                && (Direction.STOP != direct)) {
            Log.e(TAG, "seek direction wrong!");
        } else {

            for (int i = 0; i < dataIn.length; i++) {
                dataIn[i] = 0;
            }

            dataIn[0] = (byte) mode;
            dataIn[1] = (byte) direct;
            dataIn[2] = (byte) cycle;
            if (1 == mRpc.getRpc().sendCmd(RPC_CONTROL, RPC_RADIO_SEEK, dataIn)) {
                Log.e(TAG, "send interface report NG!");
            } else {
                result = true;
            }
        }

        return result;
    }

    /**
     * Save the current frequency to the given preset slot.
     * 
     * @param number
     *            Preset number to save to. 0x01-0xFF(Define by model)
     * @return Current process result.
     */
    public boolean savePreset(int number) {
        boolean result = false;

        Log.d(TAG, "savePreset(" + number + ")");
        byte[] dataIn = new byte[mRpc.getRpc().DATALEN];

        for (int i = 0; i < dataIn.length; i++) {
            dataIn[i] = 0;
        }

        dataIn[0] = (byte) number;

        if (1 == mRpc.getRpc().sendCmd(RPC_CONTROL, RPC_RADIO_MANUAL_MEMORY,
                dataIn)) {
            Log.e(TAG, "send interface report NG!");
        } else {
            result = true;
        }
        return result;
    }

    /**
     * Change the radio to the station saved in the given preset slot.
     * 
     * @param number
     *            Preset number to call from. 0x01-0xFF(Define by model)
     * @return Current process result.
     */
    public boolean callPreset(int number) {
        boolean result = false;
        Log.d(TAG, "callPreset(" + number + ")");

        byte[] dataIn = new byte[mRpc.getRpc().DATALEN];

        for (int i = 0; i < dataIn.length; i++) {
            dataIn[i] = 0;
        }

        dataIn[0] = (byte) number;

        if (1 == mRpc.getRpc().sendCmd(RPC_CONTROL, RPC_RADIO_PRESET_CALL,
                dataIn)) {
            Log.e(TAG, "send interface report NG!");
        } else {
            result = true;
        }

        return result;
    }

    /**
     * The radio will traversal all the valid station in the current band and
     * play each of them for 10 seconds.
     * 
     * @param mode
     *            Scan mode. Use {@link Action#START}, {@link Action#STOP},
     *            {@link Action#CANCEL} of {@link Action}.
     * @param cycle
     *            Scan 's cycle before finished. <br/>
     *            set to 0x00 if the mode is {@link Action#STOP} or
     *            {@link Action#CANCEL}.<br/>
     *            set to 0x01-0xfe if the mode is {@link Action#START} and do
     *            not want to seek endless.<br/>
     *            set to 0xff if want to seek endless when there was no valid
     *            stations be found.
     * @return Current process result.
     */
    public boolean scan(int mode, int cycle) {
        boolean result = false;
        Log.d(TAG, "scan(" + mode + ", " + cycle + ")");
        byte[] dataIn = new byte[mRpc.getRpc().DATALEN];

        if ((Action.START > mode) || (Action.CANCEL < mode)) {
            Log.e(TAG, "scan mode wrong!");
        } else {
            for (int i = 0; i < dataIn.length; i++) {
                dataIn[i] = 0;
            }

            dataIn[0] = (byte) mode;
            dataIn[1] = (byte) cycle;
            if (1 == mRpc.getRpc().sendCmd(RPC_CONTROL, RPC_RADIO_SCAN, dataIn)) {
                Log.e(TAG, "send interface report NG!");
            } else {
                result = true;
            }
        }
        return result;
    }

    /**
     * The constant values of bands.
     */
    public static class Band {
        /** Band FM1. */
        public static final int FM1 = 0;
        /** Band AM/MW. */
        public static final int AM = 3;
    }

    /**
     * Change band.
     * 
     * @param band
     *            Band. Use {@link Band#FM1}, {@link Band#AM}of
     *            {@link Band}.
     * @return Current process result.
     */
    public boolean changeBand(int band) {
        boolean result = false;
        Log.d(TAG, "changeBand(" + band + ")");
        byte[] dataIn = new byte[mRpc.getRpc().DATALEN];

        if ((Band.FM1 > band) || (Band.AM < band)) {
            Log.e(TAG, "bandchange band wrong!");
        } else {
            for (int i = 0; i < dataIn.length; i++) {
                dataIn[i] = 0;
            }

            dataIn[0] = (byte) band;
            if (1 == mRpc.getRpc().sendCmd(RPC_CONTROL, RPC_RADIO_BANDCHG,
                    dataIn)) {
                Log.e(TAG, "send interface report NG!");
            } else {
                result = true;
            }
        }
        return result;
    }

    /**
     * Get radio status.
     * 
     * @param band
     *            The band get from. Use {@link Band#FM1},{@link Band#AM} of
     *            {@link Band}.
     * @return Radio status.
     */
    public IVIRadioStatus getRadioStatus(int band) {

        IVIRadioStatus status = new IVIRadioStatus();
        byte[] dataIn = new byte[mRpc.getRpc().DATALEN];
        byte[] dataOut = null;

        if ((Band.FM1 > band) || (Band.AM < band)) {
            Log.e(TAG, "radio status band wrong!");
        } else {
            for (int i = 0; i < dataIn.length; i++) {
                dataIn[i] = 0;
            }

            dataIn[0] = (byte) band;
            dataOut = mRpc.getRpc().getInfo(RPC_CONTROL, RPC_RADIO_GET_STATUS,
                    dataIn);
            if (dataOut != null) {
                status.mFreqStep = getInteger(dataOut[0]);
                status.mPresetNumber = getInteger(dataOut[3]);
                status.mStereo = getInteger(dataOut[9]);
                status.mBand = getInteger(dataOut[12]);
            } else {
                Log.e(TAG, "getRadioStatus() dataOut is null!");

            }

        }
        return status;

    }

    /**
     * Get preset status.
     * 
     * @param source
     *            Get the presets' status for the given source. Use
     *            {@link Source#FM}, {@link Source#AM} of {@link Source}.
     * @return Preset status.
     */
    public IVIRadioPresetStatus getPresetStatus(int source) {
        IVIRadioPresetStatus status = new IVIRadioPresetStatus();
        byte[] dataIn = new byte[mRpc.getRpc().DATALEN];
        byte[] dataOut = null;

        if ((Source.FM != source) && (Source.AM != source)) {
            Log.e(TAG, "preset status get band wrong!");
        } else {
            for (int i = 0; i < dataIn.length; i++) {
                dataIn[i] = 0;
            }

            switch (source) {
            case Source.FM:
                dataIn[0] = 0;
                break;
            case Source.AM:
                dataIn[0] = 1;
                break;
            default:
                dataIn[0] = 0;
                break;
            }
            dataOut = mRpc.getRpc().getInfo(RPC_CONTROL,
                    RPC_RADIO_GET_PRESET_STS, dataIn);
            if (dataOut != null) {
                int i, idx = 0;
                status.setMaxValue(dataOut[idx++]);
                for (i = 0; i < IVIRadioPresetStatus.FM_PRESET_MAX; i++) {
                    status.setFmFreqStep(i + 1, getInteger(dataOut[idx++]));
                }
                for (i = 0; i < IVIRadioPresetStatus.AM_PRESET_MAX; i++) {
                    status.setAmFreqStep(i + 1, getInteger(dataOut[idx++]));
                }
            } else {
                Log.e(TAG, "getPresetStatus() dataOut is null!");
            }

        }
        return status;
    }

    /**
     * The radio will make a full list with all the valid station of the given
     * band(s). The old list would be replaced.
     * 
     * @param mode
     *            Seek mode. Use {@link Action#START}, {@link Action#STOP} of
     *            {@link Action}.
     * @param band
     *            Band. Use {@link Source#IGNORE}, {@link Source#FM},
     *            {@link Source#AM}, {@link Source#BOTH} of {@link Source}.
     * @return Current process result.
     */
    public boolean refreshStationList(int mode, int band) {
        boolean result = false;

        Log.d(TAG, "refreshStationList(" + mode + ", " + band + ")");
        byte[] dataIn = new byte[mRpc.getRpc().DATALEN];

        if ((Action.STOP != mode) && (Action.START != mode)) {
            Log.e(TAG, "auto memory mode wrong!");
        } else if ((Source.IGNORE != band) && (Source.FM != band)
                && (Source.AM != band) && (Source.BOTH != band)) {
            Log.e(TAG, "seek direction wrong!");
        } else {
            for (int i = 0; i < dataIn.length; i++) {
                dataIn[i] = 0;
            }

            switch (mode) {
            case Action.START:
                dataIn[0] = 1;
                break;
            case Action.STOP:
                dataIn[0] = 0;
                break;
            default:
                dataIn[0] = 1;
                break;
            }
            switch (band) {
            case Source.IGNORE:
                dataIn[1] = 0;
                break;
            case Source.FM:
                dataIn[1] = 1;
                break;
            case Source.AM:
                dataIn[1] = 2;
                break;
            case Source.BOTH:
                dataIn[1] = 3;
                break;
            default:
                dataIn[1] = 1;
                break;
            }
            if (1 == mRpc.getRpc().sendCmd(RPC_CONTROL,
                    RPC_RADIO_REFRESH_STATION_LIST, dataIn)) {
                Log.e(TAG, "send interface report NG!");
            } else {
                result = true;
            }
        }
        return result;
    }

    /**
     * The constant values of actions' type.
     */
    public static class ActionType {
        /** {@link #setSource} completed. */
        public static final int SET_SOURCE = 0x00;
        /** {@link #manualTuning} started. */
        public static final int MANUAL_REPEAT_START = 0x01;
        /** {@link #seek} started. */
        public static final int SEEK_START = 0x02;
        /** {@link #refreshStationList} started. */
        public static final int REFRESHING_STATION_LIST_START = 0x03;
        /** {@link #deletePreset} completed. */
        public static final int PRESET_DELETE = 0x04;
        /** {@link #savePreset} completed. */
        public static final int PRESET_MEMORY = 0x05;
        /** {@link #callPreset} completed. */
        public static final int PRESET_CALL = 0x06;
        /** {@link com.neusoft.iviradio.IVIRadioAlignment} completed. */
        public static final int AUTO_ALIGNMENT = 0x07;
        /** {@link #changeSeekMode} completed. */
        public static final int CHANGE_SEEK_MODE = 0x08;
        /** {@link #scan} completed. */
        public static final int SCAN = 0x09;
        /** {@link #changeBand} completed. */
        public static final int BAND_CHANGE = 0x0A;
        /** {@link #changeArea} completed. */
        public static final int AREA_CHANGE = 0x0B;
        /** {@link #setBandFrequency} completed. */
        public static final int BAND_FREQ_SET = 0x0C;
        /** {@link #changeSource} completed. @hide */
        public static final int SOURCE_CHANGING = 0x0D;
        /** {@link #manualTuning} completed. */
        public static final int MANUAL_TUNING_FINISH = 0x0E;
        /** {@link #seek} stopped. */
        public static final int SEEK_STOP = 0x0F;
        /** {@link #refreshStationList} stopped. */
        public static final int REFRESHING_STATION_LIST_STOP = 0x10;
        /** {@link #refreshStationList} completed. */
        public static final int REFRESHING_STATION_LIST_FINISH = 0x11;
        /** {@link #seek} cancelled. */
        public static final int SEEK_CANCEL = 0x12;
        /**
         * {@link #refreshStationList} cancelled.
         */
        public static final int REFRESHING_STATION_LIST_CANCEL = 0x13;
        /**
         * Frequency changing completed during {@link #seek}
         */
        public static final int FREQUENCY_CHANGE = 0x14;
        /** Preset number changing completed . */
        public static final int PRESETNO_CHANGE = 0x15;

        /**
         * {@link #refreshStationList} found a new valid frequency.
         * 
         * @hide
         */
        public static final int REFRESHING_STATION_LIST_REFRESH = 0x16;

        /** {@link #setSource} source on finish notify. */
        public static final int SOURCE_ON_FINISH = 0x17;

        /** {@link #scanStart} scan on start notify. */
        // TODO
        public static final int SCAN_START = 0x18;

        /** {@link #scanStop} scan on finish notify. */
        // TODO
        public static final int SCAN_STOP = 0x19;
        public static final int INF_STEREO = 0x1A;
        public static final int SCAN_HOLD_START = 0x1B;
        public static final int SCAN_HOLD_STOP = 0x1C;
    }

    /**
     * Interface definition of a callback to be invoked when radio finished an
     * action
     */
    public interface OnActionCompleteListener {
        /**
         * Called to indicate the end of the radio action.
         * 
         * @param action
         *            the action completed. refer : {@link ActionType}.
         * @param result
         *            0 means success , 1 means fail
         */
        void OnActionComplete(int action, int result);
    }

    /**
     * Add the action result listener.
     * 
     * @param listener
     *            function
     */
    public void setActionCompleteListener(OnActionCompleteListener listener) {
        mActionCompleteListener = listener;
    }

    /**
     * Returns a string containing a concise, human-readable description of this
     * object.
     * 
     * @return a printable representation of this object.
     */
    public String toString() {
        return getClass().getSimpleName();
    }

    /** @hide */
    static public int getInteger(byte b1, byte b2, byte b3, byte b4) {
        // TODO judge which one is right
        // return b1 * 0x01000000 + b2 * 0x010000 + b3 * 0x0100 + b4;
        return getInteger(b4) * 0x01000000 + getInteger(b3) * 0x010000
                + getInteger(b2) * 0x0100 + getInteger(b1);
    }

    /** @hide */
    static public int getInteger(byte b1, byte b2) {
        // TODO judge which one is right
        // return b1 * 0x0100 + b2;
        return getInteger(b2) * 0x0100 + getInteger(b1);
    }

    /** @hide */
    static public int getInteger(byte b) {
        if (b < 0) {
            return 0xff + b + 1;
        } else {
            return b;
        }
    }
}
