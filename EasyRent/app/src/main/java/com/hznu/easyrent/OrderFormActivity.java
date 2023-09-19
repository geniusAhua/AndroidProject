package com.hznu.easyrent;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.toast.ToastUtils;
import com.hznu.easyrent.Constants.Constants;
import com.hznu.easyrent.MyData.HttpUtil;
import com.hznu.easyrent.MyData.LogUtils;
import com.hznu.easyrent.MyData.OrderMsg;
import com.hznu.easyrent.MyData.UserUtil;
import com.hznu.easyrent.MyUI.myBase.BaseActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class OrderFormActivity extends BaseActivity implements View.OnClickListener, OnTitleBarListener {

    /**
     * 控件
     */
    private LinearLayout placeHolder;
    private LinearLayout orderBtnGroupView;//按钮组
    private TitleBar titleBar;//标题栏
    private ImageView orderSexPic;//性别图标
    private TextView orderName;//申请者姓名
    private TextView orderStuId;//申请者学号
    private TextView orderCollege;
    private TextView orderDepartment;
    private TextView orderClass;
    private TextView orderClassroom;
    private TextView orderDate;//申请的日期
    private TextView orderTime;//申请的时间
    private TextView orderReason;//申请的原因
    private Button applyBtn;//同意按钮
    private Button refuseBtn;//拒绝按钮

    /**
     * 申请者信息
     */
    private String dataName;
    private String dataSex;
    private String dataStuId;
    private String dataCollege;
    private String dataDepartment;
    private String dataClass;

    /**
     * 传递的数据
     */
    private OrderMsg selectedOrderMsg;

    //是否在当天之后
    boolean isAfterToday = true;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_form);

        //获取传递的数据
        Intent intent = getIntent();
        selectedOrderMsg = (OrderMsg) intent.getExtras().getSerializable(Constants.TRANS_ORDERMSG);
        LogUtils.printLogData("选择的申请表是：" + selectedOrderMsg.classRoom);

        //获取当天的时间
        Date today = new Date(System.currentTimeMillis());
        //获取申请表选择的日期
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date selectedDate = df.parse(selectedOrderMsg.orderDate);
            if(today.after(selectedDate)){
                isAfterToday = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //获取控件
        placeHolder = findViewById(R.id.place_holder);
        orderBtnGroupView = findViewById(R.id.order_btn_group);
        titleBar = findViewById(R.id.title_bar);
        orderSexPic = findViewById(R.id.sex_pic);
        orderName = findViewById(R.id.order_name);
        orderStuId = findViewById(R.id.order_stuId);
        orderCollege = findViewById(R.id.order_college);
        orderDepartment = findViewById(R.id.order_department);
        orderClass = findViewById(R.id.order_class);
        orderClassroom = findViewById(R.id.order_classroom);
        orderDate = findViewById(R.id.order_date);
        orderTime = findViewById(R.id.order_time);
        orderReason = findViewById(R.id.order_reason);
        applyBtn = findViewById(R.id.apply_btn);
        refuseBtn = findViewById(R.id.refuse_btn);

        //设置标题栏
        setPlaceHolder(placeHolder);

        //获取用户信息
        if(UserUtil.getUserType() == 1){
            /**
             * 普通用户查询直接显示申请者的信息
             */
            dataSex = UserUtil.getUserSex();
            dataName = UserUtil.getUserName();
            dataStuId = UserUtil.getUserId();
            dataCollege = UserUtil.getUserCollege();
            dataDepartment = UserUtil.getUserDepartment();
            dataClass = UserUtil.getUserClass();

            //显示信息
            orderName.setText(dataName);
            orderStuId.setText(dataStuId);
            orderCollege.setText(dataCollege);
            orderDepartment.setText(dataDepartment);
            orderClass.setText(dataClass);
            if(dataSex.equals("男")){
                orderSexPic.setImageResource(R.drawable.male);
            }
            else{
                orderSexPic.setImageResource(R.drawable.female);
            }
        }else {
            /**
             * 管理员需要通过后台获取申请者数据
             */
            queryStudentInfo(selectedOrderMsg.stuId);
        }
        //显示用户信息
        orderClassroom.setText(selectedOrderMsg.classRoom);
        orderDate.setText(selectedOrderMsg.orderDate);
        orderTime.setText(selectedOrderMsg.dateDetail);
        orderReason.setText(selectedOrderMsg.reason);

        //设置监听
        titleBar.setOnTitleBarListener(this);
        if(UserUtil.getUserType() == 0 && isAfterToday) {
            applyBtn.setOnClickListener(this);
            refuseBtn.setOnClickListener(this);
        }
        else{
            //按钮对普通用户不可见
            orderBtnGroupView.setVisibility(View.GONE);
        }
    }

    public void queryStudentInfo(String stuId){
        HttpUtil.queryStudentInfo(stuId, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                failToConnect("查询学生连接服务器失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();

                try {
                    JSONObject json = new JSONObject(responseData);
                    if(json.getString("status").equals("success")){
                        dataName = json.getString("name");
                        dataSex = json.getString("sex");
                        dataCollege = json.getString("college");
                        dataDepartment = json.getString("department");
                        dataClass = json.getString("class");
                        dataStuId = selectedOrderMsg.stuId;

                        orderName.setText(dataName);
                        orderStuId.setText(dataStuId);
                        orderCollege.setText(dataCollege);
                        orderDepartment.setText(dataDepartment);
                        orderClass.setText(dataClass);
                        if(dataSex.equals("男")){
                            orderSexPic.setImageResource(R.drawable.male);
                        }
                        else{
                            orderSexPic.setImageResource(R.drawable.female);
                        }
                    }
                    else{
                        //查询失败则退出
                        ToastUtils.show("查询学生信息失败");
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateOrderInfo(String orderId, int status){
        HttpUtil.updateOrderInfo(orderId, status, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                failToConnect("更新申请表连接服务器失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();

                runOnUiThread(()->{
                    if(responseData.equals("true")){
                        LogUtils.printLogData("修改申请表成功");
                        ToastUtils.show("修改成功");
                        finish();
                    }
                    else{
                        LogUtils.printLogData("修改申请表失败");
                        ToastUtils.show("修改失败,请重新尝试");
                    }

                });
            }
        });
    }

    private void failToConnect(String msg){
        LogUtils.printLogData(msg);
        runOnUiThread(()->{
            ToastUtils.show("服务器连接失败");
        });
    }

    /**
     * 同意和拒绝按钮时间
     * 1同意
     * 0拒绝
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.apply_btn:
                updateOrderInfo(selectedOrderMsg.orderMsgId, 1);
                break;
            case R.id.refuse_btn:
                updateOrderInfo(selectedOrderMsg.orderMsgId, 0);
                break;
        }
    }

    /**
     * 返回
     * @param v
     */
    @Override
    public void onLeftClick(View v) {
        finish();
    }

    @Override
    public void onTitleClick(View v){}
    @Override
    public void onRightClick(View v){}
}
