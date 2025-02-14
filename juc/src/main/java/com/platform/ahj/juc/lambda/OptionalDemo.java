package com.platform.ahj.juc.lambda;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @Description: 使用Optional示例
 * @Author: ziyu
 * @Created: 2025/2/14-13:46
 * @Since:
 */
@Slf4j
public class OptionalDemo {
    public static void main(String[] args) {
        Date date = new Date(2012,1,1);
        System.out.println(date);
        // 调用工厂方法创建Optional实例
        Optional<String> name = Optional.of("Sanaulla");
        // 传入参数为null，抛出NullPointerException.
        try {
            Optional<String> someNull = Optional.of(null);
        } catch (Exception e) {
            log.info("Exception: {}", e);
        }

        // 下面创建了一个不包含任何值的Optional实例
        // 例如，值为'null'
        Optional empty = Optional.ofNullable(null);
        // isPresent方法用来检查Optional实例中是否包含值
        if (name.isPresent()) {
            // 在Optional实例内调用get()返回已存在的值
            System.out.println(name.get());// 输出Sanaulla
        }
        // 执行下面的代码会输出: No value present
        try {
            // 在空的Optional实例上调用get()，抛出NoSuchElementException
            System.out.println(empty.get());
        } catch (NoSuchElementException ex) {
            log.info("Exception: {}", ex);
        }

        // ifPresent方法接受lambda表达式作为参数。 如果Optional实例有值则为其调用consumer，否则不做处理
        // lambda表达式对Optional的值调用consumer进行处理。
        name.ifPresent((value) -> System.out.println("The length of the value is: " + value.length()));
        // 无任何输出
        empty.ifPresent((value) -> System.out.println("empty The length of the value is: " + value));


        // 如果值不为null，orElse方法返回Optional实例的值。
        // 如果为null，返回传入的消息。
        // 输出: There is no value present!
        System.out.println(empty.orElse("There is no value present!"));
        // 输出: Sanaulla
        System.out.println(name.orElse("There is some value!"));

        // orElseGet与orElse方法类似，区别在于orElse传入的是默认值，
        // orElseGet可以接受一个lambda表达式生成默认值。
        // 输出: Default Value
        System.out.println(empty.orElseGet(() -> "Default Value"));
        // 输出: Sanaulla
        System.out.println(name.orElseGet(() -> "Default Value"));

        try {
            // orElseThrow与orElse方法类似。与返回默认值不同，
            // orElseThrow会抛出lambda表达式或方法生成的异常

            empty.orElseThrow(NullPointerException::new);
        } catch (Throwable ex) {
            // 输出: No value present in the Optional instance
            log.info("Exception: {}", ex);
        }

        // 如果有值，则对其执行调用mapping函数得到返回值。
        // 如果返回值不为null，则创建包含mapping返回值的Optional作为map方法返回值，否则返回空Optional。
        // map方法执行传入的lambda表达式参数对Optional实例的值进行修改。
        // 为lambda表达式的返回值创建新的Optional实例作为map方法的返回值。
        Optional<String> upperName = name.map((value) -> value.toUpperCase());
        System.out.println(upperName.orElse("No value found"));

        //flatMap与map(Function)非常类似，区别在于传入方法的lambda表达式的返回类型。
        //map方法中的lambda表达式返回值可以是任意类型，在map函数返回之前会包装为Optional。
        //但flatMap方法中的lambda表达式返回值必须是Optionl实例。
        Optional<List<String>> upperName1 = name.flatMap(
                (value) -> Optional.of(Arrays.asList(value.toUpperCase(), value.toLowerCase())));
        System.out.println(upperName1.orElse(Collections.singletonList("No value found")));//输出SANAULLA
        upperName = empty.flatMap((value) -> Optional.of(value));
        System.out.println(upperName.orElse("No value found"));//No value found


        //filter方法检查给定的Option值是否满足某些条件。
        //如果满足则返回同一个Option实例，否则返回空Optional。
        Optional<String> longName = name.filter((value) -> value.length() > 6);
        System.out.println(longName.orElse("The name is less than 6 characters"));//输出Sanaulla

        //另一个例子是Optional值不满足filter指定的条件。
        Optional<String> anotherName = Optional.of("Sana");
        Optional<String> shortName = anotherName.filter((value) -> value.length() > 6);
        //输出: name长度不足6字符
        System.out.println(shortName.orElse("The name is less than 6 characters"));
    }
}
