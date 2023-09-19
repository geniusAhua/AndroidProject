package com.hznu.easyrent.MyData;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

import com.hznu.easyrent.Constants.ServType;

import java.io.Serializable;


public class ClassRoom implements Serializable {
    public String classRoom;
    public String classRoomSeat;
    public String imagPath;

    public void setImagPath(String imagPath){
        this.imagPath = imagPath;
    }

    public void setClassRoom(String classRoom){
        this.classRoom = classRoom;
    }

    public void setClassRoomSeat(String classRoomSeat){
        this.classRoomSeat = classRoomSeat;
    }

    public String getImagPath(){
        return imagPath;
    }

    public String getClassRoom(){
        return classRoom;
    }

    public String getClassRoomSeat(){
        return classRoomSeat;
    }

    public void setRoomPic(ImageView imageView){
        Handler handler = new Handler(msg->{
            switch (msg.what) {
                case 2://成功
                    byte[] b = (byte[]) msg.obj;
                    //转换成bitmap
                    Bitmap roomBitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                    imageView.setImageBitmap(roomBitmap);
            }
            return false;
        });

        HttpUtil.loadPicOnServe(ServType.LOAD_CLASSROOM, imagPath, handler);
    }
}
