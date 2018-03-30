package com.neusoft.iviradio;

/**
 * Preset Status.
 * 
 * @author chenh<br/>
 *         refer {@link com.neusoft.c3alfus.iviradio.IVIRadio#getPresetStatus}<br/>
 *         <hr>
 *         History:<br/>
 *         No Name Date Comments<br/>
 *         #0001 chenh 2014-06-05 create<br/>
 */
public class IVIRadioPresetStatus {
    /** @hide */
    public final static int FM_PRESET_MAX = 12;
    /** @hide */
    public final static int AM_PRESET_MAX = 6;

    /**
     * radio preset status result max value. no-use in the current version.
     */
    private int mMaxValue;
    /** radio preset status result FM Preset Frequency Step */
    private int[] mFmFreqStep;
    /** radio preset status result AM Preset Frequency Step */
    private int[] mAmFreqStep;

    public IVIRadioPresetStatus() {
        mFmFreqStep = new int[FM_PRESET_MAX];
        mAmFreqStep = new int[AM_PRESET_MAX];
    }

    /**
     * Set preset status result max value
     * 
     * @param value
     *            The Value
     * 
     * @hide
     */
    public void setMaxValue(int value) {
        mMaxValue = value;
    }

    // add by zhangweixiao in 0611
    /**
     * Get fm preset status
     * 
     * 
     * @hide
     */
    public int[] getmFmFreqStep() {
        return mFmFreqStep;
    }

    // add by zhangweixiao in 0611

    /**
     * Get am preset status
     * 
     * 
     * @hide
     */
    public int[] getmAmFreqStep() {
        return mAmFreqStep;
    }

    /**
     * Set FM Frequency Step of Given Preset Number
     * 
     * @param number
     *            The Given Preset Number. 0x01-0xFF(Define by model)
     * @param step
     *            The Frequency Step
     * 
     * @hide
     */
    public void setFmFreqStep(int number, int step) {
        if (step == 0xff) {
            step = -1;
        }
        if ((0 < number) && (number <= FM_PRESET_MAX)) {
            mFmFreqStep[number - 1] = step;
        }
    }

    /**
     * Set AM Frequency Step of Given Preset Number
     * 
     * @param number
     *            The Given Preset Number. 0x01-0xFF(Define by model)
     * @param step
     *            The Frequency Step
     * 
     * @hide
     */
    public void setAmFreqStep(int number, int step) {
        if ((0 < number) && (number <= AM_PRESET_MAX)) {
            mAmFreqStep[number - 1] = step;
        }
    }
}
