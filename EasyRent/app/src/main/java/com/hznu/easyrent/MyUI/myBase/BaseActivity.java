package com.hznu.easyrent.MyUI.myBase;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.hznu.easyrent.MyData.CollectorActivity;
import com.hznu.easyrent.Constants.ForceOfflineType;
import com.hznu.easyrent.LoginActivity;
import com.hznu.easyrent.MyData.UserUtil;
import com.hznu.easyrent.MyUI.StatusBarUtil;

public abstract class BaseActivity extends AppCompatActivity{

    private ForceOfflineReceiver receiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        CollectorActivity.addActivity(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        //广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ForceOfflineType.JUST_LOGOUT);
        intentFilter.addAction(ForceOfflineType.CHANGE_LOGOUT);
        receiver = new ForceOfflineReceiver();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause(){
        if(receiver != null){
            unregisterReceiver(receiver);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        CollectorActivity.removeActivity(this);
    }

    @Override
    public void setContentView(int layoutResID){
        super.setContentView(layoutResID);
        //ActionBar透明
        getSupportActionBar().hide();

        //沉浸式状态栏
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        //需要在setContentView之后才可以调用 setRootViewFitsSystemWindows
        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        //StatusBarUtil.setRootViewFitsSystemWindows(this,true);
        //设置状态栏文字和图标颜色避免看不清楚
        StatusBarUtil.setStatusBarDarkTheme(this, false);
    }

    public void setPlaceHolder(LinearLayout placeHolder){
        placeHolder.setPadding(0,StatusBarUtil.getStatusBarHeight(this),0,0);
    }

    class ForceOfflineReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(final Context context, Intent intent){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            switch (intent.getAction()){
                case ForceOfflineType.JUST_LOGOUT:
                    builder.setMessage("执意退出？");
                    builder.setCancelable(false);
                    builder.setNegativeButton("取消", (dialog, which) -> {});
                    break;
                case ForceOfflineType.CHANGE_LOGOUT:
                    builder.setMessage("请重新登陆");
                    builder.setCancelable(false);
                    break;
            }
            builder.setPositiveButton("确认", (dialog, which) -> {
                CollectorActivity.finishAll();//销毁所有活动
                //清除登陆数据
                UserUtil.setUserDataLogout();
                Intent i = new Intent(context, LoginActivity.class);
                context.startActivity(i);
            });
            builder.show();
        }
    }

}
