package com.platform.ahj.r2dbcmysql;

import com.platform.ahj.r2dbcmysql.controller.UserController;
import com.platform.ahj.r2dbcmysql.entity.TAuthor;
import com.platform.ahj.r2dbcmysql.entity.TBook;
import com.platform.ahj.r2dbcmysql.repositories.BookRepository;
import com.platform.ahj.r2dbcmysql.repositories.UserRepository;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Slf4j
@SpringBootTest
class R2dbcMysqlApplicationTests {
    static Mono<Connection> connectionMono = null;

    @Autowired
    private UserController userController;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private DatabaseClient databaseClient;

    @AfterAll
    static void close() throws InterruptedException {
        Thread.sleep(10000);
    }

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
        userRepository.findAll()
                      .subscribe(x -> log.info("findAll:{}", x.toString()));
        userRepository.findTopById(111l)
                      .subscribe(x -> log.info("findTopById:{}", x.toString()));
        userRepository.findAllByNameLike("1%")
                      .subscribe(x -> log.info("findAllByNameLike:{}", x.toString()));
        Thread.sleep(10000);
    }

    @Test
    void testJoin() throws InterruptedException {
        bookRepository.findAll()
                      .subscribe(x -> log.info("findAll:{}", x));
        bookRepository.hahaBook(1l)
                      .subscribe(x -> log.info("hahaBook:{}", x));
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
    void oneToMany() throws InterruptedException {
        //        databaseClient.sql("select a.id aid,a.name,b.* from t_author a  " +
        //                "left join t_book b on a.id = b.author_id " +
        //                "order by a.id")
        //                .fetch()
        //                .all(row -> {
        //
        //                })


        // 1~6
        // 1：false 2：false 3:false 4: true 8:true 5:false 6:false 7:false 8:true 9:false 10:false
        // [1,2,3]
        // [4,8]
        // [5,6,7]
        // [8]
        // [9,10]
        // bufferUntilChanged：
        // 如果下一个判定值比起上一个发生了变化就开一个新buffer保存，如果没有变化就保存到原buffer中

        //        Flux.just(1,2,3,4,8,5,6,7,8,9,10)
        //                .bufferUntilChanged(integer -> integer%4==0 )
        //                .subscribe(list-> System.out.println("list = " + list));
        ; // 自带分组


        Flux<TAuthor> flux = databaseClient.sql("""
                                                select a.id aid,a.name,b.* from t_author a  
                                                       left join t_book b on a.id = b.author_id 
                                                       order by a.id
                                                """)
                                           .fetch()
                                           .all()
                                           .bufferUntilChanged(rowMap -> Long.parseLong(rowMap.get("aid")
                                                                                              .toString()))
                                           .map(list -> {
                                               TAuthor tAuthor = new TAuthor();
                                               Map<String, Object> map = list.get(0);
                                               tAuthor.setId(Long.parseLong(map.get("aid")
                                                                               .toString()));
                                               tAuthor.setName(map.get("name")
                                                                  .toString());
                                               // 查到的所有图书
                                               List<TBook> tBooks = list.stream()
                                                                        .map(ele -> {
                                                                            TBook tBook = new TBook();
                                                                            tBook.setId(Long.parseLong(ele.get("id")
                                                                                                          .toString()));
                                                                            tBook.setAuthorId(Long.parseLong(
                                                                                    ele.get("author_id")
                                                                                       .toString()));
                                                                            tBook.setTitle(ele.get("title")
                                                                                              .toString());
                                                                            return tBook;
                                                                        })
                                                                        .collect(Collectors.toList());

                                               tAuthor.setBooks(tBooks);
                                               return tAuthor;
                                           });// Long 数字缓存 -127 - 127；// 对象比较需要自己写好equals方法
        flux.subscribe(tAuthor -> log.info("tAuthor = {}", tAuthor));
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