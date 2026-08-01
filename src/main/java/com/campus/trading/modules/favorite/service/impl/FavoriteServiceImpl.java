package com.campus.trading.modules.favorite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trading.common.BusinessException;
import com.campus.trading.common.PageResult;
import com.campus.trading.modules.favorite.entity.Favorite;
import com.campus.trading.modules.favorite.mapper.FavoriteMapper;
import com.campus.trading.modules.favorite.service.FavoriteService;
import com.campus.trading.modules.product.dto.ProductVO;
import com.campus.trading.modules.product.entity.Category;
import com.campus.trading.modules.product.entity.Product;
import com.campus.trading.modules.product.mapper.CategoryMapper;
import com.campus.trading.modules.product.mapper.ProductMapper;
import com.campus.trading.modules.user.entity.User;
import com.campus.trading.modules.user.mapper.UserMapper;
import com.campus.trading.security.SecurityUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 收藏服务实现
 */
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    private final FavoriteMapper favoriteMapper;
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    private static final Map<Integer, String> CONDITION_MAP = Map.of(
            0, "全新", 1, "几乎全新", 2, "轻微使用痕迹", 3, "明显使用痕迹");

    @Override
    @Transactional
    public void add(Long productId) {
        Long userId = SecurityUtils.getCurrentUserId();
        Product product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        Long count = favoriteMapper.selectCount(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getProductId, productId));
        if (count > 0) {
            throw new BusinessException("已收藏，无需重复收藏");
        }
        Favorite favorite = Favorite.builder()
                .userId(userId).productId(productId).build();
        favoriteMapper.insert(favorite);
    }

    @Override
    @Transactional
    public void remove(Long productId) {
        Long userId = SecurityUtils.getCurrentUserId();
        favoriteMapper.delete(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getProductId, productId));
    }

    @Override
    public boolean isFavorited(Long productId) {
        Long userId = SecurityUtils.getCurrentUserId();
        return favoriteMapper.selectCount(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getProductId, productId)) > 0;
    }

    @Override
    public PageResult<ProductVO> myFavorites(int page, int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        Page<Favorite> favPage = favoriteMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .orderByDesc(Favorite::getCreateTime));

        List<ProductVO> voList = favPage.getRecords().stream()
                .map(fav -> {
                    Product product = productMapper.selectById(fav.getProductId());
                    if (product == null) return null;
                    return toSimpleVO(product);
                })
                .filter(vo -> vo != null)
                .collect(Collectors.toList());

        return PageResult.of(favPage.getTotal(), favPage.getCurrent(), favPage.getSize(), voList);
    }

    private ProductVO toSimpleVO(Product product) {
        ProductVO vo = new ProductVO();
        vo.setId(product.getId());
        vo.setTitle(product.getTitle());
        vo.setPrice(product.getPrice());
        vo.setOriginalPrice(product.getOriginalPrice());
        vo.setConditionDesc(CONDITION_MAP.getOrDefault(product.getCondition(), "未知"));
        vo.setCreateTime(product.getCreateTime());

        // 图片
        try {
            if (product.getImages() != null) {
                List<String> images = objectMapper.readValue(product.getImages(), new TypeReference<List<String>>() {});
                vo.setImages(images);
            }
        } catch (Exception ignored) {}

        // 分类名
        Category category = categoryMapper.selectById(product.getCategoryId());
        if (category != null) vo.setCategoryName(category.getName());

        // 发布者
        User user = userMapper.selectById(product.getUserId());
        if (user != null) {
            vo.setUserNickname(user.getNickname());
            vo.setUserAvatar(user.getAvatar());
        }

        return vo;
    }
}
