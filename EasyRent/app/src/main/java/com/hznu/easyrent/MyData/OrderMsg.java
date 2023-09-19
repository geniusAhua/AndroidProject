package com.hznu.easyrent.MyData;

import java.io.Serializable;

public class OrderMsg implements Serializable {
    public String orderMsgId;
    public String classRoom;
    public String stuId;
    public String orderDate;
    public String reason;
    public String dateDetail;
    public String submitTime;
    public int status;

    public void setOrderMsgId(String orderMsgId){
        this.orderMsgId = orderMsgId;
    }

    public void setClassRoom(String classRoom){
        this.classRoom = classRoom;
    }

    public void setStuId(String stuId) {
        this.stuId = stuId;
    }

    public void setOrderDate(String orderDate){
        this.orderDate = orderDate;
    }

    public void setReason(String reason){
        this.reason = reason;
    }

    public void setDateDetail(String dateDetail){
        this.dateDetail = dateDetail;
    }

    public void setSubmitTime(String submitTime){
        this.submitTime = submitTime;
    }

    public void setStatus(int status){
        this.status = status;
    }

    public String getOrderMsgId(){
        return orderMsgId;
    }

    public String getClassRoom(){
        return classRoom;
    }

    public String getStuId(){
        return stuId;
    }

    public String getOrderDate(){
        return orderDate;
    }

    public String getReason(){
        return reason;
    }

    public String getDateDetail(){
        return dateDetail;
    }

    public String getSubmitTime(){
        return submitTime;
    }

    public int getStatus(){
        return status;
    }
}