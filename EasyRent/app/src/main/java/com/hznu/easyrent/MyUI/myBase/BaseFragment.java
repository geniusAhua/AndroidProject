package com.hznu.easyrent.MyUI.myBase;

import android.content.Context;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.hznu.easyrent.MyInterface.OnFragmentInteractionListener;
import com.hznu.easyrent.MyUI.StatusBarUtil;

public class BaseFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private String title;
    private Context context;

    //同活动通信
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        this.context = context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public OnFragmentInteractionListener getmListener() {
        return mListener;
    }

    //设置占位符
    public void setPlaceHolder(LinearLayout placeHolder){
        int height = StatusBarUtil.getStatusBarHeight(context);
        placeHolder.setPadding(0,height,0,0);
    }

//    //设置占位符
//    public void setPlaceHolder(RelativeLayout placeHolder){
//        int height = StatusBarUtil.getStatusBarHeight(context);
//        placeHolder.setPadding(0,height,0,0);
//    }

    //传递标题用于debug
    public void transTitle(String title){
        this.title = title;
    }

    //隐藏改变
    @Override
    public void onHiddenChanged(boolean isHidden){
        super.onHiddenChanged(isHidden);
        mListener.onFragmentInteraction(title);
    }
    //获取context
    public Context getContext(){
        return context;
    }

    /**
     * dip转换px
     **/
    public int dip2px(int dip) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }
}
