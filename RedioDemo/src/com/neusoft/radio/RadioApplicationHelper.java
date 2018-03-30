package com.neusoft.radio;

import android.app.Application;


public class RadioApplicationHelper extends Application {

    private String radioType;
    private String radioName;
    private long timer;
    // flag表示是否被处理0-->没处理；1-->已处理
    private int flag = 1;
    private boolean isBadFreq = false;

    public String getRadioType() {
        return radioType;
    }

    public void setRadioType(String radioType) {
        this.radioType = radioType;
    }

    public String getRadioName() {
        return radioName;
    }

    public void setRadioName(String radioName) {
        this.radioName = radioName;
    }

    public long getTimer() {
        return timer;
    }

    public void setTimer(long timer) {
        this.timer = timer;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public boolean getIsBadFreq() {
        return isBadFreq;
    }

    public void setIsBadFreq(boolean isBad) {
        this.isBadFreq = isBad;
    }

}
