package com.qttaudio.android.demo.eventModel;

/**
 * Created by qingtiantree on 2017/4/27.
 */

public class EventCallRequset {

    String ip; //来源
    short port;   //端口
    public EventCallRequset(String ip ,short port){
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }
    public short getPort() {
        return port;
    }

}
