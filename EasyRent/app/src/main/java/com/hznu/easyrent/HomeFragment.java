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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hjq.toast.ToastUtils;
import com.hznu.easyrent.Constants.Constants;
import com.hznu.easyrent.MyAdapter.ClassRoomAdapter;
import com.hznu.easyrent.MyData.ClassRoom;
import com.hznu.easyrent.MyData.HttpUtil;
import com.hznu.easyrent.MyData.LogUtils;
import com.hznu.easyrent.MyData.UserUtil;
import com.hznu.easyrent.MyData.gsonUtils;
import com.hznu.easyrent.MyUI.myBase.BaseFragment;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.wyt.searchbox.SearchFragment;
import com.wyt.searchbox.custom.IOnSearchClickListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HomeFragment extends BaseFragment implements OnTitleBarListener,
        IOnSearchClickListener, AbsListView.OnScrollListener,
        ListView.OnItemClickListener {
    //控件
    private LinearLayout titleBarContainer;//标题栏容器
    private ImageView titleBarBackground;//标题栏背景图
    private TitleBar titleBar;
    private ListView listView;
    private SmartRefreshLayout smartRefreshLayout;//下拉刷新
    private SearchFragment searchFragment;//搜索框

    //Params设置布局用
    RelativeLayout.LayoutParams titleSetting;

    //listview
    private List<ClassRoom> classRoomList = new ArrayList<>();
    private ClassRoomAdapter classRoomAdapter;

    private Context context;

    //记录滑动高度
    private int scrollHeight;

    //更新ui避免在子线程中更新
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){

        public void handleMessage(Message msg){
            switch (msg.what){
                case 0:
                    ToastUtils.show("连接服务器失败");
                    break;
                case 1:
                    classRoomAdapter = new ClassRoomAdapter(context, R.layout.layout_classroom_list, classRoomList);
                    listView.setAdapter(classRoomAdapter);
                    //结束刷新动画
                    smartRefreshLayout.finishRefresh();
                    break;
                case 2:
                    ToastUtils.show("查询所有教室失败");
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        context = getContext();
        //设置主题
        transTitle("主页");

        //初始化列表
        initClassRooms();

        //获取控件
        smartRefreshLayout = rootView.findViewById(R.id.smart_refresh_layout);
        titleBarContainer = rootView.findViewById(R.id.title_bar);
        titleBarBackground = rootView.findViewById(R.id.title_background);
        titleBar = (TitleBar) titleBarContainer.getChildAt(0);
        listView = rootView.findViewById(R.id.home_class_room_list);

        //设置标题栏
        setPlaceHolder(titleBarContainer);
        //设置搜索框
        searchFragment = SearchFragment.newInstance();//实例化

        /**
         *设置下拉刷新
         */
        smartRefreshLayout.setDragRate(0.3f);
        smartRefreshLayout.setOnRefreshListener(refreshLayout ->{
            initClassRooms();
            //最多刷新五秒，五秒后自动结束
            smartRefreshLayout.finishRefresh(5000);
        });

        /*设置监听*/
        //标题栏容器加载完成后设置背景图片大小
        titleBar.setOnTitleBarListener(this);//标题栏动作监听
        searchFragment.setOnSearchClickListener(this);//搜索框动作监听
        titleBarContainer.post(()->{//容器加载完成后设置
            titleSetting = new RelativeLayout.LayoutParams(titleBarContainer.getWidth(), titleBarContainer.getHeight());
            titleBarBackground.setLayoutParams(titleSetting);
        });
        //列表视图设置滚动监听
        listView.setOnScrollListener(this);
        listView.setOnItemClickListener(this);
        listView.setEmptyView(rootView.findViewById(R.id.empty_list));//当列表为空时显示

        return rootView;
    }

    private void initClassRooms(){
        queryClassRoom();
    }

    private void queryClassRoom(){//查询所有教室
        HttpUtil.queryClassRoom(new Callback() {
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
                    if(json.getString("status").equals("success")) {
                        classRoomList = gsonUtils.toClassWithGson(json.getString("data"), ClassRoom.class);
                        msg.what = 1;
                        handler.sendMessage(msg);
                    }
                    else{
                        LogUtils.printLogData("查询所有教室 失败");
                        msg.what = 2;
                        handler.sendMessage(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void queryClassRoom(String str){//查询需要的教室
        HttpUtil.queryClassRoom(str, new Callback() {
            Message msg = new Message();
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                msg.what = 1;
                handler.sendMessage(msg);
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();
                try {
                    JSONObject json = new JSONObject(responseData);
                    classRoomList = gsonUtils.toClassWithGson(json.getString("data"), ClassRoom.class);
                    msg.what = 1;
                    handler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override//标题栏的监听器动作
    public void onRightClick(View v) {
        //打开搜索框
        searchFragment.showFragment(this.getFragmentManager(), SearchFragment.TAG);
    }

    @Override//搜索框的监听器动作
    public void OnSearchClick(String keyword) {//该监听可以做出需要的动作并记录历史
        queryClassRoom(keyword);
    }

    @Override//listview的监听器动作
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        View v = view.getChildAt(0);
        if(firstVisibleItem == 0){//titleBar背景图渐进改变
            if(v != null){
                int top = -v.getTop();
                scrollHeight = v.getHeight();
                titleBarBackground.setAlpha(1f - (float) top / scrollHeight);
                LogUtils.printLogData("====titleBackground:" + titleBarBackground.getAlpha());
                LogUtils.printLogData("=====localy:" + top);
                LogUtils.printLogData("=====scrol:" + scrollHeight);
                LogUtils.printLogData("====item:" + firstVisibleItem);
            }
        }
        else {//其他位置的则将背景图完全透明
            titleBarBackground.setAlpha(0f);
        }
    }

    /**
     * listview项点击事件
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ClassRoom selectedClassRoom = classRoomList.get(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.TRANS_CLASSROOMDATA, selectedClassRoom);

        if(UserUtil.getUserType() != 0) {
            Intent intent = new Intent(context, ApplyClassRoomActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }else{
            Intent intent = new Intent(context, AdminClassroomActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {}
    @Override
    public void onLeftClick(View v) {}
    @Override
    public void onTitleClick(View v) {}

}
