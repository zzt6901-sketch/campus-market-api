package com.campus.trading.modules.product.controller;

import com.campus.trading.common.PageResult;
import com.campus.trading.common.Result;
import com.campus.trading.modules.product.dto.ProductQueryDTO;
import com.campus.trading.modules.product.dto.ProductSaveDTO;
import com.campus.trading.modules.product.dto.ProductVO;
import com.campus.trading.modules.product.entity.Category;
import com.campus.trading.modules.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商品控制器
 */
@Tag(name = "商品模块", description = "商品浏览、搜索、发布、编辑")
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "商品列表（分页+多条件筛选）")
    @GetMapping
    public Result<PageResult<ProductVO>> list(ProductQueryDTO query) {
        PageResult<ProductVO> pageResult = productService.page(query);
        return Result.success(pageResult);
    }

    @Operation(summary = "商品详情")
    @GetMapping("/{id}")
    public Result<ProductVO> detail(@Parameter(description = "商品ID") @PathVariable Long id) {
        ProductVO vo = productService.detail(id);
        return Result.success(vo);
    }

    @Operation(summary = "发布商品")
    @PostMapping
    public Result<Void> publish(@Valid @RequestBody ProductSaveDTO dto) {
        productService.publish(dto);
        return Result.success();
    }

    @Operation(summary = "编辑商品")
    @PutMapping("/{id}")
    public Result<Void> update(
            @Parameter(description = "商品ID") @PathVariable Long id,
            @Valid @RequestBody ProductSaveDTO dto) {
        productService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "下架商品")
    @DeleteMapping("/{id}")
    public Result<Void> offShelf(@Parameter(description = "商品ID") @PathVariable Long id) {
        productService.offShelf(id);
        return Result.success();
    }

    @Operation(summary = "搜索商品")
    @GetMapping("/search")
    public Result<PageResult<ProductVO>> search(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        ProductQueryDTO query = new ProductQueryDTO();
        query.setKeyword(keyword);
        query.setPage(page);
        query.setSize(size);
        return Result.success(productService.page(query));
    }

    @Operation(summary = "获取分类树")
    @GetMapping("/categories")
    public Result<List<Category>> categories() {
        List<Category> tree = productService.getCategoryTree();
        return Result.success(tree);
    }
}
