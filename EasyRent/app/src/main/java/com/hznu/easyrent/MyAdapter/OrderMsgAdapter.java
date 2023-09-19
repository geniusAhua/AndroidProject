package com.hznu.easyrent.MyAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hznu.easyrent.MyData.LogUtils;
import com.hznu.easyrent.MyData.OrderMsg;
import com.hznu.easyrent.MyData.UserUtil;
import com.hznu.easyrent.R;

import java.util.List;

public class OrderMsgAdapter extends ArrayAdapter<OrderMsg> {
    /**
     * 布局文件id
     */
    private int resourceId;

    public OrderMsgAdapter(@NonNull Context context, int resource, List<OrderMsg> object) {
        /**
         * 下次这些部分要完全手打，系统默认的是没有object参数的
         */
        super(context, resource, object);
        this.resourceId = resource;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        OrderMsg orderMsg = getItem(position);
        View view;
        ViewHolder viewHolder;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.orderName = view.findViewById(R.id.order_name);
            viewHolder.orderClassroom = view.findViewById(R.id.order_classroom);
            viewHolder.orderDate = view.findViewById(R.id.order_date);
            viewHolder.orderReason = view.findViewById(R.id.order_reason);
            viewHolder.newMsgText = view.findViewById(R.id.new_msg_text);
            viewHolder.orderSubmitTime = view.findViewById(R.id.order_submit_time);
            view.setTag(viewHolder);
            LogUtils.printLogData("加载列表：" + orderMsg.classRoom);
        }
        else{
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        viewHolder.orderName.setText(orderMsg.stuId);
        viewHolder.orderClassroom.setText(orderMsg.classRoom);
        viewHolder.orderDate.setText(orderMsg.orderDate);
        viewHolder.orderReason.setText(orderMsg.reason);
        viewHolder.orderSubmitTime.setText(orderMsg.submitTime);
        switch (orderMsg.status){
            case -1:
                viewHolder.newMsgText.setText("待审核");
                viewHolder.newMsgText.setTextColor(Color.RED);
                break;
            case 0:
                if(UserUtil.getUserType() == 0){
                    viewHolder.newMsgText.setText("已拒绝");
                    viewHolder.newMsgText.setTextColor(R.color.standard);
                }
                else{
                    viewHolder.newMsgText.setText("审核失败");
                    viewHolder.newMsgText.setTextColor(R.color.standard);
                }
                break;
            case 1:
                if(UserUtil.getUserType() == 0){
                    viewHolder.newMsgText.setText("已确认");
                    viewHolder.newMsgText.setTextColor(R.color.standard);
                }
                else{
                    viewHolder.newMsgText.setText("审核成功");
                    viewHolder.newMsgText.setTextColor(R.color.standard);
                }
                break;
        }
        return view;
    }

    class ViewHolder{
        TextView orderName;
        TextView orderClassroom;
        TextView orderDate;
        TextView orderReason;
        TextView newMsgText;
        TextView orderSubmitTime;
    }
}
