package com.platform.ahj.juc.dynamicattributes;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 动态属性拓展使用示例
 * @Author: ziyu
 * @Created: 2025/1/13-10:17
 * @Since:
 */
@Data
@Slf4j
public class AttrOptionDynamicUseDemo implements AttrOptionDynamic{
    /** 动态属性项集 */
    private final AttrOptions options = new AttrOptions();
    public static void main(String[] args) {
        AttrOptionDynamicUseDemo demo = new AttrOptionDynamicUseDemo();
        demo.option(BindAttrOption.age,18);
        Integer option = demo.option(BindAttrOption.age);
        log.info("age:{}",option);
    }
}


