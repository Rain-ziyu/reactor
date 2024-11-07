package com.platform.ahj.webfluxsecurity.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableReactiveMethodSecurity // 开启响应式 的 基于方法级别的权限控制
public class AppSecurityConfiguration {


    @Autowired
    ReactiveUserDetailsService appReactiveUserDetailsService;

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,PasswordEncoder passwordEncoder) {
        // 1、定义哪些请求需要认证，哪些不需要
        http.authorizeExchange(authorize -> {
            // 1.1、允许所有人都访问静态资源；
            authorize.matchers(PathRequest.toStaticResources()
                                          .atCommonLocations())
                     .permitAll();


            // 1.2、剩下的所有请求都需要认证（登录）
            authorize.anyExchange()
                     .authenticated();
        });

        // 2、开启默认的表单登录
        http.formLogin(formLoginSpec -> {
            //            formLoginSpec.loginPage("/haha");
        });

        // 3、安全控制:
        http.csrf(csrfSpec -> {
            csrfSpec.disable();
        });

        // 目前认证： 用户名 是 user  密码是默认生成。
        // 期望认证： 去数据库查用户名和密码

        // 4、配置 认证规则： 如何去数据库中查询到用户;
        // Sprinbg Security 底层使用 ReactiveAuthenticationManager 去查询用户信息
        // ReactiveAuthenticationManager 有一个实现是
        //   UserDetailsRepositoryReactiveAuthenticationManager： 用户信息去数据库中查
        //   UDRespAM 需要  ReactiveUserDetailsService：
        // 我们只需要自己写一个 ReactiveUserDetailsService： 响应式的用户详情查询服务
        UserDetailsRepositoryReactiveAuthenticationManager manager = new UserDetailsRepositoryReactiveAuthenticationManager(
                appReactiveUserDetailsService);
        // 在新的响应式认证中，密码的加密方式需要我们手动设置到 认证管理器中或者直接实现更底层的ReactiveAuthenticationManager
        manager.setPasswordEncoder(passwordEncoder);
        http.authenticationManager(
                manager
                                  );


        //        http.addFilterAt()


        // 构建出安全配置
        return http.build();
    }


    @Bean(name = "passwordEncoder")
   public static PasswordEncoder passwordEncoder() {
        PasswordEncoder encoder = new MessageDigestPasswordEncoder("MD5");
        return encoder;
    }
}

