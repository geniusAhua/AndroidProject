package com.hznu.easyrent.MyData;

import android.util.Log;

import com.hznu.easyrent.Constants.Constants;

public class LogUtils {
    private static final String WEB_TAG = Constants.WEB_TAG;

    public static void printLogData(String data){
        Log.d(WEB_TAG, data);
    }
}
