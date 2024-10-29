package com.ahj.platform.reactor;

import java.util.stream.Stream;

public class StreamTest {
    public static void main(String[] args) {
        Stream<String> peek = Stream.of("a", "b", "c")
                                    .filter(s -> {
                                        System.out.println("filter: " + s);
                                        return true;
                                    })
                                    .flatMap(s -> Stream.of(s, s))
                                    .peek(s -> {
                                        System.out.println("peek: " + s);
                                        s = s + "!";
                                    });
        Stream<String> stringStream = peek.filter(s -> {
            System.out.println("filter: " + s);
            return true;
        });
        System.out.println(stringStream.max(String::compareTo));
        System.out.println();
    }
}
