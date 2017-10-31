package com.qttaudio.android.demo.eventModel;

/**
 * Created by qingtiantree on 2017/4/27.
 */

public class EventCallResult {
    boolean result; //信令
    String ip; //来源
    int port; //rtp端口
    public EventCallResult(boolean result, String ip,int port){
        this.result = result;
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public boolean isResult() {
        return result;
    }

    public int getPort() {
        return port;
    }
}
