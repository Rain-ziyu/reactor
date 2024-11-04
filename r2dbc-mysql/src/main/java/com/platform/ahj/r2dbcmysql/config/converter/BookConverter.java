package com.platform.ahj.r2dbcmysql.config.converter;

import com.platform.ahj.r2dbcmysql.entity.TAuthor;
import com.platform.ahj.r2dbcmysql.entity.TBook;
import io.r2dbc.spi.Row;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.Instant;
@ReadingConverter
public class BookConverter implements Converter<Row, TBook> {
    // 1）、@Query 指定了 sql如何发送
    // 2）、自定义 BookConverter 指定了 数据库返回的一 Row 数据，怎么封装成 TBook
    // 3）、配置 R2dbcCustomConversions 组件，让 BookConverter 加入其中生效
    @Override
    public TBook convert(Row source) {
        if (source == null) {
            return null;
        }
        // 自定义结果集的封装
        TBook tBook = new TBook();

        tBook.setId(source.get("id", Long.class));
        tBook.setTitle(source.get("title", String.class));

        Long author_id = source.get("author_id", Long.class);
        tBook.setAuthorId(author_id);
        tBook.setPublishTime(source.get("publish_time", Instant.class));


        // 让 converter兼容更多的表结构处理
        if (source.getMetadata()
                  .contains("name")) {
            TAuthor tAuthor = new TAuthor();
            tAuthor.setId(author_id);
            tAuthor.setName(source.get("name", String.class));

            tBook.setAuthor(tAuthor);
        }
        return tBook;
    }
}
