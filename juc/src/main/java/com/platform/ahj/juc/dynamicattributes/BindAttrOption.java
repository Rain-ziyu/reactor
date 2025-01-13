package com.platform.ahj.juc.dynamicattributes;

/**
 * @Description: 动态属性映射绑定关系
 * @Author: ziyu
 * @Created: 2025/1/13-10:12
 * @Since:
 */
public interface BindAttrOption {
    AttrOption<String> name = AttrOption.valueOf("name");
    AttrOption<Integer> age = AttrOption.valueOf("age");
}
