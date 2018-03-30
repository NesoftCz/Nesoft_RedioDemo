package com.neusoft.iviradio;

/**
 * Radio Status. refer
 * {@link com.neusoft.c3alfus.iviradio.IVIRadio#getRadioStatus}
 * 
 * @author chenh<br/>
 *         <hr>
 *         History:<br/>
 *         No Name Date Comments<br/>
 *         #0001 chenh 2014-05-30 create<br/>
 */
public class IVIRadioStatus {
    /** frequency step @hide */
    public int mFreqStep;
    /** preset number @hide */
    public int mPresetNumber;
    /** Stereo state @hide */
    public int mStereo;
    /** Band @hide */
    public int mBand;

    /**
     * Get the frequency step of the current listening station. </br> The
     * generating conversion formula from a step to a frequency is as
     * below:</br> FM : ""+ (step + 875.0) / 10.0 + "MHz"</br> AM : "" + (step *
     * 9 + 531) + "kHz"</br>
     * 
     * @return frequency step. <br/>
     *         0x00~0xFF(Defined by model).
     */
    public int getFrequencyStep() {
        return mFreqStep;
    }

    /**
     * If the current station using stereo state to broadcast.
     * 
     * @return Stereo state. <br/>
     *         0x00 means Off, 0x01 means On
     */
    public int getStereo() {
        return mStereo;
    }

    /**
     * Get current band.
     * 
     * @return Band. <br/>
     *         refer {@link IVIRadio.Band#FM1}, {@link IVIRadio.Band#AM}.
     */
    public int getBand() {
        return mBand;
    }

    /**
     * The constant values of radio Status.
     */
    public static class Status {
        /** normal receiving */
        public static final int NORMAL = 0;
        /** seeking up */
        public static final int SEEK_UP = 1;
        /** seeking down */
        public static final int SEEK_DOWN = 2;
        /** manual tuning up */
        public static final int TUNING_UP = 3;
        /** manual tuning down */
        public static final int TUNING_DOWN = 4;
        /** scanning in preset list */
        public static final int PRESET_SCAN = 5;
        /** scanning in whole band range */
        public static final int SEEK_SCAN = 6;
        /** playing the radio before jump to the next step during scan */
        public static final int SCAN_AUDITION = 7;
        /** auto memory @hide */
        public static final int AUTO_MEMORY = 8;
        /** station list making */
        public static final int REFRESHING_STATION_LIST = 8;
        /** error status */
        public static final int ERROR = 9;
    }

}
