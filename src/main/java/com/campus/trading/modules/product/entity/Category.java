package com.campus.trading.modules.product.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品分类实体
 */
@Data
@TableName("category")
public class Category {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 分类名称 */
    private String name;

    /** 父级分类ID，0 表示顶级 */
    private Long parentId;

    /** 排序 */
    private Integer sort;

    /** 图标 */
    private String icon;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 子分类（非数据库字段） */
    @TableField(exist = false)
    private List<Category> children;
}
