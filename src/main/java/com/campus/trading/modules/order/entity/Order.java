package com.campus.trading.modules.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@TableName("`order`")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 订单编号（业务唯一） */
    private String orderNo;

    /** 商品ID */
    private Long productId;

    /** 买家ID */
    private Long buyerId;

    /** 卖家ID */
    private Long sellerId;

    /** 交易金额 */
    private BigDecimal amount;

    /**
     * 订单状态:
     * 0-待付款, 1-已付款(待发货), 2-已发货(待收货), 3-已完成, 4-已取消
     */
    private Integer status;

    /** 买家备注 */
    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
