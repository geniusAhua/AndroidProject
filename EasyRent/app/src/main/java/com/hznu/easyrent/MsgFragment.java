package com.hznu.easyrent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.hjq.toast.ToastUtils;
import com.hznu.easyrent.Constants.Constants;
import com.hznu.easyrent.MyAdapter.OrderMsgAdapter;
import com.hznu.easyrent.MyData.HttpUtil;
import com.hznu.easyrent.MyData.LogUtils;
import com.hznu.easyrent.MyData.OrderMsg;
import com.hznu.easyrent.MyData.gsonUtils;
import com.hznu.easyrent.MyInterface.OnFragmentInteractionListener;
import com.hznu.easyrent.MyUI.myBase.BaseFragment;
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

public class MsgFragment extends BaseFragment implements ListView.OnItemClickListener {

    /**
     * 控件
     */
    private LinearLayout placeHolder;
    private ListView listView;
    private SmartRefreshLayout smartRefreshLayout;//下拉刷新
    private OnFragmentInteractionListener mListener;

    /**
     * 列表项
     */
    private List<OrderMsg> orderMsgList = new ArrayList<>();
    private OrderMsgAdapter orderMsgAdapter;

    /**
     * 上下文，碎片获取活动的上下文
     */
    private Context context;

    /**
     * 刷新等更新UI
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    ToastUtils.show("连接服务器失败");
                    break;
                case 1:
                    orderMsgAdapter = new OrderMsgAdapter(context, R.layout.layout_msg_list, orderMsgList);
                    listView.setAdapter(orderMsgAdapter);
                    //结束刷新动画
                    smartRefreshLayout.finishRefresh();
                    break;
                case 2:
                    ToastUtils.show("未查询到数据");
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_msg, container, false);
        context = getContext();
        //设置主题
        transTitle("消息");

        //获取控件
        placeHolder = rootView.findViewById(R.id.place_holder);
        smartRefreshLayout = rootView.findViewById(R.id.smart_refresh_layout);
        listView = rootView.findViewById(R.id.order_msg_list);

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
        listView.setEmptyView(rootView.findViewById(R.id.empty_list));//列表为空时显示
        listView.setOnItemClickListener(this);

        return rootView;
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
        queryOrderInfo();
    }

    private void queryOrderInfo(){
        HttpUtil.queryOrderInfo(new Callback() {
            Message msg = new Message();
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                msg.what = 0;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                try {
                    JSONObject json = new JSONObject(responseData);
                    if(json.getString("info").equals("null")){
                        LogUtils.printLogData("未查到申请");
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }
                    else{
                        orderMsgList = gsonUtils.toClassWithGson(json.getString("data"), OrderMsg.class);
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

        Intent intent = new Intent(context, OrderFormActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.TRANS_ORDERMSG, selectedOrderMsg);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
