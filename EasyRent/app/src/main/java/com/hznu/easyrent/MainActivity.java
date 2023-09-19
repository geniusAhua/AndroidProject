package com.hznu.easyrent;

import android.os.Bundle;

import com.hjq.toast.ToastUtils;
import com.hznu.easyrent.MyInterface.OnFragmentInteractionListener;
import com.hznu.easyrent.MyUI.myBase.BaseActivity;
import com.hznu.easyrent.MyUI.BottomBar;

public class MainActivity extends BaseActivity implements OnFragmentInteractionListener {

    private BottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化toastUtil
        ToastUtils.init(this.getApplication());

        //获取控件
        bottomBar = findViewById(R.id.bottom_bar);
        bottomBar.setContainer(R.id.fl_content)
                .setTitleBeforeAndAfterColor(R.color.standard, R.color.cutePink)
                .addItem(HomeFragment.class,
                        "首页",
                        R.drawable.home,
                        R.drawable.home_focus)
                .addItem(MsgFragment.class,
                        "消息",
                        R.drawable.chat,
                        R.drawable.chat_focus)
                .addItem(MineFragment.class,
                        "我的",
                        R.drawable.mine,
                        R.drawable.mine_focus)
                .build();
    }

    //设置状态栏占位符
    @Override
    public void onFragmentInteraction(String title) {
        setTitle(title);
    }
}