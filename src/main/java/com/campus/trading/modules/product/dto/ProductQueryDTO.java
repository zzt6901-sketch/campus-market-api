package com.campus.trading.modules.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品搜索/筛选参数
 */
@Data
@Schema(description = "商品查询参数")
public class ProductQueryDTO {

    @Schema(description = "搜索关键词")
    private String keyword;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "成色", example = "1")
    private Integer condition;

    @Schema(description = "交易方式", example = "0")
    private Integer tradeWay;

    @Schema(description = "最低价格")
    private BigDecimal minPrice;

    @Schema(description = "最高价格")
    private BigDecimal maxPrice;

    @Schema(description = "排序方式: time-最新, price_asc-价格升序, price_desc-价格降序, hot-最热", example = "time")
    private String sortBy;

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;
}
