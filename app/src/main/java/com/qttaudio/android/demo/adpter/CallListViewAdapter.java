package com.qttaudio.android.demo.adpter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.qttaudio.android.demo.Model.AddIPModel;
import com.qttaudio.android.demo.R;

import java.util.List;

/**
 * Created by qingtiantree on 2017/4/27.
 */

public class CallListViewAdapter extends BaseAdapter {

    List<AddIPModel> userList;
    private Context context;



    //创建
    public CallListViewAdapter(Context context, List<AddIPModel> userList) {
        this.context = context;
        this.userList = userList;
    }
    @Override
    public int getCount() {
        if (userList == null){

            return 0;
        }
        return userList.size();
    }

    @Override
    public Object getItem(int i) {
        return userList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        final ViewHolder vh;
//        if (view == null){
        vh = new ViewHolder();
        view = LayoutInflater.from(context).inflate(R.layout.callactivity_listview_item,null);
;
        vh.ipNameTextView = (TextView)view.findViewById(R.id.call_listview_item_textview);


        vh.ipNameTextView.setText(userList.get(i).getIpName()+":"+userList.get(i).getIpAddress());

        return view;
    }

    class ViewHolder{
        TextView ipNameTextView;
    }

}
