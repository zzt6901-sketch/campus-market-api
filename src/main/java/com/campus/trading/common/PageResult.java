package com.campus.trading.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分页响应体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页结果")
public class PageResult<T> {

    @Schema(description = "总记录数", example = "100")
    private long total;

    @Schema(description = "当前页码", example = "1")
    private long page;

    @Schema(description = "每页大小", example = "10")
    private long size;

    @Schema(description = "总页数", example = "10")
    private long pages;

    @Schema(description = "数据列表")
    private List<T> records;

    public static <T> PageResult<T> of(long total, long page, long size, List<T> records) {
        long pages = total % size == 0 ? total / size : total / size + 1;
        return new PageResult<>(total, page, size, pages, records);
    }

    /** 从 MyBatis-Plus Page 对象转换，同时应用实体→VO 映射 */
    public static <E, V> PageResult<V> fromMPage(com.baomidou.mybatisplus.extension.plugins.pagination.Page<E> mpPage, Function<E, V> converter) {
        List<V> records = mpPage.getRecords().stream().map(converter).collect(Collectors.toList());
        return of(mpPage.getTotal(), mpPage.getCurrent(), mpPage.getSize(), records);
    }
}
