package com.platform.ahj.r2dbcmysql.controller;

import com.platform.ahj.r2dbcmysql.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class UserController {
    @Autowired
    private R2dbcEntityTemplate r2dbcEntityTemplate;
    @GetMapping("/user")
    public Flux<User> getAllUser() {
        Criteria criteria = Criteria.empty()
                              .and("id")
                              .is(111);
        Flux<User> select = r2dbcEntityTemplate.select(Query.query(criteria), User.class);
        return select;
    }
}
