package com.platform.ahj.r2dbcmysql.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("t_book")
@Data
public class TBookAuthor {
    @Id
    private Long id;

    private String title;

    private Long authorId;

    private Instant publishTime; // 响应式中日期的映射用 Instant 或者 LocalXxx


    private TAuthor author; // 每本书有唯一作者；
}
