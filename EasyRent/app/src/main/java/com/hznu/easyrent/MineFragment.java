package com.hznu.easyrent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hznu.easyrent.Constants.Constants;
import com.hznu.easyrent.Constants.ForceOfflineType;
import com.hznu.easyrent.MyData.UserUtil;
import com.hznu.easyrent.MyUI.myBase.BaseFragment;
import com.kcode.bottomlib.BottomDialog;

public class MineFragment extends BaseFragment implements View.OnClickListener {

    private Context context;
    //用户控件
    private ImageView headPic;
    private TextView userName;
    private TextView userSex;
    private TextView userCollege;
    private TextView userDepartment;
    private ImageView sexPic;
    private LinearLayout userSetting;
    private LinearLayout placeHolder;//状态栏占位符，为了更好地沉浸效果
    private Button changePWD;
    private Button logout;
    //用户数据

    private String WEB_TAG = Constants.WEB_TAG;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_mine, container, false);
        context = getActivity();
        //初始化toastUtil
        //ToastUtils.init(((Activity)context).getApplication());

        //绑定控件
        changePWD = rootView.findViewById(R.id.change_pwd);
        logout = rootView.findViewById(R.id.logout);//从相册选取图片
        userName = rootView.findViewById(R.id.user_name);//用户名称
        userSex = rootView.findViewById(R.id.user_sex);//用户性别
        userCollege = rootView.findViewById(R.id.user_college);//学院
        userDepartment = rootView.findViewById(R.id.user_department);//专业
        sexPic = rootView.findViewById(R.id.sex_pic);//性别图标
        userSetting = rootView.findViewById(R.id.user_setting);//用户设置
        placeHolder = rootView.findViewById(R.id.place_holder);//占位符，沉浸式状态栏
        headPic = rootView.findViewById(R.id.head_pic);//头像

        //注册事件
        changePWD.setOnClickListener(this);
        logout.setOnClickListener(this);
        userSetting.setOnClickListener(this);

        //显示用户信息
        InitInfo();

        //设置占位符
        setPlaceHolder(placeHolder);
        //设置主题
        transTitle("我的");

        return rootView;
    }

    private void InitInfo(){//显示用户信息
        String sex = UserUtil.getUserSex();

        userName.setText(UserUtil.getUserName());
        userSex.setText(sex);
        userCollege.setText(UserUtil.getUserCollege());
        userDepartment.setText(UserUtil.getUserDepartment());
        UserUtil.setUserHeadPicUI(headPic);
        if(sex.equals("男")){
            sexPic.setImageResource(R.drawable.male);
        }
        else{
            sexPic.setImageResource(R.drawable.female);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        //如果头像更新了需要更换
        InitInfo();//信息需要重新加载
    }

    @Override
    public void onHiddenChanged(boolean isHidden){
        super.onHiddenChanged(isHidden);
        //每次重新载入界面都重新加载信息
        InitInfo();
    }

    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.logout:
                //退出登录，退出广播，修改登录状态
                showDialogFragment();
                break;
            case R.id.user_setting:
                Intent intent1 = new Intent(context, UserInfoAcitvity.class);
                startActivity(intent1);
                break;
            case R.id.change_pwd:
                Intent intent2 = new Intent(context, ChangePWDActivity.class);
                startActivity(intent2);
                break;
        }
    }

    //弹出底部弹窗
    private void showDialogFragment(){
        BottomDialog dialog = BottomDialog.newInstance("", "点错了", new String[]{"退出登录"});
        dialog.show(getChildFragmentManager(), "dialog");
        dialog.setListener(i -> {
            if(i == 0){
                Intent intent = new Intent(ForceOfflineType.JUST_LOGOUT);
                context.sendBroadcast(intent);
                Log.d(Constants.WEB_TAG, "发送下线广播");
            }
        });
    }

}
