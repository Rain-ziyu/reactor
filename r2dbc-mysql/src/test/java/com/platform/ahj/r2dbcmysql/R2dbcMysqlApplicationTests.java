package com.platform.ahj.r2dbcmysql;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

// @SpringBootTest
class R2dbcMysqlApplicationTests {
    static Mono<Connection> connectionMono = null;

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
                              connection -> connection.createStatement("SELECT * FROM user where id = ?id and name=?name")
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