package com.campus.trading.modules.message.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 消息视图对象
 */
@Data
@Schema(description = "消息")
public class MessageVO {

    @Schema(description = "消息ID")
    private Long id;

    @Schema(description = "发送者ID")
    private Long fromUserId;

    @Schema(description = "发送者昵称")
    private String fromUserNickname;

    @Schema(description = "接收者ID")
    private Long toUserId;

    @Schema(description = "关联商品ID")
    private Long productId;

    @Schema(description = "关联商品标题")
    private String productTitle;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "类型: 0-系统通知, 1-买家咨询, 2-订单通知")
    private Integer type;

    @Schema(description = "类型描述")
    private String typeDesc;

    @Schema(description = "是否已读: 0-未读, 1-已读")
    private Integer isRead;

    @Schema(description = "发送时间")
    private LocalDateTime createTime;
}
