package com.qttaudio.android.demo;


import com.qttaudio.android.demo.Model.AddIPModel;
import com.qttaudio.android.demo.PermissionsActivity;
import com.qttaudio.android.demo.ShareData.ShareData;
import com.qttaudio.sdk.QttAudioEngine;
import com.qttaudio.sdk.QttException;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ScrollingView;
import android.view.GestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class MainActivity extends FragmentActivity {


    private EditText loginNumberEditText; //账号输入
    private CheckBox rememberNumberCheckBox;//记住密码
    private Button registButton;
    protected Button loginButton;//注册按钮
    Context context;
    private Boolean isRemember;//是否记住账号

    private ScrollingView scrollingView;

    private static final int REQUEST_CODE = 0; // 请求码

    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.RECORD_AUDIO
    };


    private PermissionsChecker permissionsChecker;//权限检测器




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        context = getApplicationContext();
//        appKey = “网站申请试用时填写的手机号”;
//




        loginNumberEditText = (EditText) findViewById(R.id.login_editText);
        registButton = (Button) findViewById(R.id.login_register_button);
        rememberNumberCheckBox = (CheckBox) findViewById(R.id.login_rememberPhone);
        loginButton = (Button) findViewById(R.id.login_button);
        loginNumberEditText.clearFocus();

        permissionsChecker = new PermissionsChecker(this);

        SharedPreferences sharedata = getSharedPreferences("data", 0);
        String data = sharedata.getString("remember", "");


        if (data.equals("true")){
            rememberNumberCheckBox.setChecked(true);
            loginNumberEditText.setText(sharedata.getString("name", ""));
        }







        //登陆按钮
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //验证，





                //注册SDK

                    try {
//                        QttAudioEngine.me().init(context, "ad0f1eeda5f54abc10aa6c4344079d0c");
                        QttAudioEngine.me().init(context, loginNumberEditText.getText().toString());
                        //跳转
                        if (rememberNumberCheckBox.isChecked()) {
                            SharedPreferences.Editor sharedata = getSharedPreferences("data", 0).edit();
                            sharedata.putString("name", loginNumberEditText.getText().toString());
                            sharedata.commit();
                        }


                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this , AddIPActivity.class);
                        MainActivity.this.startActivity(intent);
                    } catch (QttException e) {
                        e.printStackTrace();
                        showNormalDialog1();

                    }









            }
        });

       //记住密码
        rememberNumberCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor sharedata = getSharedPreferences("data", 0).edit();
                if (rememberNumberCheckBox.isChecked()){

                    sharedata.putString("remember", "true");

                }else{
                    sharedata.putString("remember", "false");
                }
                sharedata.commit();

            }
        });

        //注册
        registButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this , RegistActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume(){

        super.onResume();
        //
        if (permissionsChecker.lacksPermissions(PERMISSIONS)){

            startPermissionActivity();
        }
    }

    private void startPermissionActivity(){

        PermissionsActivity.startActivityForResult(this,REQUEST_CODE,PERMISSIONS);
    }
    @Override
    protected void onActivityResult(int requstCode, int resultCode,Intent data){
        super.onActivityResult(requstCode,resultCode,data);
        if (requstCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED){
            //quanxian buyunxu

        }
    }

    private void showNormalDialog1(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog1 =
                new AlertDialog.Builder(MainActivity.this);
//        normalDialog1.setIcon(R.drawable.icon_dialog);
        normalDialog1.setTitle("手机号(AppKey)验证失败");

        normalDialog1.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do


                    }
                });

        // 显示
        normalDialog1.show();
    }
    //if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
}


