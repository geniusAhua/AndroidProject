package com.hznu.easyrent;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hznu.easyrent.Constants.Constants;
import com.hznu.easyrent.MyData.HttpUtil;
import com.hznu.easyrent.MyData.UserUtil;
import com.hznu.easyrent.MyUI.myBase.BaseActivity;
import com.hznu.easyrent.MyUI.MyProgressBtn;
import com.hznu.easyrent.MyUI.StatusBarUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private MyProgressBtn loginBtn;
    private LinearLayout loginContent;//用于切换界面动画
    private EditText accountET;
    private EditText passwordET;
    private TextView register;
    //UI相关
    private Handler handler;
    private Animator animator;//动画
    //设置数据
    private SharedPreferences preferences;
    //debug用
    private String WEB_TAG = Constants.WEB_TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //重新设置一下状态栏
        StatusBarUtil.setStatusBarDarkTheme(this, true);
        //绑定控件
        loginBtn = findViewById(R.id.login_btn);
        accountET = findViewById(R.id.login_account);
        passwordET = findViewById(R.id.login_pwd);
        loginContent = findViewById(R.id.login_content);
        register = findViewById(R.id.register);
        //按钮及界面动画专用
        handler = new Handler();

        //绑定事件
        loginContent.getBackground().setAlpha(0);
        loginContent.setVisibility(View.VISIBLE);
        loginBtn.setOnClickListener(this);
        register.setOnClickListener(this);

        //SharedPreferences存储
        UserUtil.Init(this);

        //填写登录信息
        String account = UserUtil.getUserAccount();
        String password = UserUtil.getUserPassword();
        accountET.setText(account);
        passwordET.setText(password);

        //根据登录状态进行登录操作
        if(UserUtil.isLogin()) {//判断是否登录
            //通过post，让按钮加载完成后再执行动作操作
            loginBtn.post(() -> {
                loginBtn.startAnim();//开启动画
                loginWithOkHttp(account, password);
            });
        }
    }

    @Override
    public void finish(){
        super.finish();
        //取消出场动画
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(animator != null)
            animator.cancel();
        loginContent.getBackground().setAlpha(0);
        loginBtn.regainBackground();
        passwordET.setText("");
    }

    private void gotoNew(MyProgressBtn btn){
        btn.gotoNew();
        //声明并设置intent
        final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        //动画开始的位置
        int xc = (btn.getLeft() + btn.getRight()) / 2;
        int yc = (btn.getTop() + btn.getBottom()) / 2;

        animator = ViewAnimationUtils.createCircularReveal(loginContent, xc, yc, 0, 1111);
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
        //动画效果设置
        animator.start();
        loginContent.getBackground().setAlpha(255);
        //关闭登录界面，进入后点击返回不会回到主界面
        finish();
    }

    private void backtoOrigin(MyProgressBtn btn){
        btn.backtoOrigin();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_btn:
                //开始按钮的动画
                loginBtn.startAnim();
                //连接服务器
                try {
                    String loginAccount = accountET.getText().toString();
                    String loginPassword = passwordET.getText().toString();
                    if(loginAccount.isEmpty() || loginPassword.isEmpty())
                        throw new Exception("请输入账号或密码");
                    loginWithOkHttp(loginAccount, loginPassword);
                }
                catch (Exception e){
                    backtoOrigin(loginBtn);
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.register:
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }

    //登录实现
    private void loginWithOkHttp(String account, String password){
        HttpUtil.loginWithOkHttp(account, password, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //异常处理
                Log.d(WEB_TAG, "connect:failure");
                runOnUiThread(()-> {
                    backtoOrigin(loginBtn);
                    Toast.makeText(LoginActivity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                //得到服务器返回的具体内容
                final String responseData = response.body().string();

                Log.d(WEB_TAG, "LoginData:" + responseData);

                //这里是异步处理，Android里不允许子线程更新UI所以需要使用runOnUiThread
                //因为是异步所以不用在新开线程
                runOnUiThread(() -> {
                    String status = null;
                    try {
                        JSONObject json = new JSONObject(responseData);
                        status = (String) json.get(Constants.JSON_STATUS);

                        //登录设置
                        if(status.equals("success")){
                            //成功则记录账号信息
                            UserUtil.setUserData(account, password, json);
                            //更新界面
                            handler.postDelayed(() -> gotoNew(loginBtn), 400);
                            //弹出提醒
                            Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            backtoOrigin(loginBtn);
                            Toast.makeText(LoginActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                });
            }
        });
    }
}