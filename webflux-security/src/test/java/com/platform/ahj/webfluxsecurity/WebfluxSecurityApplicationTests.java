package com.platform.ahj.webfluxsecurity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
class WebfluxSecurityApplicationTests {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void contextLoads() {
        String root = passwordEncoder.encode("root");
        boolean root1 = passwordEncoder.matches("root", "{Qg+JPN1+NXKW7oyf7MDn6WFRvIKSRFB0iabYx6cp2wg=}74f856fc98ce6a6709680d6d8d7f2fd3");
        System.out.println(root);
        System.out.println(root1);
    }

}
