package com.campus.trading.modules.favorite.service;

import com.campus.trading.common.PageResult;
import com.campus.trading.modules.product.dto.ProductVO;

/**
 * 收藏服务接口
 */
public interface FavoriteService {

    /** 添加收藏 */
    void add(Long productId);

    /** 取消收藏 */
    void remove(Long productId);

    /** 是否已收藏 */
    boolean isFavorited(Long productId);

    /** 我的收藏列表 */
    PageResult<ProductVO> myFavorites(int page, int size);
}
