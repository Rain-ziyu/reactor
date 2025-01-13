package com.platform.ahj.juc.dynamicattributes;

/**
 * @Description: 动态属性公共接口
 * @Author: ziyu
 * @Created: 2025/1/13-10:15
 * @Since:
 */
public interface AttrOptionDynamic {

    /**
     * 动态成员属性
     */
    AttrOptions getOptions();

    /**
     * 获取值
     */
    default <T> T option(AttrOption<T> option) {
        return this.getOptions()
                   .option(option);
    }

    /**
     * 设置值
     */
    default <T> void option(AttrOption<T> option, T value) {
        this.getOptions()
            .option(option, value);
    }


}
