package com.campus.trading.modules.favorite.controller;

import com.campus.trading.common.PageResult;
import com.campus.trading.common.Result;
import com.campus.trading.modules.favorite.service.FavoriteService;
import com.campus.trading.modules.product.dto.ProductVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 收藏控制器
 */
@Tag(name = "收藏模块", description = "添加/取消收藏、收藏列表")
@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "添加收藏")
    @PostMapping
    public Result<Void> add(@Parameter(description = "商品ID") @RequestParam Long productId) {
        favoriteService.add(productId);
        return Result.success();
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/{productId}")
    public Result<Void> remove(@Parameter(description = "商品ID") @PathVariable Long productId) {
        favoriteService.remove(productId);
        return Result.success();
    }

    @Operation(summary = "检查是否已收藏")
    @GetMapping("/check/{productId}")
    public Result<Boolean> check(@Parameter(description = "商品ID") @PathVariable Long productId) {
        return Result.success(favoriteService.isFavorited(productId));
    }

    @Operation(summary = "我的收藏列表")
    @GetMapping
    public Result<PageResult<ProductVO>> myFavorites(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        return Result.success(favoriteService.myFavorites(page, size));
    }
}
