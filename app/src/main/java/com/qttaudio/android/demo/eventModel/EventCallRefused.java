package com.qttaudio.android.demo.eventModel;

/**
 * Created by qingtiantree on 2017/4/27.
 */

public class EventCallRefused {
    String ip; //来源
    public EventCallRefused(String ip){
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

}
