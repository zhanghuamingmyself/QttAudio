package com.qttaudio.android.demo;
import com.qttaudio.android.demo.Model.AddIPModel;
import com.qttaudio.android.demo.ShareData.ShareData;
import com.qttaudio.android.demo.adpter.AddIpListViewAdapter;
import com.qttaudio.android.demo.eventModel.EventCallRequset;
import com.qttaudio.android.demo.tool.UDPHelper;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

//import com.qttaudio.android.demo.tool.UDPHelper;
import com.qttaudio.sdk.QttAudioEngine;

public class AddIPActivity extends AppCompatActivity {

    public ListView listView;
    public ImageButton callButton;
    public Button backButton,delectEchoButton;
    public TextView showDelayTextView,localMessageTextView;
    AddIpListViewAdapter addIpAdapter;
    public ArrayList<AddIPModel> addIpList;
    private  int i = 1;
    private Boolean isCurrentActivity; //是否是当前界面

    private String ipPrefix;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ip);

        isCurrentActivity = true;
        //设置共享数据
        if (ShareData.helper == null){  //开始监听
            ShareData.helper = new UDPHelper(getHostIP());
            String [] temp = null;
            temp = getHostIP().split("\\.");
            try {
                ipPrefix = temp[0]+"."+temp[1]+"."+temp[2] +".";
            }catch (Exception e){
                ipPrefix = "";
            }
        }
        ShareData.localIP = getHostIP();
        //注册regist
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        ShareData.helper.receiveData();
//        ShareData.helper.resetConfig();

        localMessageTextView = (TextView) findViewById(R.id.addip_showmessage);
        callButton = (ImageButton)findViewById(R.id.addip_call_imagebutton);
        backButton = (Button)findViewById(R.id.backbutton) ;
        listView = (ListView)findViewById(R.id.addip_listview);
        listView.setDividerHeight(0); //去掉分割线

//        delectEchoButton = (Button) findViewById(R.id.addip_call_deletecho);
//        showDelayTextView = (TextView) findViewById(R.id.addip_call_delayTextView);


        //listView
        addIpList = new ArrayList<>() ;
        AddIPModel localUser = new AddIPModel(0,"本机ip:",getHostIP(),false);
        AddIPModel tmpUser = new AddIPModel(1,"对方ip_1",ipPrefix,true);
        addIpList.add(localUser);
        addIpList.add(tmpUser);

        String tmpStr = "本机IP: "+getHostIP();
        localMessageTextView.setText(tmpStr);

        addIpAdapter = new AddIpListViewAdapter(AddIPActivity.this,addIpList);
        addIpAdapter.setOnClickListener(new AddIpListViewAdapter.ImageButtonClick() {
           @Override
           public void onItemClick(View view, int index) {

               System.out.print("1111111111111"+addIpList.get(index).getIpAddress());
               LinearLayout layout = (LinearLayout) listView.getChildAt(listView.getChildCount() - 1);
               EditText ipTempTextView = (EditText) layout.findViewById(R.id.listview_addip_editText);

               String lastIP = ipTempTextView.getText().toString();
               System.out.println("lastIP"+lastIP);
               if (lastIP.equals(ipPrefix)){

                   addIpList.remove(addIpList.size()-1);
                   int newIndex = addIpList.size()+1;
                   String ipName = "对方ip_" + newIndex ;
                   AddIPModel tmpUser = new AddIPModel(1,ipName,ipPrefix,true);
                   addIpList.add(tmpUser);

                   new Thread() {
                       public void run() {
                           //这儿是耗时操作，完成之后更新UI；
                           runOnUiThread(new Runnable(){

                               @Override
                               public void run() {
                                   //更新UI
                                   addIpAdapter.notifyDataSetChanged();
                               }

                           });
                       }
                   }.start();
               }
               if (addIpList.get(index).getIpAddress().length() > 0 && !lastIP.equals(ipPrefix)){

                    addIpList.get(index).setShowAdd(false);

                    int newIndex = addIpList.size()+1;
                   String ipName = "对方ip_" + newIndex ;
                   AddIPModel tmpUser = new AddIPModel(1,ipName,ipPrefix,true);
                   addIpList.add(tmpUser);

                   new Thread() {
                       public void run() {
                           //这儿是耗时操作，完成之后更新UI；
                           runOnUiThread(new Runnable(){

                               @Override
                               public void run() {
                                   //更新UI
                                   addIpAdapter.notifyDataSetChanged();
                               }

                           });
                       }
                   }.start();

//                   addIpAdapter.notifyDataSetChanged();
               }

           }
       });
        listView.setAdapter(addIpAdapter);
        //拨打按钮
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //传值，呼叫，界面跳转


                LinearLayout layout = (LinearLayout) listView.getChildAt(listView.getChildCount() - 1);
                EditText ipTempTextView = (EditText) layout.findViewById(R.id.listview_addip_editText);

                String lastIP = ipTempTextView.getText().toString();

                if(lastIP.length() > 0 ){


                    if (!lastIP.equals(ipPrefix)) {

                        addIpList.get(addIpList.size() - 1).setShowAdd(false);
                        addIpList.get(addIpList.size() - 1).setIpAddress(lastIP);
                        int newIndex = addIpList.size();
                        String ipName = "对方ip_" + newIndex + ":";
                        AddIPModel tmpUser = new AddIPModel(1,ipName,ipPrefix,true);
                    }

//                    addIpList.add(tmpUser);
                }

                System.out.println("显示当前ip " + lastIP + "   "+addIpList.get(addIpList.size() - 1).getIpAddress());


                if (1<=addIpList.size()&&!(addIpList.get(0).getIpAddress()).equals(ipPrefix)){ //至少有三个元素，一个自己 第二个为对方，…… 最后一个为空


                    boolean tempBool = false;

                    for (int i = 1;i<addIpList.size();i++){
                        if (addIpList.get(i).getIpAddress().length() > 0){

                            if (addIpList.get(i).isContainOther()){
                                tempBool = true;
                            }
                        }

                    }

                    if (tempBool){

                        QttAudioEngine.me().detectEcho();
                    }

//                    addIpList.remove(addIpList.size() - 1);

                    //传值
                    ShareData.isReceive = false;
                    System.out.println("ip +"+ ipPrefix+"  "+addIpList.get(addIpList.size() - 1).getIpAddress());
                    if (addIpList.get(addIpList.size() - 1).getIpAddress().equals(ipPrefix)){

//                        if (addIpList.size() == 2){
//                            return;
//                        }
                        System.out.println("ip +remove" );
                        addIpList.remove(addIpList.size() - 1);

                    }



//                    if (addIpList.size() >= 2){
//                        addIpList.remove(0);
//                    }



                    ShareData.shareIpArray = addIpList;
                    ShareData.model = null;

                    isCurrentActivity = false;

                    //界面跳转
                    Intent intent = new Intent();
                    intent.setClass(AddIPActivity.this , CallActivity.class);
                    startActivityForResult(intent, 1000);
                }


            }
        });

        //退出按钮
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    ShareData.helper.stop();
                    ShareData.helper = null;

                    QttAudioEngine.me().free();

                    finish();
                }catch (Exception e){

                }

            }
        });



    }
    @Subscribe
    public void  onEvent(final EventCallRequset notication){
        //接收到来电


            System.out.println("接收到来电1 coming ip" + notication.getIp());
            new Thread() {
                public void run() {
                    //这儿是耗时操作，完成之后更新UI；
                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            //更新UI
                            showNormalDialog1(notication.getIp(),notication.getPort());
                        }

                    });
                }
            }.start();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 获取ip地址
     * @return
     */
    public static String getHostIP() {

        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.i("yao", "SocketException");
            e.printStackTrace();
        }
        return hostIp;

    }


    public static String hexStr2Str(String hexStr)
    {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++)
        {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }


    /** 获取从子窗口传递过来的数据 */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 || resultCode == 1001){
            System.out.println("ip + 回到该界面");
            addIpList.clear();
//            AddIPModel localUser = new AddIPModel(0,"本机ip:",getHostIP(),false);
            AddIPModel tmpUser = new AddIPModel(1,"对方ip_1",ipPrefix,true);
//            addIpList.add(localUser);
            addIpList.add(tmpUser);

            isCurrentActivity = true;
            new Thread() {
                public void run() {
                    //这儿是耗时操作，完成之后更新UI；
                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            //更新UI
                            addIpAdapter.notifyDataSetChanged();
                        }

                    });
                }
            }.start();
        }
    }

    private void showNormalDialog1(final String ip ,final short port){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog1 =
                new AlertDialog.Builder(AddIPActivity.this);
//        normalDialog1.setIcon(R.drawable.icon_dialog);
        normalDialog1.setTitle("来电通知");
        normalDialog1.setMessage(ip);
        normalDialog1.setPositiveButton("接听",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do

                        System.out.println("cmd 成功接通" +ip);
                        AddIPModel tmpUser = new AddIPModel(1,"对方ip:",ip,true);
                        tmpUser.setIpName("对方ip_1");
                        tmpUser.setIpAddress(ip);
                        addIpList.clear();
                        addIpList.add(tmpUser);

                        ShareData.model = tmpUser;
                        if (addIpList.get(addIpList.size() - 1).getIpAddress().equals(ipPrefix)){}
                            addIpList.remove(addIpList.size() - 1);
                        ShareData.shareIpArray = addIpList;
                        ShareData.isReceive = true;
                        ShareData.port = port;
                        //界面跳转
                        Intent intent = new Intent();
                        intent.setClass(AddIPActivity.this , CallActivity.class);
                        startActivityForResult(intent, 1000);
                    }
                });
        normalDialog1.setNegativeButton("挂断",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        ShareData.helper.replyCallResult(port,ip,false);
                    }
                });
        // 显示
        normalDialog1.show();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
            try {
                ShareData.helper.stop();
                ShareData.helper = null;

                QttAudioEngine.me().free();

                finish();
            }catch (Exception e){

            }
        }
        return super.onKeyDown(keyCode,event);
    }

}