package com.platform.ahj.r2dbcmysql;

import com.platform.ahj.r2dbcmysql.controller.UserController;
import com.platform.ahj.r2dbcmysql.repositories.UserRepositories;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Slf4j
@SpringBootTest
class R2dbcMysqlApplicationTests {
    static Mono<Connection> connectionMono = null;

    @Autowired
    private UserController userController;

    @Autowired
    private UserRepositories userRepositories;

    @BeforeAll
    static void init() {
        // Notice: the query string must be URL encoded
        ConnectionFactoryOptions options = ConnectionFactoryOptions.builder()
                                                                   .option(DRIVER, "mysql")
                                                                   .option(HOST, "mp-base-mysql")
                                                                   .option(USER, "root")
                                                                   .option(PORT, 3306)
                                                                   .option(PASSWORD, "root")
                                                                   .option(DATABASE, "r2dbc")
                                                                   .build();
        ConnectionFactory connectionFactory = ConnectionFactories.get(options);

        // Creating a Mono using Project Reactor
        connectionMono = Mono.from(connectionFactory.create());
    }

    @Test
    void testQueryByTemplate() throws InterruptedException {
        Flux<com.platform.ahj.r2dbcmysql.entity.User> allUser = userController.getAllUser();
        allUser.subscribe(x -> log.info(x.toString()));
        Thread.sleep(10000);
    }

    @Test
    void testQueryByRepository() throws InterruptedException {
        userRepositories.findAll()
                        .subscribe(x -> log.info(x.toString()));
        Thread.sleep(10000);
    }

    @Test
    void contextLoads() throws InterruptedException {


        connectionMono.flatMapMany(connection -> connection.createStatement("SELECT * FROM user")
                                                           .execute())
                      .flatMap(result -> result.map(
                              (row, rowMetadata) -> new User(row.get("name", String.class), row.get("id", Long.class))))
                      .subscribe(v -> {
                          System.out.println(v);
                      });
        Thread.sleep(10000);
    }

    @Test
    public void select() throws InterruptedException {
        connectionMono.flatMapMany(
                              connection -> connection.createStatement("SELECT * FROM user where id = ?id and " +
                                                                               "name=?name")
                                                      .bind("id", 111)
                                                      .bind("name", "eee")
                                                      .execute())
                      .flatMap(result -> result.map(
                              (row, rowMetadata) -> new User(row.get("name", String.class), row.get("id", Long.class))))
                      .subscribe(v -> {
                          System.out.println(v);
                      });
        Thread.sleep(10000);
    }
}

class User {
    private String name;

    private Long id;

    public User(String name, Long id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}