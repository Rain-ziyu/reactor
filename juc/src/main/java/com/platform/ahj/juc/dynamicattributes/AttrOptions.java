package com.platform.ahj.juc.dynamicattributes;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 存放动态属性的基本类
 * @Author: ziyu
 * @Created: 2025/1/13-10:12
 * @Since:
 */
public class AttrOptions {

    /**
     * 动态成员属性
     */
    private final Map<AttrOption<?>, Object> options = new HashMap<>();

    /**
     * 获取值
     */
    @SuppressWarnings("unchecked")
    public <T> T option(AttrOption<T> option) {
        return (T) options.get(option);
    }

    /**
     * 设置值
     */
    public <T> void option(AttrOption<T> option, T value) {
        options.put(option, value);
    }


}

record AttrOption<T>(String name) {
    /**
     * 构建属性项
     */
    public static <T> AttrOption<T> valueOf(String name) {
        return new AttrOption<T>(name);
    }
}
