package com.campus.trading.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trading.common.PageResult;
import com.campus.trading.common.Result;
import com.campus.trading.modules.product.entity.Product;
import com.campus.trading.modules.product.mapper.ProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员 — 商品管理
 */
@Tag(name = "管理后台 — 商品管理")
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final ProductMapper productMapper;

    @Operation(summary = "商品列表（含已下架）")
    @GetMapping
    public Result<PageResult<Product>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "状态: 0-在售, 1-已售, 2-已下架") @RequestParam(required = false) Integer status,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.like(Product::getTitle, keyword);
        }
        wrapper.orderByDesc(Product::getCreateTime);

        Page<Product> productPage = productMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(PageResult.of(
                productPage.getTotal(), productPage.getCurrent(), productPage.getSize(), productPage.getRecords()));
    }

    @Operation(summary = "强制下架商品")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @Parameter(description = "商品ID") @PathVariable Long id,
            @Parameter(description = "状态: 0-上架, 2-下架") @RequestParam Integer status) {
        Product product = productMapper.selectById(id);
        if (product != null) {
            product.setStatus(status);
            productMapper.updateById(product);
        }
        return Result.success();
    }
}
