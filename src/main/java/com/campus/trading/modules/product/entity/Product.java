package com.campus.trading.modules.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体
 */
@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 标题 */
    private String title;

    /** 描述 */
    private String description;

    /** 售价 */
    private BigDecimal price;

    /** 原价 */
    private BigDecimal originalPrice;

    /** 图片列表（JSON数组） */
    private String images;

    /** 所属分类ID */
    private Long categoryId;

    /** 发布者用户ID */
    private Long userId;

    /** 成色: 0-全新, 1-几乎全新, 2-轻微使用, 3-明显使用 */
    @TableField("`condition`")
    private Integer condition;

    /** 交易方式: 0-自提, 1-快递 */
    private Integer tradeWay;

    /** 校区 */
    private String campus;

    /** 状态: 0-在售, 1-已售, 2-已下架 */
    private Integer status;

    /** 浏览量 */
    private Integer viewCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
