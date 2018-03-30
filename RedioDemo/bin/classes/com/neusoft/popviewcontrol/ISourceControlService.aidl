package com.neusoft.popviewcontrol;

interface ISourceControlService{

    void setAppSourceMode(in int streamType, in int mode);
 
    int getAppSourceMode(in int streamType);

    void setStreamVolume(in int streamType,in int index,in int flags);

    void setStreamCanBeMute(in int streamType,in boolean flag);

    void setStreamMute(in int streamType,in boolean flag);

}
