package com.platform.ahj.webflux.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class MyWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("MyWebFilter start");
        // 需要注意流一旦经过某个操作就会变成新流
        Mono<Void> mono = chain.filter(exchange)
                                    .doFinally(signalType -> {
                                        log.info("doFinally");
                                    });
        // 整个流程中实际的请求处理依靠的是Mono被订阅之后才会实际处理请求 所以以下打印实际并不是位于controller之前
        log.info("MyWebFilter end");
        return mono;
    }
}
