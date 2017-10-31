package com.qttaudio.android.demo;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/1/28.
 */
public class PullDownMenu extends LinearLayout {

    // 选中之后是否需要搜索
    private boolean needSearch;

    // 下拉框
    private PopupWindow popupWindow;

    // 下拉框数据
    private List<String> mStringList;

    // 顶部位置
    private TextView tvPullDown;

    // 顶部位置显示的内容
    private String topTitle;

    private ListView listView;

    private View viewList;

    private SimpleAdapter simpleAdapter;

    private List<HashMap<String, String>> hashMaps;

    public PullDownMenu(Context context) {
        super(context);
        init();
    }

    //定义接口
    public interface selectMode {
        void onItemClick(String string);
    }
    //设置接口和adapter的方法
    private selectMode mSelectMode = null;

    public void setSelectMode(selectMode listener) {
        this.mSelectMode = listener;
    }

    public PullDownMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullDownMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.pull_down_menu_layout, null);
        addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        tvPullDown = (TextView) findViewById(R.id.tvPullDown);
        hashMaps = new ArrayList<HashMap<String, String>>();
//		// 模拟加载数据
//		loadData();
        tvPullDown.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    viewList = LayoutInflater.from(getContext()).inflate(R.layout.pop_menulist, null);
                    listView = (ListView) viewList.findViewById(R.id.menulist);
                    simpleAdapter = new SimpleAdapter(getContext(), hashMaps, R.layout.pop_menuitem,
                            new String[]{"item"}, new int[]{R.id.menuitem});
                    listView.setAdapter(simpleAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String stringItem = hashMaps.get(position).get("item");
                            tvPullDown.setText(stringItem);
                            if (popupWindow != null && popupWindow.isShowing()) {
                                popupWindow.dismiss();
                            }
                            // 选择菜单后进行的操作
                            search();
                            mSelectMode.onItemClick(stringItem);

                            System.out.println(stringItem);
                        }
                    });
                    popupWindow = new PopupWindow(viewList, tvPullDown.getWidth(), LayoutParams.WRAP_CONTENT);
                    ColorDrawable cd = new ColorDrawable(-0000);
                    popupWindow.setBackgroundDrawable(cd);
                    popupWindow.setAnimationStyle(R.style.PopupAnimation);
                    popupWindow.update();
                    popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
                    popupWindow.setTouchable(true);
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.setFocusable(true);

                    int topHeight = tvPullDown.getBottom();
                    popupWindow.showAsDropDown(tvPullDown, 0, 0);
                    popupWindow.setTouchInterceptor(new OnTouchListener() {

                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                                popupWindow.dismiss();
                                return true;
                            }
                            return false;
                        }
                    });
                }
            }
        });
    }

    private void search(){

    }

    public void setData(String title, List<String> strings, boolean needSearch) {
        this.topTitle = title;
        this.mStringList = strings;
        this.needSearch = needSearch;
        tvPullDown.setText(topTitle);
        if (hashMaps !=null){
            hashMaps.clear();
        }
        for (String string : mStringList) {
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("item", string);
            hashMaps.add(hashMap);
        }
    }

}
