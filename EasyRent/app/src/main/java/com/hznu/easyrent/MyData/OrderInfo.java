package com.hznu.easyrent.MyData;

/**
 * 用于申请的，是准备添加进去的
 */
public class OrderInfo {
    public String date;
    public String reason;
    public String choices;
    public ClassRoom selectedRoom;

    public OrderInfo(String date, String choices, String reason, ClassRoom selectedRoom){
        this.date = date;
        this.choices = choices;
        this.reason = reason;
        this.selectedRoom = selectedRoom;
    }
}
