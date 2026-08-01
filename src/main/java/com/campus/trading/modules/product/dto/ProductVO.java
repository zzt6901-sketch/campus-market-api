package com.campus.trading.modules.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品详情视图对象
 */
@Data
@Schema(description = "商品详情")
public class ProductVO {

    @Schema(description = "商品ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "售价")
    private BigDecimal price;

    @Schema(description = "原价")
    private BigDecimal originalPrice;

    @Schema(description = "图片列表")
    private List<String> images;

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "发布者ID")
    private Long userId;

    @Schema(description = "发布者昵称")
    private String userNickname;

    @Schema(description = "发布者头像")
    private String userAvatar;

    @Schema(description = "成色")
    private Integer condition;

    @Schema(description = "成色描述")
    private String conditionDesc;

    @Schema(description = "交易方式")
    private Integer tradeWay;

    @Schema(description = "交易方式描述")
    private String tradeWayDesc;

    @Schema(description = "校区")
    private String campus;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "浏览量")
    private Integer viewCount;

    @Schema(description = "发布时间")
    private LocalDateTime createTime;
}
