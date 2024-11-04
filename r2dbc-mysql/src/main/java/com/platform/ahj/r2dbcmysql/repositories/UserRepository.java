package com.platform.ahj.r2dbcmysql.repositories;

import com.platform.ahj.r2dbcmysql.entity.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends R2dbcRepository<User, Long> {
    /**
     * 方法findTopById作用为：
     * 查询id匹配的第一条记录
     *
     * @param id
     * @return com.platform.ahj.r2dbcmysql.entity.User
     * @throws
     * @author ziyu
     */
    Mono<User> findTopById(Long id);
    /**
     * 方法findAllByNameLike作用为：
     * 根据name模糊查询
     * @author ziyu
     * @param name
     * @throws
     * @return reactor.core.publisher.Flux<com.platform.ahj.r2dbcmysql.entity.User>
     */
    Flux<User> findAllByNameLike(String name);


}
