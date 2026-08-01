package com.campus.trading.modules.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.trading.common.PageResult;
import com.campus.trading.modules.product.dto.ProductQueryDTO;
import com.campus.trading.modules.product.dto.ProductSaveDTO;
import com.campus.trading.modules.product.dto.ProductVO;
import com.campus.trading.modules.product.entity.Category;

import java.util.List;

/**
 * 商品服务接口
 */
public interface ProductService {

    /**
     * 分页查询商品列表（多条件筛选）
     */
    PageResult<ProductVO> page(ProductQueryDTO query);

    /**
     * 获取商品详情（浏览量+1）
     */
    ProductVO detail(Long id);

    /**
     * 发布商品
     */
    void publish(ProductSaveDTO dto);

    /**
     * 编辑商品
     */
    void update(Long productId, ProductSaveDTO dto);

    /**
     * 下架商品
     */
    void offShelf(Long productId);

    /**
     * 获取分类树
     */
    List<Category> getCategoryTree();

    /**
     * 获取用户发布的商品列表
     */
    PageResult<ProductVO> pageByUserId(Long userId, int page, int size);
}
