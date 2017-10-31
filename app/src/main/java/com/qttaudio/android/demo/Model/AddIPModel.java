package com.qttaudio.android.demo.Model;

/**
 * Created by qingtiantree on 2017/4/24.
 */

public class AddIPModel {

    private int index;
    private String ipName;
    private String ipAddress;
    private boolean isShowAdd;
    private int localPort;
    private int remotePort;


    public AddIPModel(int index, String ipName, String ipAddress, boolean isShowAdd){

        this.index = index;
        this.ipAddress = ipAddress;
        this.ipName = ipName;
        this.isShowAdd = isShowAdd;
        this.remotePort = -1;
    }


    public int getIndex(){
        return index;
    }
    public String getIpName(){
        return ipName;
    }
    public String getIpAddress(){
        return ipAddress;
    }

    public boolean getIsShowAdd(){
        return isShowAdd;
    }


    public void setIndex(int index){
        this.index = index;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setIpName(String ipName) {
        this.ipName = ipName;
    }
    public void setShowAdd(boolean isShowAdd){
        this.isShowAdd = isShowAdd;
    }

    public void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public int getLocalPort() {
        return localPort;
    }

    public int getRemotePort() {
        return remotePort;
    }


    public boolean isContainOther(){

        if (this.ipAddress.contains("@")){
            this.ipAddress = this.ipAddress.replace("@","");

            System.out.print("message +++ " + this.ipAddress);

            return true;
        }else{
            return false;
        }
    }
}
