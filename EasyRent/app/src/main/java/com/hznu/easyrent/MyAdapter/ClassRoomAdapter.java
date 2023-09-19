package com.hznu.easyrent.MyAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hznu.easyrent.MyData.ClassRoom;
import com.hznu.easyrent.R;

import java.util.List;

public class ClassRoomAdapter extends ArrayAdapter<ClassRoom> {

    private int resourceId;

    public ClassRoomAdapter(@NonNull Context context, int resourceId, List<ClassRoom> objects) {
        super(context, resourceId, objects);
        this.resourceId = resourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ClassRoom classRoom = getItem(position);
        View view;
        ViewHolder viewHolder;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.IV_classRoom = view.findViewById(R.id.classroom_pic);
            viewHolder.TV_classRoom = view.findViewById(R.id.classroom);
            viewHolder.TV_classRoomSeat = view.findViewById(R.id.classroom_seat);
            view.setTag(viewHolder);//将viewHolder存储在view中
        }
        else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        classRoom.setRoomPic(viewHolder.IV_classRoom);
        viewHolder.TV_classRoom.setText(classRoom.classRoom);
        viewHolder.TV_classRoomSeat.setText(classRoom.classRoomSeat);
        return view;
    }

    class ViewHolder{
        ImageView IV_classRoom;
        TextView TV_classRoom;
        TextView TV_classRoomSeat;
    }
}
