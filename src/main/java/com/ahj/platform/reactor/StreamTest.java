package com.ahj.platform.reactor;

import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Stream;

public class StreamTest {
    public static void main(String[] args) {
        byte[] input = new byte[] { (byte) 0xe4, (byte) 0xb8, (byte) 0xad, 0x21 };
        String b64encoded = Base64.getEncoder().encodeToString(input);
        String b64encoded2 = Base64.getEncoder().withoutPadding().encodeToString(input);
        System.out.println(b64encoded);
        System.out.println(b64encoded2);
        byte[] output = Base64.getDecoder().decode(b64encoded2);
        System.out.println(Arrays.toString(output));
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
        new Thread(()->{
            try {
                Thread.sleep(10000);
                System.out.println("hello");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
