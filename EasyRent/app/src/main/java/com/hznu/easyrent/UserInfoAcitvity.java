package com.hznu.easyrent;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.*;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.*;
import android.provider.*;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.core.content.ContextCompat;

import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.hznu.easyrent.Constants.Constants;
import com.hznu.easyrent.MyData.HttpUtil;
import com.hznu.easyrent.MyUI.myBase.BaseActivity;
import com.hznu.easyrent.MyData.UserUtil;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserInfoAcitvity extends BaseActivity implements OnTitleBarListener, View.OnClickListener{
    //控件
    private TitleBar titleBar;
    private TextView userName;
    private TextView userStuId;
    private TextView userSex;
    private TextView userCollege;
    private TextView userDepartment;
    private TextView userClass;
    private ImageView headPic;
    private ImageView sexIcon;
    private Button sureChange;
    private Button cancleChange;

    private String picPath;

    //修改信息flag
    private boolean isChange = false;

    private static final int CHOOSE_PHOTO = 2;
    private final String WEB_TAG = Constants.WEB_TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setTitle("个人信息");
        setContentView(R.layout.activity_user_info);

        //设置占位符
        setPlaceHolder(findViewById(R.id.place_holder));

        //获取控件
        headPic = findViewById(R.id.head_pic);
        sexIcon = findViewById(R.id.sex_pic);
        titleBar = findViewById(R.id.userinfo_titlebar);//标题栏
        userName = findViewById(R.id.user_name);
        userStuId = findViewById(R.id.user_stuId);
        userSex = findViewById(R.id.user_sex);
        userCollege = findViewById(R.id.user_college);
        userDepartment = findViewById(R.id.user_department);
        userClass = findViewById(R.id.user_class);
        sureChange = findViewById(R.id.sure_change);//确认修改按钮
        cancleChange = findViewById(R.id.cancel_change);//取消修改按钮

        //设置用户信息
        userName.setText(UserUtil.getUserName());
        userStuId.setText(UserUtil.getUserId());
        userCollege.setText(UserUtil.getUserCollege());
        userDepartment.setText(UserUtil.getUserDepartment());
        userClass.setText(UserUtil.getUserClass());
        userSex.setText(UserUtil.getUserSex());
        if(UserUtil.getUserSex().equals("男")){
            sexIcon.setImageResource(R.drawable.male);
        }
        else{
            sexIcon.setImageResource(R.drawable.female);
        }
        //获取头像图片
        UserUtil.setUserHeadPicUI(headPic);

        //是否修改_按钮设置
        sureChange.setVisibility(View.GONE);
        cancleChange.setVisibility(View.GONE);

        //事件绑定
        sureChange.setOnClickListener(this);
        cancleChange.setOnClickListener(this);
        headPic.setOnClickListener(this);
        titleBar.setOnTitleBarListener(this);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(isChange) {
            sureChange.setVisibility(View.VISIBLE);
            cancleChange.setVisibility(View.VISIBLE);
        }
        else {
            //获取头像图片
            Log.d(WEB_TAG, "刷新了");
            UserUtil.setUserHeadPicUI(headPic);
            sureChange.setVisibility(View.GONE);
            cancleChange.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLeftClick(View v) {
        //返回按钮
        finish();
    }

    @Override
    public void onTitleClick(View v) { }

    @Override
    public void onRightClick(View v) { }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.head_pic:
                //从相册中选择
                //fragment中申请权限同Activity不同！
                if(ContextCompat.checkSelfPermission(UserInfoAcitvity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    UserInfoAcitvity.this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                else
                    openAlbum();
                break;
            case R.id.sure_change:
                //下面一句话运行结束后picPath的值会变为服务器中的地址
                //因为确定后才可以将该图片上传
                netUploadWithOkHttp(Constants.UPLOAD_HEADPIC, picPath);
                break;
            case R.id.cancel_change:
                isChange = false;
                onResume();
                break;
        }
    }

    //打开相册
    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    //打开相册权限
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                //获取权限成功打开相册，否则弹出提醒
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openAlbum();
                else
                    Toast.makeText(UserInfoAcitvity.this, "You denied the permission", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    //startActivityForResult完成后根据返回的requestCode判断下一步操作
    @Override
    public void  onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case CHOOSE_PHOTO:
                isChange = true;
                if(resultCode == RESULT_OK){
                    if(Build.VERSION.SDK_INT >= 19){
                        //4.4及以上系统使用这个方法处理图片
                        handleImageOnKitKat(data);
                    }
                    else{
                        //4.4一下的使用该方法处理
                        handleImageBeforeKitKat(data);
                    }
                }
                sureChange.setVisibility(View.VISIBLE);
                cancleChange.setVisibility(View.VISIBLE);
                break;
        }
    }

    //新版图片处理
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data){
        String imagePath = null;
        Uri uri = data.getData();
        if(DocumentsContract.isDocumentUri(this, uri)){
            //如果是document类型的uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            }
            else if("com.android.providers.downloada.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        }
        else if("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        }
        else if("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的uri，则直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath);
    }
    //兼容老版本的图片处理
    private void handleImageBeforeKitKat(Intent data){
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    //获取图片路径
    private String getImagePath(Uri uri, String selection){
        String path = null;
        //通过uri和selection来获取真实的图片路径
        //fragment中获取内容提供器需要context
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        Log.d(Constants.WEB_TAG, path);
        picPath = path;
        return path;
    }

    //显示图片
    private void displayImage(String imagePaht){
        if(imagePaht != null){
            Bitmap headBitmap = BitmapFactory.decodeFile(imagePaht);
            //避免图片发生旋转
            try {
                ExifInterface exif = new ExifInterface(imagePaht);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                Log.d("EXIF", "Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                }
                else if (orientation == 3) {
                    matrix.postRotate(180);
                }
                else if (orientation == 8) {
                    matrix.postRotate(270);
                }
                headBitmap = Bitmap.createBitmap(headBitmap, 0, 0, headBitmap.getWidth(), headBitmap.getHeight(), matrix, true); // rotating bitmap
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            headPic.setImageBitmap(headBitmap);
        }
        else {
            Toast.makeText(UserInfoAcitvity.this, "faild to get image", Toast.LENGTH_SHORT).show();
        }
    }

    //上传头像
    private void netUploadWithOkHttp(String nameType, String path){
        HttpUtil.netUploadWithOkHttp(nameType, path, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //连接服务器失败
                Log.d(WEB_TAG, "connect failure");
                runOnUiThread(()-> {
                    Toast.makeText(UserInfoAcitvity.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String responseData = response.body().string();

                runOnUiThread(()->{
                    try {
                        JSONObject json = new JSONObject(responseData);
                        String status = json.get(Constants.JSON_STATUS).toString();
                        if (status.equals("success")) {
                            //提醒
                            Log.d(WEB_TAG, json.toString());
                            Toast.makeText(UserInfoAcitvity.this, json.get(Constants.JSON_MSG).toString(), Toast.LENGTH_SHORT).show();
                            //获取图片路径,并更新头像图片
                            picPath = json.getString(Constants.JSON_HEADPATH);
                            Log.d(WEB_TAG, "update:" + picPath);
                            UserUtil.UpdateUserHeadPicPath(picPath);
                //////////////确定用户的头像图片修改完成
                            //isChange改变代表修改已经完成，改变应该在上传结束后完成
                            //防止path未修改而界面已经开始刷新导致头像还是原来的头像
                            isChange = false;
                            onResume();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }
}
