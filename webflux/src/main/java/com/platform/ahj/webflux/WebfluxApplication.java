package com.platform.ahj.webflux;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

import java.nio.charset.StandardCharsets;
// @EnableWebFlux   // 开启webflux的自定义；将会禁用WebFlux的很多默认效果
@SpringBootApplication
@Slf4j
public class WebfluxApplication {

    public static void httpHandler(String[] args) {

        HttpHandler httpHandler = (request, response) -> {
            log.info("请求路径：{}", request.getURI());
            //编写请求处理的业务,给浏览器写一个内容 URL + "Hello~!"
            //            response.getHeaders(); //获取响应头
            //            response.getCookies(); //获取Cookie
            //            response.getStatusCode(); //获取响应状态码；
            //            response.bufferFactory(); //buffer工厂
            //            response.writeWith() //把xxx写出去
            //            response.setComplete(); //响应结束
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            DataBuffer wrap = dataBufferFactory.wrap(new String("hello!").getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(wrap));
        };
        ReactorHttpHandlerAdapter adapter = new ReactorHttpHandlerAdapter(httpHandler);
        DisposableServer localhost = HttpServer.create()
                                               .host("localhost")
                                               .port(8080)
                                               .handle(adapter)
                                               .bindNow();
        localhost.onDispose().block();
        // SpringApplication.run(WebfluxApplication.class, args);
    }

    public static void main(String[] args) {
        SpringApplication.run(WebfluxApplication.class, args);
    }
}
