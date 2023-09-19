package com.hznu.easyrent.MyData;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class gsonUtils {
    public static <T> List<T> toClassWithGson(String jsonData, Class<T> cls){
        Gson gson = new Gson();
        //得到List<T>
        Type type = new ParameterizedTypeImpl(ArrayList.class, new Class[]{cls});
        List<T> list = gson.fromJson(jsonData, type);
        return gson.fromJson(jsonData, type);
    }

}
