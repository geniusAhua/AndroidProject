package com.hznu.easyrent;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.toast.ToastUtils;
import com.hznu.easyrent.Constants.Constants;
import com.hznu.easyrent.MyAdapter.OrderMsgAdapter;
import com.hznu.easyrent.MyData.ClassRoom;
import com.hznu.easyrent.MyData.HttpUtil;
import com.hznu.easyrent.MyData.LogUtils;
import com.hznu.easyrent.MyData.OrderMsg;
import com.hznu.easyrent.MyData.gsonUtils;
import com.hznu.easyrent.MyUI.myBase.BaseActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AdminClassroomActivity extends BaseActivity implements AdapterView.OnItemClickListener, OnTitleBarListener {
    /**
     * 控件
     */
    private LinearLayout placeHolder;
    private ListView listView;
    private SmartRefreshLayout smartRefreshLayout;//下拉刷新
    private TitleBar titleBar;

    /**
     * 列表项
     */
    private List<OrderMsg> orderMsgList = new ArrayList<>();
    private OrderMsgAdapter orderMsgAdapter;

    /**
     * 传递的教室数据
     */
    private ClassRoom selectedClassroom;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_msg);

        //获取传递的数据
        Intent intent = getIntent();
        selectedClassroom = (ClassRoom)intent.getSerializableExtra(Constants.TRANS_CLASSROOMDATA);

        //获取控件
        placeHolder = findViewById(R.id.place_holder);
        smartRefreshLayout = findViewById(R.id.smart_refresh_layout);
        listView = findViewById(R.id.order_msg_list);
        titleBar = findViewById(R.id.title_bar);

        //设置占位符
        setPlaceHolder(placeHolder);

        /**
         * 设置下拉刷新
         */
        smartRefreshLayout.setDragRate(0.3f);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            initOrderMsgs();
            //最多刷新五秒，五秒后结束
            smartRefreshLayout.finishRefresh(5000);
        });

        //初始化列表项
        initOrderMsgs();

        //列表视图设置
        listView.setEmptyView(findViewById(R.id.empty_list));//列表为空时显示
        listView.setOnItemClickListener(this);

        //设置标题栏
        titleBar.setLeftIcon(R.mipmap.back_btn);
        titleBar.setLeftTitle("返回");
        titleBar.setOnTitleBarListener(this);
    }

    /**
     * 重新打开则进行刷新
     */
    @Override
    public void onResume(){
        super.onResume();
        initOrderMsgs();
    }

    private void initOrderMsgs(){
        queryOrderInfo(selectedClassroom.classRoom);
    }

    private void queryOrderInfo(String classroom){
        HttpUtil.queryOrderInfo(classroom, new Callback() {
            Message msg = new Message();
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                LogUtils.printLogData("根据教室查询失败");
                runOnUiThread(()-> ToastUtils.show("连接服务器失败"));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();

                runOnUiThread(()->{
                    try {
                        JSONObject json = new JSONObject(responseData);
                        if(json.getString("info").equals("null")){
                            LogUtils.printLogData("未查到申请");
                            ToastUtils.show("未查询到数据");
                        }
                        else{
                            orderMsgList = gsonUtils.toClassWithGson(json.getString("data"), OrderMsg.class);

                            orderMsgAdapter = new OrderMsgAdapter(AdminClassroomActivity.this, R.layout.layout_msg_list, orderMsgList);
                            listView.setAdapter(orderMsgAdapter);
                            //结束刷新动画
                            smartRefreshLayout.finishRefresh();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    /**
     * 列表项点击事件
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        OrderMsg selectedOrderMsg = orderMsgList.get(position);

        Intent intent = new Intent(AdminClassroomActivity.this, OrderFormActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.TRANS_ORDERMSG, selectedOrderMsg);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    /**
     * 标题栏返回按钮事件
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
