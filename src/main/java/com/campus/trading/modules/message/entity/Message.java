package com.campus.trading.modules.message.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("message")
public class Message {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 发送者用户ID，0 表示系统消息 */
    private Long fromUserId;

    /** 接收者用户ID */
    private Long toUserId;

    /** 关联商品ID */
    private Long productId;

    /** 消息内容 */
    private String content;

    /** 类型: 0-系统通知, 1-买家咨询, 2-订单通知 */
    private Integer type;

    /** 是否已读 */
    private Integer isRead;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
