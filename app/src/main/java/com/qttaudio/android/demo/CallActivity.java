package com.qttaudio.android.demo;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.qttaudio.android.demo.Model.AddIPModel;
import com.qttaudio.android.demo.ShareData.ShareData;
import com.qttaudio.android.demo.adpter.CallListViewAdapter;
import com.qttaudio.android.demo.eventModel.EventCallRequset;

import com.qttaudio.android.demo.tool.UDPHelper;
import com.qttaudio.sdk.QttAudioEngine;
import com.qttaudio.sdk.QttAudioOutput;
import com.qttaudio.sdk.QttAudioStream;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CallActivity extends AppCompatActivity {


    private ArrayList<AddIPModel> callArray;
    private ArrayList<QttAudioStream> streamArray;


    private PullDownMenu pullDownMenu;
    public ImageButton hungupImageButton;
    public SeekBar inputSeekBar,outputSeekBar,denoiseSeekbar; //输入输出音量设置
    public Switch microPhoneSwitch,bluetoothSwitch,denoiseSwitch,dechoSwitch;//麦克风，蓝牙开关
    public TextView callStatusTextView; //通话状态
    public ListView callListView; //通话列表

//    private Spinner AudioSpiner; //音频设备选择下拉列表
//
//    private AudioManager am; //用于设置音频输出

    private ImageButton denoiseButton,dechoButton;
    private QttAudioOutput audioOutput;

    public CallListViewAdapter adpter;


    private boolean isClicked = false;
    private boolean isNoiseClicked = false;
    public short remotePort; //远端port
    public HashMap streamMap;

    private int acceptSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        acceptSize = 0;
        //注册regist
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        System.out.println("这个是这个界面");
        audioOutput = QttAudioOutput.HEADPHONE;
        init();
        hungupImageButton = (ImageButton)findViewById(R.id.call_hungup_imagebutton);
        inputSeekBar = (SeekBar) findViewById(R.id.call_input_seekbar);
        outputSeekBar = (SeekBar) findViewById(R.id.call_output_seekbar);
        microPhoneSwitch = (Switch) findViewById(R.id.call_microphone_switch);
        bluetoothSwitch = (Switch) findViewById(R.id.bluetooth_switch);
        callStatusTextView = (TextView) findViewById(R.id.call_status_textview);
        callListView = (ListView) findViewById(R.id.call_call_listview);
        denoiseButton = (ImageButton) findViewById(R.id.call_denoise_imagebutton);
        dechoButton = (ImageButton) findViewById(R.id.call_decho_imagebutton);
        denoiseSeekbar = (SeekBar) findViewById(R.id.call_noise_seekbar);
//        dechoSwitch = (Switch) findViewById(R.id.call_noise_switch2);
//        denoiseSwitch = (Switch) findViewById(R.id.call_noise_switch1);


//        AudioSpiner = (Spinner) findViewById(R.id.call_audio_spinner);

//        am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        denoiseSeekbar.setEnabled(false);
//        denoiseSwitch.setClickable(false);
//        dechoSwitch.setClickable(false);
        streamMap = new HashMap();
        callArray = ShareData.shareIpArray;

        //显示下拉列表
        final String arr[] = new  String[]{
          "默认输出", "有线耳机","蓝牙耳机","扬声器"
        };

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,R.layout.simple_spinner_item,arr);
//       AudioSpiner.setAdapter(arrayAdapter);
//        //注册事件
//        AudioSpiner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                Spinner spinner = (Spinner) parent;
//                audioOutput = QttAudioOutput.HEADPHONE;
//                if (spinner.getItemAtPosition(position).equals("默认输出")){
//                    audioOutput = QttAudioOutput.HEADPHONE;
//
//
//                }else if (spinner.getItemAtPosition(position).equals("有线耳机")){
//
//                    audioOutput = QttAudioOutput.HEADPHONE;
//
//                } else if(spinner.getItemAtPosition(position).equals("蓝牙耳机")){
//
//                    audioOutput = QttAudioOutput.BLUETOOTH;
//
//                }else{
//                    audioOutput = QttAudioOutput.SPEAKER;
//
//                }
//                System.out.println("mode is "+audioOutput);
//                QttAudioEngine.me().routeAudioTo(audioOutput);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });


        //创建流拨打,或者接通
        short port = 9000;
        System.out.println("成功   1234");
        if (!ShareData.isReceive){   //作为拨打方进入该界面

            System.out.println("成功   拨打");
            //回复接通,并创建挂断的回调

            //遍历
            for(int i = 0; i < callArray.size(); i ++){

                //创建流
                QttAudioStream tmpStream = QttAudioEngine.me().createStream();
                AddIPModel model = callArray.get(i);
                System.out.println("成功   拨打111" +model.getIpAddress() );
                if (model.getIpAddress().equals(ShareData.localIP)){

                    tmpStream.setRtpParams(port,-1,model.getIpAddress(),port,-1,2,123,97);
//                    recordTime();
//                    acceptSize ++;
//                    tmpStream.start();
                }else{
                    tmpStream.setRtpParams(port,-1,model.getIpAddress(),port + 1,-1,2,123,97);

                }

                streamMap.put(model.getIpAddress(),tmpStream);


                //拨打
                ShareData.helper.requsetCall(port,model.getIpAddress(), new UDPHelper.CallBackListener() {
                    @Override
                    public void receiveCallbackMessage(boolean result, String ipAddress) {
                        //接收到接通消息

                        QttAudioStream callStream = (QttAudioStream)streamMap.get(ipAddress);

                        System.out.println("成功接收9876" + result);

                        if (result) {
                            acceptSize ++;
                            if (acceptSize == callArray.size()){

                            }
                            //启动音频流
                            callStream.start();
                            //刷新计时
                            recordTime();
                        }else{
                            //挂断
                            callStream.free();
                            if (streamMap.containsKey(ipAddress)){
                                streamMap.remove(ipAddress);
                            }
                            removeModelWithIP(ipAddress);

                        }


                    }

                }, new UDPHelper.HungUpBackListener() {
                    @Override
                    public void receiveHungUpMessage(String ipAddress) {

                        //接收到挂断消息

                        QttAudioStream callStream = (QttAudioStream)streamMap.get(ipAddress);
                        callStream.free();
                        removeModelWithIP(ipAddress);


                    }
                });



                port += 2;
            }







        }else{  //作为接听方进入该界面
            callArray = new ArrayList<AddIPModel>();
            callArray.add(ShareData.model);
            remotePort = ShareData.port;
            System.out.println(" cmd 成功接通 " +remotePort +"   "+ ShareData.model.getIpAddress()+ ShareData.port );
            //挨个挨个拨打,并为每个拨打创建收听回复的回调,以及接收到挂断的回调

            //创建回调的流
            QttAudioStream tmpStream = QttAudioEngine.me().createStream();


            if (ShareData.model.getIpAddress() == ShareData.localIP){

                tmpStream.setRtpParams(remotePort,-1, ShareData.model.getIpAddress(),remotePort,-1,2,123,97);

            }else{
                tmpStream.setRtpParams((remotePort + 1),-1, ShareData.model.getIpAddress(),remotePort ,-1,2,123,97);

            }

            tmpStream.start();
            acceptSize ++;
            //刷新计时
            recordTime();
            streamMap.put(ShareData.model.getIpAddress(),tmpStream);


            ShareData.helper.replyCallResult(remotePort, ShareData.model.getIpAddress(), new UDPHelper.HungUpBackListener() {
                @Override
                public void receiveHungUpMessage(String ipAddress) {
                    //接收到挂断消息


                    QttAudioStream callStream = (QttAudioStream)streamMap.get(ipAddress);
                    callStream.free();

                    removeModelWithIP(ipAddress);
                }
            });




        }


        QttAudioEngine.me().enableSpeaker(false);







        adpter = new CallListViewAdapter(CallActivity.this,callArray);
        callListView.setAdapter(adpter);



        //decho
        dechoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (acceptSize >= callArray.size() ){
//
                System.out.println("decho");
                    if (!isClicked) {
                        System.out.println("decho2");
                        isClicked = !isClicked;
                        dechoButton.setImageDrawable(getResources().getDrawable(R.drawable.squeak_1));
                        dechoButton.setClickable(false);
                        for (int j = 0; j < callArray.size(); j++) {


                            //停止
                            if (streamMap.containsKey(callArray.get(j).getIpAddress())) {
                                String ipAddress = callArray.get(j).getIpAddress();

                                QttAudioStream callStream = (QttAudioStream) streamMap.get(ipAddress);
                                callStream.stop();

                            }
                        }
                        //q
                        QttAudioEngine.me().detectEcho();

                        for (int j = 0; j < callArray.size(); j++) {


                            //停止
                            if (streamMap.containsKey(callArray.get(j).getIpAddress())) {
                                String ipAddress = callArray.get(j).getIpAddress();

                                QttAudioStream callStream = (QttAudioStream) streamMap.get(ipAddress);
                                callStream.start();

                            }
                        }


                        QttAudioEngine.me().routeAudioTo(audioOutput);
                    }
                }
            }
        });
//        dechoSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//
//                if (acceptSize >= callArray.size() && isChecked){
//
//                    if (!isClicked) {
//                        isClicked = !isClicked;
//                        dechoSwitch.setClickable(false);
//                        for (int j = 0; j < callArray.size(); j++) {
//
//
//                            //停止
//                            if (streamMap.containsKey(callArray.get(j).getIpAddress())) {
//                                String ipAddress = callArray.get(j).getIpAddress();
//
//                                QttAudioStream callStream = (QttAudioStream) streamMap.get(ipAddress);
//                                callStream.stop();
//
//                            }
//                        }
//                        //q
//                        QttAudioEngine.me().detectEcho();
//
//                        for (int j = 0; j < callArray.size(); j++) {
//
//
//                            //停止
//                            if (streamMap.containsKey(callArray.get(j).getIpAddress())) {
//                                String ipAddress = callArray.get(j).getIpAddress();
//
//                                QttAudioStream callStream = (QttAudioStream) streamMap.get(ipAddress);
//                                callStream.start();
//
//                            }
//                        }
//
//
//                        QttAudioEngine.me().routeAudioTo(audioOutput);
//                    }
//                }
//
//
//
//            }
//        });
//
//        //降噪
//        denoiseSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
//
//                if (acceptSize >= callArray.size()) {
//                    for (int j = 0; j < callArray.size() ; j++) {
//
//
//                        //停止
//                        if (streamMap.containsKey(callArray.get(j).getIpAddress())) {
//                            String ipAddress = callArray.get(j).getIpAddress();
//
//                            QttAudioStream callStream = (QttAudioStream)streamMap.get(ipAddress);
//                            callStream.stop();
//
//                        }
//                    }
//                    QttAudioEngine.me().enableNoiseGate(isChecked);
//                    denoiseSeekbar.setEnabled(isChecked);
//                    for (int j = 0; j < callArray.size() ; j++) {
//
//
//                        //停止
//                        if (streamMap.containsKey(callArray.get(j).getIpAddress())) {
//                            String ipAddress = callArray.get(j).getIpAddress();
//
//                            QttAudioStream callStream = (QttAudioStream)streamMap.get(ipAddress);
//                            callStream.start();
//
//                        }
//                    }
//                    QttAudioEngine.me().routeAudioTo(audioOutput);
//                }
//            }
//        });


        //降噪
        denoiseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isNoiseClicked = !isNoiseClicked;


                if (acceptSize >= callArray.size()) {

                    if (isNoiseClicked){
                        denoiseButton.setImageDrawable(getResources().getDrawable(R.drawable.squeak_1));
                    }else{
                        denoiseButton.setImageDrawable(getResources().getDrawable(R.drawable.squeak_2));
                    }

                    for (int j = 0; j < callArray.size() ; j++) {


                        //停止
                        if (streamMap.containsKey(callArray.get(j).getIpAddress())) {
                            String ipAddress = callArray.get(j).getIpAddress();

                            QttAudioStream callStream = (QttAudioStream)streamMap.get(ipAddress);
                            callStream.stop();

                        }
                    }
                    QttAudioEngine.me().enableNoiseGate(isNoiseClicked);
                    denoiseSeekbar.setEnabled(isNoiseClicked);



                    for (int j = 0; j < callArray.size() ; j++) {


                        //停止
                        if (streamMap.containsKey(callArray.get(j).getIpAddress())) {
                            String ipAddress = callArray.get(j).getIpAddress();

                            QttAudioStream callStream = (QttAudioStream)streamMap.get(ipAddress);
                            callStream.start();

                        }
                    }
                    QttAudioEngine.me().routeAudioTo(audioOutput);

            }
            }
        });

        //扬声器
        microPhoneSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    QttAudioEngine.me().enableSpeaker(true);

                } else {
                    QttAudioEngine.me().enableSpeaker(false);

                }
            }
        });

        //蓝牙
        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub

                //蓝牙操作
               if (turnOnBluetooth(isChecked)){

               }else{


               }
            }
        });

        //降噪阀值
        denoiseSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float temp = progress / 33.0f;
                QttAudioEngine.me().setNoiseGateThres(temp);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //播放音量
        outputSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {



            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) { //默认0 - 100
                float volume = i / 33.0f;
                for (int j = 0; j < callArray.size() ; j++) {


                    //播放
                    if (streamMap.containsKey(callArray.get(j).getIpAddress())) {
                        String ipAddress = callArray.get(j).getIpAddress();
                        System.out.println("output " + ipAddress);
                        QttAudioStream callStream = (QttAudioStream)streamMap.get(ipAddress);
                        callStream.setOutputVolume(volume);

                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //录制音量
        inputSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float volume = i / 33.0f;
                System.out.println("input " + volume);
                for (int j = 0; j < callArray.size()  ; j++) {
                    //

                    if (streamMap.containsKey(callArray.get(j).getIpAddress())) {

                        QttAudioStream stream1 = (QttAudioStream) streamMap.get(callArray.get(j).getIpAddress());
                        stream1.setInputVolume(volume);

                    }
                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        //挂断
        hungupImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                //请求挂断
                for (int i = 0; i < callArray.size() ; i++) {

                    System.out.println("挂断" + callArray.get(i).getIpAddress());
                    if (callArray.get(i).getIpAddress() != null) {
                        System.out.println("挂断" + callArray.get(i).getIpAddress());
                        ShareData.helper.hungupCall(callArray.get(i).getIpAddress());
                    }

                    //关闭流
                    if (streamMap.containsKey(callArray.get(i).getIpAddress())) {

                        QttAudioStream stream1 = (QttAudioStream) streamMap.get(callArray.get(i).getIpAddress());

                        stream1.stop();
                        stream1.free();
                        try {
                            streamMap.remove(callArray.get(i).getIpAddress());
                        }catch (Exception e){

                        }


                    }

                }

               // am.stopBluetoothSco();
                ShareData.helper.resetConfig();

                //退出
                Intent intent1 = new Intent();
                // 设置回传意图
                setResult(1001,intent1);
                // 结束当前窗口的生命周期
                finish();
            }
        });

    }















    /**
     * 当前 Android 设备是否支持 Bluetooth
     *
     * @return true：支持 Bluetooth false：不支持 Bluetooth
     */

    public static boolean isBluetoothSupported()

    {

        return BluetoothAdapter.getDefaultAdapter() != null ? true : false;
    }

        /**
         * 当前 Android 设备的 bluetooth 是否已经开启
         *
         * @return true：Bluetooth 已经开启 false：Bluetooth 未开启
         */

    public static boolean isBluetoothEnabled()
    {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter != null)
        {
            return bluetoothAdapter.isEnabled();
        }
        return false;

    }
    /**
     * 强制开关当前 Android 设备的 Bluetooth
     *
     * @return true：强制打开 Bluetooth　成功　false：强制打开 Bluetooth 失败
     */
    public static boolean turnOnBluetooth(boolean isOn)

    {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter != null)
        {
            if (isOn)
                 return bluetoothAdapter.enable();
            else
                return bluetoothAdapter.disable();
        }
        return false;

    }



    private int currVolume = 0;
    /**
     * 打开扬声器
     */




    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    //来电消息

    @Subscribe
    public void  onEvent(EventCallRequset notication){
        //接收到来电
        System.out.println("接收到来电 coming ip" + notication.getIp());
        //显示对话框接收或者挂断
        showNormalDialog(notication.getIp());


    }


    //通过ip移除 listviewCell
    public void removeModelWithIP(String ip){

        int index = -1;
        for (int i = 0; i < callArray.size(); i++) {
            if (callArray.get(i).getIpAddress().equals(ip)) {
                index = i;
                break;
            }
        }


        //移除
        if (-1 != index)
            callArray.remove(index);
        else
            return;



        //没人了退出
        if (callArray.size() == 0){

          //  am.stopBluetoothSco();
            ShareData.helper.resetConfig();
            //退出
            Intent intent1 = new Intent();
            // 设置回传意图
            setResult(1001,intent1);
            // 结束当前窗口的生命周期
            finish();
        }else{

            //刷新
            new Thread() {
                public void run() {
                    //这儿是耗时操作，完成之后更新UI；
                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            //更新UI
                            adpter.notifyDataSetChanged();
                        }

                    });
                }
            }.start();

        }


    }



    public void recordTime(){

        if (callStatusTextView.getText().toString().equals("接通中…"))
            startRecord();
    }

    private void showNormalDialog(final String ip){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
//        final AlertDialog.Builder normalDialog =
//                new AlertDialog.Builder(CallActivity.this);
////        normalDialog.setIcon(R.drawable.icon_dialog);
//        new Thread() {
//            public void run() {
//                //这儿是耗时操作，完成之后更新UI；
//                runOnUiThread(new Runnable(){
//
//                    @Override
//                    public void run() {
//        normalDialog.setTitle("来电通知");
//        normalDialog.setMessage(ip);
//
//        normalDialog.setNegativeButton("挂断",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        //...To-do
//                        ShareData.helper.replyCallResult(ip,false);
//                    }
//                });
//
//
//                        //更新UI
//                        // 显示
//                        normalDialog.show();
//                    }
//
//                });
//            }
//        }.start();

    }




    //计时
    private void startRecord() {
        uiHandler.sendEmptyMessageDelayed(1, 1000);
    }

    private int timeUsedInSec;
    private Handler uiHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    addTimeUsed();
                    updateClockUI();

                    uiHandler.sendEmptyMessageDelayed(1, 1000);
                    break;
                default:
                    break;
            }
        }

    };
    private void updateClockUI() {
        callStatusTextView.setText(getMin()+":"+getSec());
    }

    public void addTimeUsed() {
        timeUsedInSec=timeUsedInSec+1;
//        timeUsed = this.getMin() + ":" + this.getSec();
    }

    public CharSequence getMin() {
        int min = timeUsedInSec / 60;
        return min < 10 ? "0" + min : String.valueOf(min);
    }

    public CharSequence getSec() {
        int sec = timeUsedInSec % 60;
        return sec < 10 ? "0" + sec : String.valueOf(sec);
    }


    private void init() {
        pullDownMenu=(PullDownMenu)findViewById(R.id.call_spinner);
        List<String> stringList=new ArrayList<>();
        stringList.add("默认输出");
        stringList.add("有线耳机");
        stringList.add("蓝牙耳机");
        stringList.add("扬声器");
        pullDownMenu.setData("默认输出",stringList,false);

        pullDownMenu.setSelectMode(new PullDownMenu.selectMode() {
            @Override
            public void onItemClick(String string) {

                if (string.equals("默认输出")){
                    audioOutput = QttAudioOutput.HEADPHONE;


                }else if (string.equals("有线耳机")){

                    audioOutput = QttAudioOutput.HEADPHONE;

                } else if(string.equals("蓝牙耳机")){

                    audioOutput = QttAudioOutput.BLUETOOTH;

                }else{
                    audioOutput = QttAudioOutput.SPEAKER;

                }
                System.out.println("mode is "+audioOutput);
                QttAudioEngine.me().routeAudioTo(audioOutput);
            }
        });
    }

}
