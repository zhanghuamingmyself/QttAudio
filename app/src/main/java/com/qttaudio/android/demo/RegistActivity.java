package com.qttaudio.android.demo;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class RegistActivity extends FragmentActivity {


    private ImageButton backButton;
    private Button registbutton;
    private TextView registResult;

    private Button getVarificationButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);


        backButton = (ImageButton) findViewById(R.id.regist_backbutton);
        getVarificationButton = (Button) findViewById(R.id.regist_get_varification_button);
        registbutton = (Button) findViewById(R.id.regist_regist_button);
        registResult = (TextView) findViewById(R.id.regist_result_textView);
        final MyCountDownTimer myCountDownTimer = new MyCountDownTimer(60000,1000);






        //返回按钮
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //判断当前注册是否成功，成功反向传值 电话号码

                finish();
            }
        });


        //验证码倒计时
        getVarificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myCountDownTimer.start();
            }
        });


        //注册按钮
        registbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                //判断是否注册成功
                //成功显示，成功延时1秒返回

                new Thread() {
                    public void run() {
                        //这儿是耗时操作，完成之后更新UI；
                        runOnUiThread(new Runnable(){

                            @Override
                            public void run() {
                                //更新UI

                                Thread.currentThread();
                                try {
                                    registResult.setVisibility(View.VISIBLE);
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                finish();
                            }

                        });
                    }
                }.start();






            }
        });

    }

//复写倒计时
public class MyCountDownTimer extends CountDownTimer {

    public MyCountDownTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    //计时过程
    @Override
    public void onTick(long l) {
        //防止计时过程中重复点击
        getVarificationButton.setClickable(false);
        getVarificationButton.setBackgroundColor(Color.parseColor("#BFBFBF"));
        getVarificationButton.setText(l/1000+"s");

    }

    //计时完毕的方法
    @Override
    public void onFinish() {
        getVarificationButton.setBackgroundColor(Color.parseColor("#3f51b5"));
        //重新给Button设置文字
        getVarificationButton.setText("获取验证码");
        //设置可点击
        getVarificationButton.setClickable(true);
    }
}
}

