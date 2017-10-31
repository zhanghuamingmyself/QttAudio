package com.qttaudio.android.demo.adpter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qttaudio.android.demo.Model.AddIPModel;
import com.qttaudio.android.demo.R;

import java.util.List;

/**
 * Created by qingtiantree on 2017/4/24.
 */

public class AddIpListViewAdapter extends BaseAdapter {

    List<AddIPModel> userList;
    private Context context;





    //定义接口
    public interface ImageButtonClick {
        void onItemClick(View view, int index);
    }
    //设置接口和adapter的方法
    private ImageButtonClick mOnItemClickListener = null;

    public void setOnClickListener(ImageButtonClick listener) {
        this.mOnItemClickListener = listener;
    }

    //创建
    public AddIpListViewAdapter(Context context, List<AddIPModel> userList) {
        this.context = context;
        this.userList = userList;
        this.userList.remove(0);
    }
    @Override
    public int getCount() {
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
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final ViewHolder vh;
//        if (view == null){
                vh = new ViewHolder();
//        if (0==i) {
            view = LayoutInflater.from(context).inflate(R.layout.listview_addip_item, null);
            vh.backGround_listView = (LinearLayout) view.findViewById(R.id.listview_addip_background);
            vh.ipEditText = (EditText) view.findViewById(R.id.listview_addip_editText);
            vh.ipNameTextView = (TextView) view.findViewById(R.id.listview_addip_textView);
            vh.ipNameTextView.setGravity(Gravity.CENTER_VERTICAL);
            vh.imageView_btn = (ImageButton) view.findViewById(R.id.listview_addip_imageButton);
//        }else{
//            vh = (ViewHolder)view.getTag();
//        }
//        }else{
//
//            view = LayoutInflater.from(context).inflate(R.layout.listview_addip_item_others, null);
//
//            vh.ipEditText = (EditText) view.findViewById(R.id.listview_addip_other_editText);
//            vh.ipNameTextView = (TextView) view.findViewById(R.id.listview_addip_other_textView);
//            vh.ipNameTextView.setGravity(Gravity.CENTER_VERTICAL);
//            vh.imageView_btn = (ImageButton) view.findViewById(R.id.listview_addip_other_imageButton);
//            vh.localPortTextView = (TextView) view.findViewById(R.id.listview_addip_other_localport);
//            vh.remotePortEditText =  (EditText) view.findViewById(R.id.listview_addip_other_remoteport);
//        }
//        final String ipAddress = vh.ipEditText.getText().toString();
        vh.imageView_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ipAddress = vh.ipEditText.getText().toString();
                userList.get(i).setIpAddress(vh.ipEditText.getText().toString());

//                if ()
//                System.out.println("localip" + vh.localPortTextView.getText().toString() + "remoteip" +vh.remotePortEditText.getText());
//                userList.get(i).setLocalPort(Integer.parseInt(vh.localPortTextView.getText().toString()));
//                userList.get(i).setRemotePort(Integer.parseInt(vh.remotePortEditText.getText().toString()));
                if (ipAddress.length() > 0) {

//                    vh.remotePortEditText.setEnabled(false);
                    vh.ipEditText.setEnabled(false);
                    vh.imageView_btn.setVisibility(View.INVISIBLE);
                    if (mOnItemClickListener != null) {
                        //注意这里使用getTag方法获取数据
                        mOnItemClickListener.onItemClick(view, i);
                    }
                }
//                if (ipAddress.length() > 0){
//                    //证明输入了值
//                    //添加到数组，显示下一行
//
//                    if (mOnItemClickListener != null) {
//                        //注意这里使用getTag方法获取数据
//                        userList.get(i).setIpAddress(ipAddress);
//                        mOnItemClickListener.onItemClick(view, i);
//                    }
//
//
//                }else{
//                    //没有输入值，什么事也不做
//                }
            }
        });
//
        System.out.println("1111111111111111111111111111111111111111111111111111111");

//        if (i !=0 ){
//            if (-1 == userList.get(i).getRemotePort()){
//                vh.remotePortEditText.setText("");
//
//            }else {
//                vh.remotePortEditText.setText(Integer.toString(userList.get(i).getRemotePort()));
//            }
//            int localPort = 8998 + i * 2;
//            vh.localPortTextView.setText(Integer.toString(localPort));
//        }
        vh.ipEditText.setText(userList.get(i).getIpAddress());
        vh.ipNameTextView.setText(userList.get(i).getIpName());
        vh.imageView_btn.setVisibility(View.VISIBLE);
        vh.ipEditText.setEnabled(true);
        if (userList.get(i).getIsShowAdd()){
            vh.imageView_btn.setVisibility(View.VISIBLE);
            vh.ipEditText.setEnabled(true);
            vh.backGround_listView.setBackgroundColor(0xffffffff);
//            if (0 != i)
//                vh.remotePortEditText.setEnabled(true);
        }else{
            vh.backGround_listView.setBackgroundColor(0xf2f2f2ff);
            vh.ipEditText.setEnabled(false);
            vh.imageView_btn.setVisibility(View.INVISIBLE);
//            if (0 != i)
//                vh.remotePortEditText.setEnabled(false);
        }
        vh.ipEditText.setText(userList.get(i).getIpAddress());
        return view;
    }






//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.listview_addip, null);
////        view.setOnClickListener(this);
//        return new ViewHolder(view);
//    }
//
//
//
//    //绑定数据
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
//        final ViewHolder vh = (ViewHolder) holder;
//        vh.ipNameTextView.setText(userList.get(position).getIpName());
//        vh.ipEditText.setText(userList.get(position).getIpAddress());
//        if (userList.get(position).getIsShowAdd()){
//            vh.imageView_btn.setVisibility(View.VISIBLE);
//        }else {
//            vh.imageView_btn.setVisibility(View.INVISIBLE);
//        }
//
//       final int index;
//        index = userList.get(position).getIndex();
//
//        vh.imageView_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (vh.ipEditText.getText().toString().length() > 0){
//                    //证明输入了值
//                    //添加到数组，显示下一行
//
//                    if (mOnItemClickListener != null) {
//                        //注意这里使用getTag方法获取数据
//                        mOnItemClickListener.onItemClick(view, index);
//                    }
//
//
//                }else{
//                    //没有输入值，什么事也不做
//                }
//            }
//        });
//
//
//    }
//
//    @Override
//    public int getItemCount() {
//
//        return userList.size();
//
//    }
//
////    @Override
////    public void onClick(View v) {
////        if (mOnItemClickListener != null) {
////            //注意这里使用getTag方法获取数据
////        }
////    }



    class ViewHolder{
        LinearLayout backGround_listView;
        TextView ipNameTextView;
        ImageButton imageView_btn;
        EditText ipEditText;

//        TextView localPortTextView;
//        EditText remotePortEditText;
    }

    //设置
//    private static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView ipNameTextView;
//        ImageButton imageView_btn;
//        EditText ipEditText;
//        public ViewHolder(View itemView) {
//            super(itemView);
//            ipNameTextView = (TextView) itemView.findViewById(R.id.listview_addip_textView);
//            imageView_btn = (ImageButton) itemView.findViewById(R.id.listview_addip_imageButton);
//            ipEditText = (EditText)itemView.findViewById(R.id.listview_addip_editText);
//
//        }
//    }
}
