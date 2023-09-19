package com.hznu.easyrent;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hznu.easyrent.Constants.Constants;
import com.hznu.easyrent.MyData.HttpUtil;
import com.hznu.easyrent.MyData.UserUtil;
import com.hznu.easyrent.MyUI.myBase.BaseActivity;
import com.hznu.easyrent.MyUI.MyProgressBtn;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends BaseActivity implements View.OnClickListener, OnTitleBarListener {

    private EditText registAccount;
    private EditText registPassword;
    private MyProgressBtn registBtn;
    private LinearLayout registContent;
    private LinearLayout placeHolder;
    private TitleBar titleBar;

    private Handler handler = new Handler();
    private Animator animator;

    private String WEB_TAG = Constants.WEB_TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //绑定控件
        titleBar = findViewById(R.id.title_bar);
        registAccount = findViewById(R.id.regist_account);
        registPassword = findViewById(R.id.regist_password);
        registBtn = findViewById(R.id.register);
        registContent = findViewById(R.id.regist_content);
        placeHolder = findViewById(R.id.place_holder);

        //设置控件
        setPlaceHolder(placeHolder);
        registContent.getBackground().setAlpha(0);
        registContent.setVisibility(View.VISIBLE);
        //注册监听器
        registBtn.setOnClickListener(this);
        titleBar.setOnTitleBarListener(this);
    }

    private void gotoNew(){
        int xc = (registBtn.getLeft() + registBtn.getRight()) / 2;
        int yc = (registBtn.getTop() + registBtn.getBottom()) / 2;

        registBtn.gotoNew();

        final Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);

        animator = ViewAnimationUtils.createCircularReveal(registContent, xc, yc, 0, 1111);
        animator.setDuration(300);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                handler.postDelayed(() -> {
                    startActivity(intent);
                    overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                }, 120);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        animator.start();
        registContent.getBackground().setAlpha(255);
        //关闭界面
        finish();
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(animator != null)
            animator.cancel();
        registContent.getBackground().setAlpha(0);
        registBtn.regainBackground();
    }

    @Override
    public void onClick(View view) {
        //开始动画
        registBtn.startAnim();
        //连接服务器
        try {
            String account = registAccount.getText().toString();
            String password = registPassword.getText().toString();
            if(account.isEmpty() || password.isEmpty())
                throw new Exception("请输入注册信息");
            registerWithOkHttp(account, password);
        }
        catch (Exception e){
            registBtn.backtoOrigin();
            Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //注册实现
    private void registerWithOkHttp(String account, String password){
        HttpUtil.registerWithOkHttp(account, password, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(WEB_TAG, "connect:failure");
                runOnUiThread(()->{
                    registBtn.backtoOrigin();
                    Toast.makeText(RegisterActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //得到服务器返回内容
                final String responseData = response.body().string();
                Log.d(WEB_TAG, "registData:" + responseData);

                runOnUiThread(()->{
                    try {
                        JSONObject json = new JSONObject(responseData);
                        String status = json.getString(Constants.JSON_STATUS);
                        if (status.equals("success")) {
                            UserUtil.setUserData(account);
                            handler.postDelayed(() -> gotoNew(), 400);
                            Toast.makeText(RegisterActivity.this, json.getString(Constants.JSON_MSG), Toast.LENGTH_SHORT).show();
                        } else {
                            registBtn.backtoOrigin();
                            Toast.makeText(RegisterActivity.this, json.getString(Constants.JSON_MSG), Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    @Override
    public void onLeftClick(View v) {
        finish();
    }
    @Override
    public void onTitleClick(View v){}
    @Override
    public void onRightClick(View v){}
}
