package com.ahj.platform.reactor;

import java.util.Arrays;
import java.util.Base64;

public class EnCodedDemo {
    public static void main(String[] args) {
        byte[] input = new byte[] { (byte) 0xe4, (byte) 0xb8, (byte) 0xad, 0x10 };
        String b64encoded = Base64.getEncoder().encodeToString(input);
        String b64encoded2 = Base64.getEncoder().withoutPadding().encodeToString(input);
        System.out.println(b64encoded);
        System.out.println(b64encoded2);
        byte[] output = Base64.getDecoder().decode(b64encoded2);
        System.out.println(Arrays.toString(output));
        System.out.println(new String(new byte[] { (byte) 0x00, (byte) 0x00, (byte) 0x00, 0x00 }));
    }
}
