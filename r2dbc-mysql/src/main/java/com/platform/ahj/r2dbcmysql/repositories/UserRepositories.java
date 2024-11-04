package com.platform.ahj.r2dbcmysql.repositories;

import com.platform.ahj.r2dbcmysql.entity.User;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepositories extends R2dbcRepository<User,Long> {
}
