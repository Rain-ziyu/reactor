package com.platform.ahj.r2dbcmysql.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

@Table("user")
@Data
@AllArgsConstructor
public class User {
    private String id;
    private String name;
}
