package com.hznu.easyrent.MyData;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hznu.easyrent.Constants.Constants;
import com.hznu.easyrent.Constants.ServType;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.CookieManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.JavaNetCookieJar;

public class HttpUtil {
    private static String WEB_TAG = Constants.WEB_TAG;
    private static CookieManager cookieManager;

    //登录
    public static void loginWithOkHttp(String account, String password, okhttp3.Callback callback){
        cookieManager = new CookieManager();
        CookieJar cookieJar = new JavaNetCookieJar(cookieManager);//获取cookie以保持session
        String address = ServType.LOGIN;
        //创建包含cookie头的Client
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
        RequestBody body = new FormBody.Builder()
                .add("loginAccount", account)
                .add("loginPassword", password)
                .build();

        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();
        //日志信息
        toLog(address, account, password);
        //发送请求
        client.newCall(request).enqueue(callback);
    }

    //注册
    public static void registerWithOkHttp(String account, String password, okhttp3.Callback callback){
        CookieJar cookieJar = new JavaNetCookieJar(cookieManager);//获取cookie以保持session
        String address = ServType.REGIST;
        //创建包含cookie头的Client
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
        RequestBody body = new FormBody.Builder()
                .add("registerAccount", account)
                .add("registerPassword", password)
                .build();

        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();
        //日志信息
        toLog(address, account, password);
        //发送请求
        client.newCall(request).enqueue(callback);
    }

    //上传图片
    public static void netUploadWithOkHttp(String nameType, String path, Callback callback){
        CookieJar cookieJar = new JavaNetCookieJar(cookieManager);//获取cookie以保持session
        String address = ServType.UPLOAD;
        //创建包含cookie头的Client
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
        MediaType mediaType = MediaType.Companion.parse("application/octet-stream; charset=utf-8");
        File file = new File(path);

        RequestBody body = RequestBody.Companion.create(file, mediaType);
        MultipartBody multipartBody;
        switch (nameType){
            case Constants.UPLOAD_HEADPIC:
                if(!MediaFileUtil.isImageFileType(path)){
                    Log.d(WEB_TAG, "filetype: 不是图片类型!");
                    return;
                }
                multipartBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart(nameType, file.getName(), body)
                        .build();
                break;
            default:
                multipartBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("Img", file.getName(), body)
                        .build();
                break;
        }
        Request request = new Request.Builder()
                .url(address)
                .post(multipartBody)
                .build();
        //发送日志信息
        toLog(address, nameType, file);
        //发送数据到服务器
        client.newCall(request).enqueue(callback);
    }

    //加载网络图片
    //path要给出图片的Constans标志
    public static void loadPicOnServe(final String type, String path, Handler handler){
        CookieJar cookieJar = new JavaNetCookieJar(cookieManager);//获取cookie以保持session
        Log.d(WEB_TAG, "loadPath:" + type + path);
        String address = type + path;
        //创建包含cookie头的Client
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //失败提醒
                Log.d(WEB_TAG, "连接失败：" + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Message message = handler.obtainMessage();//声明一个传递信息的message
                if(response.isSuccessful()){//成功
                    Log.d(WEB_TAG, "onResponse: YES");
                    switch (type){
                        case ServType.LOAD_HEAD://加载头像
                            message.what = 1;//设置成功的指令
                            break;
                        case ServType.LOAD_CLASSROOM://加载教室图片
                            message.what = 2;
                            break;
                    }
                    message.obj = response.body().bytes();//带入图片数据
                    handler.sendMessage(message);//将指令和数据传出去
                }
                else{//失败
                    Log.d(WEB_TAG, "onResponse: NO" + " response:" + response.toString());
                    handler.sendEmptyMessage(0);
                    //如果头像加载失败则加载默认图片
                    loadPicOnServe(type, "timg.jpg", handler);
                }
            }
        });
    }

    //检查密码
    public static void checkPWD(String pwd, Callback callback){
        CookieJar cookieJar = new JavaNetCookieJar(cookieManager);//获取cookie以保持session
        String address = ServType.CHECKPWD;
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
        RequestBody body = new FormBody.Builder()
                .add("password", pwd)
                .build();
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();

        //日志信息
        toLog(address, "originalPWD:" + pwd);
        //发送数据到服务器
        client.newCall(request).enqueue(callback);
    }

    //修改密码
    public static void changePWD(String pwd, Callback callback){
        CookieJar cookieJar = new JavaNetCookieJar(cookieManager);
        String address = ServType.CHANGEPWD;
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
        RequestBody body = new FormBody.Builder()
                .add("newPassword", pwd)
                .build();
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();
        //日志信息
        toLog(address, "newPWD:" + pwd);
        //发送数据到服务器
        client.newCall(request).enqueue(callback);
    }

    //查询教室
    public static void queryClassRoom(Callback callback){//查询所有
        CookieJar cookieJar = new JavaNetCookieJar(cookieManager);
        String address = ServType.QUERY_CLASSROOM;
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
        RequestBody body = new FormBody.Builder()
                .add("info","all")
                .build();
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();

        toLog(address, "查询所有教室");
        client.newCall(request).enqueue(callback);
    }

    //查询教室
    public static void queryClassRoom(String str, Callback callback){
        CookieJar cookieJar = new JavaNetCookieJar(cookieManager);
        String address = ServType.QUERY_CLASSROOM;
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
        RequestBody body = new FormBody.Builder()
                .add("info", str)
                .build();
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();

        toLog(address, "查询教室,信息：" + str);
        client.newCall(request).enqueue(callback);
    }

    //更新申请
    public static void insertOrderInfo(OrderInfo orderInfo, Callback callback){
        CookieJar cookieJar = new JavaNetCookieJar(cookieManager);
        String address = ServType.INSERT_ORDER_INFO;
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
        RequestBody body = new FormBody.Builder()
                .add("type", "insert")
                .add("userId", UserUtil.getUserId())
                .add("orderDate", orderInfo.date)
                .add("orderReason", orderInfo.reason)
                .add("orderChoices", orderInfo.choices)
                .add("classRoom", orderInfo.selectedRoom.classRoom)
                .build();
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();

        toLog(address, "提交申请："+orderInfo.reason+"---"+orderInfo.date);
        client.newCall(request).enqueue(callback);
    }

    /**
     * 查询所有申请数据
     * 根据用户类型，后台自动选择查询内容
     * @param callback
     */
    public static void queryOrderInfo(Callback callback){
        CookieJar cookieJar = new JavaNetCookieJar(cookieManager);
        String address = ServType.QUERY_ORDER_INFO;
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
        RequestBody body = new FormBody.Builder()
                .add("type", UserUtil.getUserType() + "")
                .build();
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();

        toLog(address, "查询申请信息："+ (UserUtil.getUserType() == 0?"管理员":"普通用户"));
        client.newCall(request).enqueue(callback);
    }

    /**
     * 查询单个教室所有的申请
     * @param callback
     */
    public static void queryOrderInfo(String classroom, Callback callback){
        CookieJar cookieJar = new JavaNetCookieJar(cookieManager);
        String address = ServType.QUERY_ORDER_INFO;
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
        RequestBody body = new FormBody.Builder()
                .add("type", "byClassroom")
                .add("classroom", classroom)
                .build();
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();

        toLog(address, "根据教室查询申请信息");
        client.newCall(request).enqueue(callback);
    }

    /**
     * 查询申请表
     * @param stuId
     * @param callback
     */
    public static void queryStudentInfo(String stuId, Callback callback){
        CookieJar cookieJar = new JavaNetCookieJar(cookieManager);
        String address = ServType.QUERY_STU_INFO;
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
        RequestBody body = new FormBody.Builder()
                .add("stuId", stuId)
                .build();
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();

        toLog(address, "管理员查询学生信息:" + stuId);
        client.newCall(request).enqueue(callback);
    }

    public static void updateOrderInfo(String orderId, int status, Callback callback){
        CookieJar cookieJar = new JavaNetCookieJar(cookieManager);
        String address = ServType.UPDATE_ORDER_INFO;
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
        RequestBody body = new FormBody.Builder()
                .add("type", "update")
                .add("orderId", orderId)
                .add("orderStatus", status + "")
                .build();
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();

        toLog(address, "修改申请表状态" + status);
        client.newCall(request).enqueue(callback);
    }

    public static void queryClassroomChoices(String classroom, String date, Callback callback){
        CookieJar cookieJar = new JavaNetCookieJar(cookieManager);
        String address = ServType.QUERY_CLASSROOM_CHOICES;
        OkHttpClient client = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .build();
        RequestBody body = new FormBody.Builder()
                .add("type", "classroomChoices")
                .add("classroom", classroom)
                .add("date", date)
                .build();
        Request request = new Request.Builder()
                .url(address)
                .post(body)
                .build();

        toLog(address, "查询当天教室可提供的选项");
        client.newCall(request).enqueue(callback);
    }

    private static void toLog(String address, String account, String password){
        Log.d(WEB_TAG, "address:" + address + " ,account:" + account + " ,password:" + password);
    }

    private static void toLog(String address, String msg){
        Log.d(WEB_TAG, "address:" + address + " ," + msg);
    }

    private static void toLog(String address, String nameType, File file){
        Log.d(WEB_TAG, "address:" + address + " ,nameType:" + nameType + " ,file:" + file.getPath());
    }
}
