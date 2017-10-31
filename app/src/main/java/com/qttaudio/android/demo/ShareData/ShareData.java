package com.qttaudio.android.demo.ShareData;

import com.qttaudio.android.demo.Model.AddIPModel;
import com.qttaudio.android.demo.tool.UDPHelper;

import java.util.ArrayList;

/**
 * Created by qingtiantree on 2017/4/25.
 */

public class ShareData {

    //添加ip界面跳转到通话界面
    public static ArrayList<AddIPModel> shareIpArray; //添加的ip数组
    public static UDPHelper helper = null;
    public static String localIP ; //本机ip
    public static Boolean isReceive;//是否作为接听方直接进如通话界面
    public  static AddIPModel model = null; //
    public static short port;//是否作为接听方直接进如通话界面
}
