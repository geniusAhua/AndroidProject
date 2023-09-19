package com.hznu.easyrent;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.toast.ToastUtils;
import com.hznu.easyrent.Constants.Constants;
import com.hznu.easyrent.Constants.ForceOfflineType;
import com.hznu.easyrent.MyData.HttpUtil;
import com.hznu.easyrent.MyUI.myBase.BaseActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;

public class ChangePWDActivity extends BaseActivity implements OnTitleBarListener, View.OnClickListener{

    //控件
    private TitleBar titleBar;
    private EditText originalPWDET;
    private EditText newPWDET;
    private EditText sureNewPWDET;
    private Button nextStepBTN;
    private LinearLayout layoutOriginalPWD;
    private LinearLayout layoutNewPWD;

    //步骤标志
    private int flag = 1;

    private final String WEB_TAG = Constants.WEB_TAG;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_change_pwd);

        //设置占位符
        setPlaceHolder(findViewById(R.id.place_holder));

        //获取控件
        titleBar = findViewById(R.id.changepwd_titlebar);
        originalPWDET = findViewById(R.id.original_pwd);
        newPWDET = findViewById(R.id.new_pwd);
        sureNewPWDET = findViewById(R.id.sure_new_pwd);
        nextStepBTN = findViewById(R.id.next_step);
        layoutOriginalPWD = findViewById(R.id.layout_originalpwd);
        layoutNewPWD = findViewById(R.id.layout_newpwd);
        //设置监听
        nextStepBTN.setOnClickListener(this);
        titleBar.setOnTitleBarListener(this);//标题栏设置监听

        //初始化控件
        layoutOriginalPWD.setVisibility(View.VISIBLE);
        layoutNewPWD.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        String ori_pwd = originalPWDET.getText().toString();
        String new_pwd = newPWDET.getText().toString();
        String sure_new_pwd = sureNewPWDET.getText().toString();
        switch (flag){
            case 1:
                //第一步获取原始密码
                if(ori_pwd.isEmpty()){
                    ToastUtils.show("请输入原始密码！");
                }
                else{
                    checkPWD(ori_pwd);
                }
                break;
            case 2:
                if(!new_pwd.isEmpty()){
                    if(!sure_new_pwd.isEmpty()){
                        if(new_pwd.equals(sure_new_pwd)){
                            changePWD(new_pwd);
                        }else {
                            ToastUtils.show("两次密码不匹配！");
                        }
                    } else{
                        ToastUtils.show("请确认新密码！s");
                    }
                } else {
                    ToastUtils.show("请输入新秘密！");
                }
                break;
        }
    }

    @Override
    public void onLeftClick(View v) {
        //返回按钮
        finish();
    }

    //检查密码
    private void checkPWD(String pwd){
        HttpUtil.checkPWD(pwd, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(WEB_TAG, "检查密码连接服务器失败");
                runOnUiThread(()->{
                    ToastUtils.show("连接服务器失败");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();

                runOnUiThread(()->{
                    try {
                        JSONObject json = new JSONObject(responseData);
                        String status = json.getString(Constants.JSON_STATUS);
                        if(status.equals("success")){
                            Log.d(WEB_TAG, "检查密码无误,original:" + pwd);
                            flag = 2;
                            layoutOriginalPWD.setVisibility(View.GONE);
                            layoutNewPWD.setVisibility(View.VISIBLE);
                        }
                        else{
                            Log.d(WEB_TAG, "检查密码,密码不对,original:" + pwd);
                            ToastUtils.show("密码有误，请检查密码");
                            originalPWDET.setText("");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    //修改密码
    private void changePWD(String newPWD){
        HttpUtil.changePWD(newPWD, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(WEB_TAG, "检查密码连接服务器失败");
                runOnUiThread(()->{
                    ToastUtils.show("连接服务器失败");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();

                runOnUiThread(()->{
                    try {
                        JSONObject json = new JSONObject(responseData);
                        String status = json.getString(Constants.JSON_STATUS);
                        if(status.equals("success")){
                            Log.d(WEB_TAG, "修改成功,newPWD"+newPWD);
                            flag = 2;
                            ToastUtils.show(json.getString(Constants.JSON_MSG));
                            //修改成功，发送退出登录广播，重新登录
                            Intent intent = new Intent(ForceOfflineType.CHANGE_LOGOUT);
                            sendBroadcast(intent);
                            Log.d(Constants.WEB_TAG, "发送更新下线广播");
                        }
                        else{
                            Log.d(WEB_TAG, "修改密码失败," + json.getString(Constants.JSON_MSG));
                            ToastUtils.show("更新失败，请重新尝试");
                            newPWDET.setText("");
                            sureNewPWDET.setText("");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    @Override
    public void onTitleClick(View v) {}

    @Override
    public void onRightClick(View v) {}

}
