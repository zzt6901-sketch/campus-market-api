package com.campus.trading.modules.favorite.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 收藏实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("favorite")
public class Favorite {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 商品ID */
    private Long productId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
