package com.qttaudio.android.demo.tool;



import android.os.Message;

import com.qttaudio.android.demo.Model.WeightObject;
import com.qttaudio.android.demo.eventModel.EventCallRequset;

import org.greenrobot.eventbus.EventBus;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

/**
 * Created by qingtiantree on 2017/4/24.
 */
public class UDPHelper{

    public String localIp;
    public String receiveIP;
    private int port = 9999;
    private short port1 = 9000;
    private int currentActivity = 0;

    private  short cmdNum = 0;

    private short send_port = 9998;
    private byte[] sendBuffer ,receiveBuffer;

    private static final short cmd_call_success = 0x6400;
    private static final short cmd_call_failed = 0x6499;
    private static final short cmd_request_call = 0x6401;
    private static final short cmd_refused_call = 0x6402;
    private static final short cmd_hungup_call = 0x6403;

    private short cmd_audio_data = 0x4800;


    private DatagramSocket sendSocket = null,receiveSocket = null;


    public HashMap CallBackMap,HungUpMap,deWeightMap; //

    private  boolean calledSelf = false;


    public void resetConfig(){  //重置配置
        calledSelf = false;
        CallbackMap().clear();
        HungUpMap().clear();
        deWeightMap().clear();
        cmdNum = 0;
    }



    private HashMap CallbackMap(){

        if (CallBackMap == null)
            CallBackMap = new HashMap();
        return CallBackMap;
    }
    private HashMap HungUpMap(){

        if (HungUpMap == null)
            HungUpMap = new HashMap();
        return HungUpMap;
    }
    private HashMap deWeightMap(){

        if (deWeightMap == null)
            deWeightMap = new HashMap();
        return deWeightMap;
    }

    public UDPHelper(String localIp) {
        this.localIp = localIp;
    }


    public void stop(){
        if (sendSocket!=null)
            sendSocket.close();
        if (receiveSocket != null)
            receiveSocket.close();
    }


    //回调接口 :主动拨打 接收别人回复消息
    public interface CallBackListener{

        void receiveCallbackMessage(boolean result, String ipAddress);


    }
    //回调接口 :接收挂断消息
    public interface HungUpBackListener{

        void receiveHungUpMessage(String ipAddress);

    }



    //发送请求拨打
    public void requsetCall(String receiveIP){

        cmdNum ++;
        System.out.println("发送UDP 拨打 1");
        this.receiveIP = receiveIP;
        byte[] bytes1 = FormatTransfer.byteMerger(FormatTransfer.shortToBytes((short)cmd_request_call), FormatTransfer.shortToBytes (cmdNum));

        byte[] bytes2 = FormatTransfer.byteMerger(bytes1, FormatTransfer.shortToBytes((short) localIp.length()));

        byte[] bytes = FormatTransfer.byteMerger(bytes2, FormatTransfer.shortToBytes(port1));
        port1 += 2;
        sendBuffer= FormatTransfer.byteMerger(bytes,localIp.getBytes());
        new udpSendThread().start();

    }

    //发送请求拨打
    public void requsetCall(short remotePort,String receiveIP,CallBackListener callBackListener, HungUpBackListener hungUpBackListener){

        HungUpMap().put(receiveIP,hungUpBackListener);
        CallbackMap().put(receiveIP,callBackListener);

        cmdNum ++;
        System.out.println("发送UDP 拨打 " + receiveIP);
        this.receiveIP = receiveIP;
        byte[] bytes1 = FormatTransfer.byteMerger(FormatTransfer.shortToBytes((short)cmd_request_call), FormatTransfer.shortToBytes (cmdNum));

        byte[] bytes2 = FormatTransfer.byteMerger(bytes1, FormatTransfer.shortToBytes((short) localIp.length()));

        byte[] bytes = FormatTransfer.byteMerger(bytes2, FormatTransfer.shortToBytes(remotePort));
        port1 += 2;
        sendBuffer= FormatTransfer.byteMerger(bytes,localIp.getBytes());
        new udpSendThread().start();

    }


    //发送拒绝接听
    public void refusedCall(String receiveIP){
        cmdNum ++;
        this.receiveIP = receiveIP;
        byte[] bytes1 = FormatTransfer.byteMerger(FormatTransfer.shortToBytes((short)cmd_refused_call), FormatTransfer.shortToBytes (cmdNum));

        byte[] bytes2 = FormatTransfer.byteMerger(bytes1, FormatTransfer.shortToBytes((short) localIp.length()));
        byte[] bytes = FormatTransfer.byteMerger(bytes2, FormatTransfer.shortToBytes((short)0));

        sendBuffer= FormatTransfer.byteMerger(bytes,localIp.getBytes());
        new udpSendThread().start();

    }

    //挂断
    public void hungupCall(String receiveIP){
        cmdNum ++;
        this.receiveIP = receiveIP;

        byte[] bytes1 = FormatTransfer.byteMerger(FormatTransfer.shortToBytes((short)cmd_hungup_call), FormatTransfer.shortToBytes (cmdNum));

        byte[] bytes2 = FormatTransfer.byteMerger(bytes1, FormatTransfer.shortToBytes((short) localIp.length()));
        byte[] bytes = FormatTransfer.byteMerger(bytes2, FormatTransfer.shortToBytes((short) 0));
        sendBuffer= FormatTransfer.byteMerger(bytes,localIp.getBytes());
        new udpSendThread().start();

        if (deWeightMap().containsKey(receiveIP)){ //已经有了该字段,那么比较,如果字段对应的cmdNum小,那么本socket有效,否则无效


            WeightObject object = (WeightObject) deWeightMap().get(receiveIP);

            object.setIndex(0);
            deWeightMap().put(receiveIP,object);

        }
    }


    //回复拨打结果
    public void replyCallResult(short receiveport,String receiveIP,boolean result){
        cmdNum ++;
        this.receiveIP = receiveIP;
        short replyCmd = 0;

        if (receiveIP.equals(localIp)){

        }else{
            receiveport += 1;
        }

        if (result){

            replyCmd = cmd_call_success;
        }else{
            replyCmd = cmd_call_failed;
            receiveport = 0;
        }
        byte[] bytes1 = FormatTransfer.byteMerger(FormatTransfer.shortToBytes((short)replyCmd), FormatTransfer.shortToBytes (cmdNum));

        byte[] bytes2 = FormatTransfer.byteMerger(bytes1, FormatTransfer.shortToBytes((short) localIp.length()));
        byte[] bytes = FormatTransfer.byteMerger(bytes2, FormatTransfer.shortToBytes(receiveport));
        sendBuffer= FormatTransfer.byteMerger(bytes,localIp.getBytes());
        new udpSendThread().start();

        if (!result){
            if (deWeightMap().containsKey(receiveIP)){ //已经有了该字段,那么比较,如果字段对应的cmdNum小,那么本socket有效,否则无效


                WeightObject object = (WeightObject) deWeightMap().get(receiveIP);

                object.setIndex(0);
                deWeightMap().put(receiveIP,object);

                }
        }
    }





    //回复拨打结果
    public void replyCallResult(short receiveport,String receiveIP,HungUpBackListener hungUpBackListener){
        HungUpMap().put(receiveIP,hungUpBackListener);
        cmdNum ++;
        this.receiveIP = receiveIP;
        short replyCmd = 0;

        if (receiveIP.equals(localIp)){

        }else{
            receiveport += 1;
        }


        replyCmd = cmd_call_success;

        byte[] bytes1 = FormatTransfer.byteMerger(FormatTransfer.shortToBytes((short)replyCmd), FormatTransfer.shortToBytes (cmdNum));

        byte[] bytes2 = FormatTransfer.byteMerger(bytes1, FormatTransfer.shortToBytes((short) localIp.length()));
        byte[] bytes = FormatTransfer.byteMerger(bytes2, FormatTransfer.shortToBytes(receiveport));
        sendBuffer= FormatTransfer.byteMerger(bytes,localIp.getBytes());
        new udpSendThread().start();
    }






    public void receiveData(){

        new udpReceiveThread().start();
    }



    public class udpSendThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
//                sendBuffer=receiveIP.getBytes();
                if (sendSocket == null){
                    System.out.println("成功发送 ——创建");
                    sendSocket = new DatagramSocket(send_port);
                }
                System.out.println("成功发送");
                InetAddress serverAddr = InetAddress.getByName(receiveIP);
                DatagramPacket outPacket = new DatagramPacket(sendBuffer, sendBuffer.length,serverAddr, port);
                sendSocket.send(outPacket);
//                sendSocket.close();
//
            } catch (Exception e)
            {
                System.out.println("成功发送 -异常" + e.getLocalizedMessage());
                // TODO Auto-generated catch block
            }
        }
    }

    public class udpReceiveThread extends Thread
    {


        @Override
        public void run()
        {
            if (receiveSocket != null)
                return;
            try
            {
                if (receiveSocket == null){

                    receiveSocket = new DatagramSocket(port);
                }


                while(true)
                {
                    byte[] inBuf= new byte[1024];
                   short cmd;
                    DatagramPacket inPacket=new DatagramPacket(inBuf,inBuf.length);
                    receiveSocket.receive(inPacket);


                    //获取cmd
                    cmd = FormatTransfer.lBytesToShort(inBuf);

                    //cmdnum
                    short cmdNum1 = FormatTransfer.lBytesToShort(inBuf,2);
                    //获取ip长度
                    short iplength = FormatTransfer.lBytesToShort(inBuf,4);

                    //接到的port
                    short remotePort = FormatTransfer.lBytesToShort(inBuf,6);

                    //ip
                    byte[] ipData = new byte[iplength];
                    System.arraycopy(inBuf,8,ipData,0,iplength);
                    String ipString = new String(ipData);
                    System.out.println("cmd  " + cmd + "iplength " + iplength + ipString);




                    //首先去重复socket
                    if (deWeightMap().containsKey(ipString)){ //已经有了该字段,那么比较,如果字段对应的cmdNum小,那么本socket有效,否则无效


                        WeightObject object = (WeightObject) deWeightMap().get(ipString);
                        if (object.getIndex() < cmdNum1){

                            System.out.println("cmd  去重 有效1"+ cmdNum1);
                            object.setIndex(cmdNum1);
                            deWeightMap().put(ipString,object);

                        }else {
                            System.out.println("cmd  去重   "+ cmdNum1 + "  fdafd" + object.getIndex());
                            //无效
                            continue;

                        }

                    }else{  //没有该字段,该socket有效
                        System.out.println("cmd  去重 有效"+ cmdNum1 +" "+cmd );
                        WeightObject object = new WeightObject(cmdNum1);
                        deWeightMap().put(ipString,object);
                    }



                    switch (cmd){

                        case cmd_call_success:{//拨打成功回复



                            if (CallbackMap().containsKey(ipString)){

                                CallBackListener callBackListener = (CallBackListener) CallbackMap().get(ipString);
                                callBackListener.receiveCallbackMessage(true,ipString);
                                CallbackMap().remove(ipString);
                            }

                            break;
                        }
                        case cmd_call_failed:{//拨打成功失败
//                            EventBus.getDefault().post(new EventCallResult(false,ipString));
//                            break;
                            //如果有回调,回调
                            if (CallbackMap().containsKey(ipString)){
                                CallBackListener callBackListener = (CallBackListener) CallbackMap().get(ipString);
                                callBackListener.receiveCallbackMessage(false,ipString);

                                CallbackMap().remove(ipString);
                            }

                            //如果还存在挂断回调,移除
                            if (HungUpMap().containsKey(ipString)){

                                HungUpMap().remove(ipString);
                            }

                            if (deWeightMap().containsKey(receiveIP)){ //已经有了该字段,那么比较,如果字段对应的cmdNum小,那么本socket有效,否则无效


                                WeightObject object = (WeightObject) deWeightMap().get(receiveIP);

                                object.setIndex(0);
                                deWeightMap().put(receiveIP,object);

                            }

                            break;
                        }
                        case cmd_request_call:{//请求通话 ,接受到来电信息
//                            EventBus.getDefault().post(new EventCallRequset(ipString));

                            //
                            System.out.println("cmd 接收到来电 " + remotePort +"  " +  ipString + " "+ localIp);

                            if (ipString.equals(localIp)){
                                System.out.println("cmd 接收到来电 " + remotePort +"  " +  ipString + " "+ localIp);

                                if (!calledSelf){ //自己拨打自己,直接回复
                                    calledSelf = true;
                                    replyCallResult(remotePort,localIp,true);
                                    break;
                                }

                            }

                            EventBus.getDefault().post(new EventCallRequset(ipString,remotePort));
                            break;
                        }
                        case cmd_hungup_call: {//挂断电话

                            //调用挂断回调,移除
                            if (HungUpMap().containsKey(ipString)){

                                HungUpBackListener hungUpBackListener = (HungUpBackListener) HungUpMap().get(ipString);

                                hungUpBackListener.receiveHungUpMessage(ipString);

                                HungUpMap().remove(ipString);
                            }

                            //移除
                            if (CallbackMap().containsKey(ipString)){

                                CallbackMap().remove(ipString);
                            }

                            if (deWeightMap().containsKey(receiveIP)){ //已经有了该字段,那么比较,如果字段对应的cmdNum小,那么本socket有效,否则无效


                                WeightObject object = (WeightObject) deWeightMap().get(receiveIP);

                                object.setIndex(0);
                                deWeightMap().put(receiveIP,object);

                            }

                            break;

                        }
                        case cmd_refused_call://挂断电话
//                            EventBus.getDefault().post(new EventCallRefused(ipString));
                            break;
                        default:
                            break;
                    }

                    Message msg = new Message();
                    System.out.println("接收到消息");
                }
            } catch (Exception e)
            {
                // TODO Auto-generated catch block
            }
        }
    }


}