package easyRent;

public class OrderMsg {
	public String orderMsgId;
	public String classRoom;
    public String stuId;
    public String orderDate;
    public String reason;
    public String dateDetail;
    public String submitTime;
    public int status;
    
    public OrderMsg(String orderMsgId, String classRoom, String stuId, String orderDate, String reason, String dateDetail, String submitTime, int status) {
    	this.orderMsgId = orderMsgId;
    	this.classRoom = classRoom;
    	this.stuId = stuId;
    	this.orderDate = orderDate;
    	this.reason = reason;
    	this.dateDetail = dateDetail;
    	this.submitTime = submitTime;
    	this.status = status;
    }

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
    
    public void setDateDetail(String dateDetail) {
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
    
    public String getDateDetail() {
    	return dateDetail;
    }
    
    public String getSubmitTime() {
    	return submitTime;
    }

    public int getStatus(){
        return status;
    }
}
