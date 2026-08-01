package com.campus.trading.modules.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品新增/编辑请求体
 */
@Data
@Schema(description = "商品保存请求")
public class ProductSaveDTO {

    @NotBlank(message = "标题不能为空")
    @Schema(description = "商品标题", example = "高等数学第七版 上下册", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "商品描述", example = "只用了一学期，几乎全新，笔记很少")
    private String description;

    @NotNull(message = "价格不能为空")
    @Schema(description = "售价", example = "25.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal price;

    @Schema(description = "原价", example = "49.80")
    private BigDecimal originalPrice;

    @Schema(description = "图片地址列表", example = "[\"/uploads/product/1.jpg\", \"/uploads/product/2.jpg\"]")
    private List<String> images;

    @NotNull(message = "分类不能为空")
    @Schema(description = "分类ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long categoryId;

    @Schema(description = "成色: 0-全新, 1-几乎全新, 2-轻微使用, 3-明显使用", example = "1")
    private Integer condition;

    @Schema(description = "交易方式: 0-自提, 1-快递", example = "0")
    private Integer tradeWay;

    @Schema(description = "校区", example = "主校区")
    private String campus;
}
