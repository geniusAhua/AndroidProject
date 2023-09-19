package com.hznu.easyrent.Constants;

public class ServType {
    private static final String SER_IP = Constants.SER_IP;
    public static final String LOGIN = "http://" + SER_IP + ":8080/AndroidWeb/LoginServlet";
    public static final String REGIST = "http://" + SER_IP + ":8080/AndroidWeb/RegisterServlet";
    public static final String UPLOAD = "http://" + SER_IP + ":8080/AndroidWeb/UploadServlet";
    public static final String CHECKPWD = "http://" + SER_IP + ":8080/AndroidWeb/CheckPWDServlet";
    public static final String CHANGEPWD = "http://" + SER_IP + ":8080/AndroidWeb/ChangePWDServlet";
    public static final String LOAD_HEAD = "http://" + SER_IP + ":8080/AndroidWeb/headPic/";
    public static final String LOAD_CLASSROOM = "http://" + SER_IP + ":8080/AndroidWeb/classRoomPic/";
    public static final String QUERY_CLASSROOM = "http://" + SER_IP + ":8080/AndroidWeb/ClassRoomServlet";
    public static final String INSERT_ORDER_INFO = "http://" + SER_IP + ":8080/AndroidWeb/OrderInfoServlet";
    public static final String QUERY_ORDER_INFO = "http://" + SER_IP + ":8080/AndroidWeb/OrderInfoServlet";
    public static final String QUERY_STU_INFO = "http://" + SER_IP + ":8080/AndroidWeb/StudentInfoServlet";
    public static final String UPDATE_ORDER_INFO = "http://" + SER_IP + ":8080/AndroidWeb/OrderInfoServlet";
    public static final String QUERY_CLASSROOM_CHOICES = "http://" + SER_IP + ":8080/AndroidWeb/OrderInfoServlet";
}
