package com.hznu.easyrent.MyData;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ImageView;

import com.hjq.toast.ToastUtils;
import com.hznu.easyrent.Constants.Constants;
import com.hznu.easyrent.Constants.ServType;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

public class UserUtil {
    //初始化标志
    private static boolean isInit = false;
    //常量
    private static String SPF_NAME = Constants.SPF_NAME;
    private static String SPF_ISLOGIN = Constants.SPF_ISLOGIN;
    private static String SPF_ACCOUNT = Constants.SPF_ACCOUNT;
    private static String SPF_PASSWORD = Constants.SPF_PASSWORD;

    //获取上下文
    private static Context c;

    //存储登录信息
    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;
    private static boolean _isLogin = false;
    private static String account = null;
    private static String password = null;
    private static int user_type = 1;//0管理员1学生
    private static String user_id = null;
    private static String user_name = null;
    private static String user_sex = null;
    private static String user_college = null;
    private static String user_department = null;
    private static String user_class = null;
    private static String user_headPath = null;

    public static void Init(Context context){
        isInit = true;
        c = context;
        preferences = c.getSharedPreferences(SPF_NAME, MODE_PRIVATE);
        editor = preferences.edit();
        _isLogin = preferences.getBoolean(SPF_ISLOGIN, false);
        account = preferences.getString(SPF_ACCOUNT, "");
        password = preferences.getString(SPF_PASSWORD, "");
    }
    //更新头像路径
    public static void UpdateUserHeadPicPath(String path){
        checkInit();
        user_headPath = path;
    }

    //退出登录更新数据
    public static void setUserDataLogout(){
        checkInit();
        editor.putBoolean(SPF_ISLOGIN, false);
        editor.putString(SPF_PASSWORD, "");
        editor.apply();
        initUserData();
        isInit = false;
    }
    //头像图片加载到imageview
    public static void setUserHeadPicUI(ImageView imageView){
        headPicFromServer(imageView);
    }
    //判断是否登陆
    public static boolean isLogin(){
        checkInit();
        return _isLogin;
    }

    public static String getUserAccount(){
        checkInit();
        return account;
    }

    public static String getUserPassword(){
        checkInit();
        return password;
    }

    public static String getUserId(){
        checkInit();
        return user_id;
    }

    public static String getUserName(){
        checkInit();
        return user_name;
    }

    public static String getUserSex(){
        checkInit();
        return user_sex;
    }

    public static String getUserCollege(){
        checkInit();
        return user_college;
    }

    public static String getUserDepartment(){
        checkInit();
        return user_department;
    }

    public static String getUserClass(){
        checkInit();
        return user_class;
    }
    //判断用户类型
    public static int getUserType(){
        checkInit();
        return user_type;
    }

    public static void setUserData(String account){
        checkInit();
        editor.putBoolean(SPF_ISLOGIN, false);
        editor.putString(SPF_ACCOUNT, account);
        editor.putString(SPF_PASSWORD, "");
        UserUtil.account = account;
    }

    public static void setUserData(String account, String password, JSONObject json){
        checkInit();
        editor.putBoolean(SPF_ISLOGIN, true);
        editor.putString(SPF_ACCOUNT, account);
        editor.putString(SPF_PASSWORD, password);
        editor.apply();
        UserUtil.account = account;
        try {
            user_type = (int) json.get(Constants.JSON_TYPE);
            user_id = (String) json.get(Constants.JSON_ID);
            user_name = (String) json.get(Constants.JSON_NAME);
            user_sex = (String) json.get(Constants.JSON_SEX);
            user_college = (String) json.get(Constants.JSON_COLLEGE);
            user_department = (String) json.get(Constants.JSON_DEPARTMENT);
            user_class = (String) json.get(Constants.JSON_CLASS);
            user_headPath = json.getString(Constants.JSON_HEADPATH);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //从服务器获取图片
    private static void headPicFromServer(ImageView imageView){
        //获取服务器头像图片
        Handler handler = new Handler(msg -> {
            switch (msg.what) {
                case 1://成功
                    byte[] b = (byte[]) msg.obj;
                    //转换成bitmap
                    Bitmap headBitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                    imageView.setImageBitmap(headBitmap);
                    break;
                case 0:
                    ToastUtils.show("图片加载失败");
                    break;
            }
            return false;
        });
        HttpUtil.loadPicOnServe(ServType.LOAD_HEAD, user_headPath, handler);
    }
    //初始化数据
    private static void initUserData(){
        user_type = 1;
        password = "";
        user_name = user_id = user_sex = user_college = user_department = user_class = user_headPath =null;
    }
    //检查是否有初始化，否则抛出异常
    private static void checkInit(){
        try{
            if(!isInit)
                throw new Exception("UserUtil must be inited first");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
