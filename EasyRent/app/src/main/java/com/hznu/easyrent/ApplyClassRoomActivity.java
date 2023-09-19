package com.hznu.easyrent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.toast.ToastUtils;
import com.hznu.easyrent.Constants.Constants;
import com.hznu.easyrent.MyData.ClassRoom;
import com.hznu.easyrent.MyData.HttpUtil;
import com.hznu.easyrent.MyData.LogUtils;
import com.hznu.easyrent.MyData.OrderInfo;
import com.hznu.easyrent.MyUI.StatusBarUtil;
import com.hznu.easyrent.MyUI.myBase.BaseActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ApplyClassRoomActivity extends BaseActivity implements
        OnTitleBarListener, CalendarView.OnCalendarSelectListener, CalendarView.OnViewChangeListener,
        CalendarView.OnYearChangeListener, View.OnClickListener, CheckBox.OnCheckedChangeListener {
    //控件
    private LinearLayout placeHolder;

    /**
     * 日期标题栏
     */
    private LinearLayout titleDate;

    /**
     * 当天具体信息的容器
     */
    private LinearLayout dateDetail;

    /**
     * 标题栏
     */
    private TitleBar titleBar;

    /**
     * 顶部标题显示的时间
     */
    private TextView mTextMonthDay;

    /**
     * 顶部标题显示的年份
     */
    private TextView mTextYear;

    /**
     * 顶部标题显示的农历
     */
    private TextView mTextLunar;

    /**
     * 日历
     */
    private CalendarView mCalendarView;

    /**
     * 日历布局
     */
    private CalendarLayout mCalendarLayout;

    /**
     * 主要控件
     * 提交信息和提交按钮
     */
    private CheckBox[] checkBoxes = new CheckBox[4];
    private EditText etReason;
    private Button btnSubmit;

    private ClassRoom selectedRoom;
    private int choices = 0;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_apply_classroom);
        //状态栏转为黑色主题
        StatusBarUtil.setStatusBarDarkTheme(this, true);
        //初始化toast
        ToastUtils.getToast();

        //获取传递的数据
        Intent intent = getIntent();
        selectedRoom = (ClassRoom) intent.getExtras().getSerializable(Constants.TRANS_CLASSROOMDATA);
        LogUtils.printLogData("selectedClassroom:" + selectedRoom.classRoom);

        //获取控件
        placeHolder = findViewById(R.id.place_holder);
        titleBar = findViewById(R.id.title_bar);
        titleDate = findViewById(R.id.title_date);
        dateDetail = findViewById(R.id.year_lunar_layout);
        mTextMonthDay = findViewById(R.id.tv_month_day);
        mTextLunar = findViewById((R.id.tv_lunar));
        mTextYear = findViewById(R.id.tv_year);
        mCalendarView = findViewById(R.id.calendar_view);
        mCalendarLayout = findViewById(R.id.calendar_layout);
        etReason = findViewById(R.id.reason_content);
        btnSubmit = findViewById(R.id.sure);
        for(int i = 0; i < 4; i++){//获取和设置多选框
            try {
                String idstr = "choice" + (i + 1);
                int id = 0;
                id = R.id.class.getField(idstr).getInt(null);
                //LogUtils.printLogData("CheckBox id::::" + id);
                checkBoxes[i] = findViewById(id);
                checkBoxes[i].setTag(i);
                checkBoxes[i].setOnCheckedChangeListener(this);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        //设置标题栏
        setPlaceHolder(placeHolder);

        //设置控件
        btnSubmit.setOnClickListener(this);
        titleBar.setOnTitleBarListener(this);
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
        mTextLunar.setText("今日");
        //日历的
        mCalendarLayout.expand();
        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnYearChangeListener(this);
    }

    /**
     * 返回按钮
     * @param v
     */
    @Override
    public void onLeftClick(View v) {
        finish();
    }

    /**
     * 多选框
     * 四个多选框相当于四个二进制位
     * 计算则是通过二进制位运算
     * @param checkBox
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton checkBox, boolean isChecked) {
        switch ((int)checkBox.getTag()){
            case 3:
                if(isChecked){
                    choices = choices | 1;
                }
                else {
                    choices = choices & 14;
                }
                break;
            case 2:
                if(isChecked){
                    choices = choices | 2;
                }
                else {
                    choices = choices & 13;
                }
                break;
            case 1:
                if(isChecked){
                    choices = choices | 4;
                }
                else {
                    choices = choices & 11;
                }
                break;
            case 0:
                if(isChecked){
                    choices = choices | 8;
                }
                else {
                    choices = choices & 7;
                }
                break;
        }
        LogUtils.printLogData("choices::::" + choices);
    }

    /**
     * 提交按钮点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sure:
                //声明必须的变量
                DecimalFormat decimalFormat = new DecimalFormat("0000");//指定为四位
                String choicesBit = decimalFormat.format(Integer.valueOf(Integer.toBinaryString(choices)));
                Calendar chooseDate = mCalendarView.getSelectedCalendar();
                String reason = etReason.getText().toString();
                String date = String.format("%tF", new Date(chooseDate.getTimeInMillis()));

                if(chooseDate.isCurrentDay()){
                    ToastUtils.show("请选择范围内日期，当天日期不可借教室");
                }
                else {
                    if (reason.isEmpty())
                        ToastUtils.show("请填写原因");
                    else {
                        LogUtils.printLogData("chooseDate:::" + date + ":::bit:::" + choicesBit);
                        insertOrderInfo(new OrderInfo(date, choicesBit, reason, selectedRoom));
                    }
                }
                break;
        }
    }

    /**
     * 日期选择事件
     * @param calendar
     * @param isClick
     */
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());

        String date = String.format("%tF", new Date(calendar.getTimeInMillis()));
        String classroom = selectedRoom.classRoom;
        queryClassroomChoices(classroom, date);

        LogUtils.printLogData("onDateSelected:  -- " + calendar.getYear() +
                "  --  " + calendar.getMonth() +
                "  -- " + calendar.getDay() +
                "  --  " + isClick + "  --   " + calendar.getScheme());
    }

    @Override
    public void onYearChange(int year) {
        mTextYear.setText(String.valueOf(year));
    }

    /**
     * 向服务器提交申请
     * @param orderInfo
     */
    private void insertOrderInfo(OrderInfo orderInfo){
        HttpUtil.insertOrderInfo(orderInfo, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LogUtils.printLogData("服务器连接失败");
                runOnUiThread(()-> ToastUtils.show("服务器连接失败，请重新提交"));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responsData = response.body().string();

                switch (responsData){
                    case "true":
                        runOnUiThread(()->{
                            ToastUtils.show("提交成功，等待审核");
                            //成功之后切换界面
                            Intent intent = new Intent(ApplyClassRoomActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        });
                        break;
                    case "false":
                        runOnUiThread(()->ToastUtils.show("提交失败，请重新提交"));
                        break;
                }
            }
        });
    }

    /**
     * 如果该日期的该时间被选过，则不可再点击
     * @param classroom
     * @param date
     */
    private void queryClassroomChoices(String classroom, String date){
        HttpUtil.queryClassroomChoices(classroom, date, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LogUtils.printLogData("查询教室选项服务器连接失败");
                runOnUiThread(()-> ToastUtils.show("服务器连接失败，请重新提交"));
            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final  String responseData = response.body().string();

                runOnUiThread(()->{
                    try {
                        JSONObject json = new JSONObject(responseData);
                        int bit = json.getInt("choices");
                        LogUtils.printLogData(":::::" + bit);
                        for(int i = 3; i >= 0; i--){
                            if((bit & 1) > 0){
                                LogUtils.printLogData("第" + (i + 1) + "项不可选");
                                checkBoxes[i].setEnabled(false);
                                checkBoxes[i].setTextColor(R.color.standard);
                            }
                            else{
                                checkBoxes[i].setEnabled(true);
                                checkBoxes[i].setTextColor(Color.BLACK);
                            }
                            bit = bit >> 1;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    @Override
    public void onViewChange(boolean isMonthView){}
    @Override
    public void onTitleClick(View v){}
    @Override
    public void onRightClick(View v){}
    @Override
    public void onCalendarOutOfRange(Calendar calendar){}
}
