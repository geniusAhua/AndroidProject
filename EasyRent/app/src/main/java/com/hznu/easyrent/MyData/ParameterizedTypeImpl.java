package com.hznu.easyrent.MyData;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ParameterizedTypeImpl implements ParameterizedType {//该类用于获取类型

    private final Class raw;
    private final Type[] args;
    //通过声明该类，获取复合类类型
    public ParameterizedTypeImpl(Class raw, Type[] args){
        this.raw = raw;
        this.args = args != null ?  args : new Type[0];
    }

    @Override//获取复合类，如List<int>,返回[List.class, int.class]
    public Type[] getActualTypeArguments() {
        return args;
    }

    @Override//获取类,上述例子，返回List.class
    public Type getRawType() {
        return raw;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}
