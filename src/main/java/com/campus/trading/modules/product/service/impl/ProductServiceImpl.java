package com.campus.trading.modules.product.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trading.common.*;
import com.campus.trading.modules.product.dto.ProductQueryDTO;
import com.campus.trading.modules.product.dto.ProductSaveDTO;
import com.campus.trading.modules.product.dto.ProductVO;
import com.campus.trading.modules.product.entity.Category;
import com.campus.trading.modules.product.entity.Product;
import com.campus.trading.modules.product.mapper.CategoryMapper;
import com.campus.trading.modules.product.mapper.ProductMapper;
import com.campus.trading.modules.product.service.ProductService;
import com.campus.trading.modules.user.entity.User;
import com.campus.trading.modules.user.mapper.UserMapper;
import com.campus.trading.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;

    @Override
    public PageResult<ProductVO> page(ProductQueryDTO query) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.and(w -> w.like(Product::getTitle, query.getKeyword())
                    .or().like(Product::getDescription, query.getKeyword()));
        }
        if (query.getCategoryId() != null) wrapper.eq(Product::getCategoryId, query.getCategoryId());
        if (query.getCondition() != null) wrapper.eq(Product::getCondition, query.getCondition());
        if (query.getTradeWay() != null) wrapper.eq(Product::getTradeWay, query.getTradeWay());
        if (query.getMinPrice() != null) wrapper.ge(Product::getPrice, query.getMinPrice());
        if (query.getMaxPrice() != null) wrapper.le(Product::getPrice, query.getMaxPrice());
        wrapper.eq(Product::getStatus, 0);

        // 排序 - 修复: 明确升序/降序，不追加第二个 orderBy
        String sortBy = StrUtil.isBlank(query.getSortBy()) ? "time" : query.getSortBy();
        boolean isAsc = "price_asc".equals(sortBy);
        wrapper.orderBy(true, isAsc, switch (sortBy) {
            case "price_asc", "price_desc" -> Product::getPrice;
            case "hot" -> Product::getViewCount;
            default -> Product::getCreateTime;
        });

        Page<Product> page = productMapper.selectPage(new Page<>(query.getPage(), query.getSize()), wrapper);
        return PageResult.fromMPage(page, this::toVO);
    }

    @Override
    public ProductVO detail(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null || product.getStatus() == 2) {
            throw new BusinessException("商品不存在或已下架");
        }
        product.setViewCount((product.getViewCount() == null ? 0 : product.getViewCount()) + 1);
        productMapper.updateById(product);
        return toVO(product);
    }

    @Override
    @Transactional
    public void publish(ProductSaveDTO dto) {
        Long userId = SecurityUtils.getCurrentUserId();
        Product product = new Product();
        BeanUtils.copyProperties(dto, product);
        product.setUserId(userId);
        product.setImages(ImageJsonUtils.toJson(dto.getImages()));
        product.setStatus(0);
        product.setViewCount(0);
        productMapper.insert(product);
    }

    @Override
    @Transactional
    public void update(Long productId, ProductSaveDTO dto) {
        Product product = productMapper.selectById(productId);
        if (product == null) throw new BusinessException("商品不存在");
        Long userId = SecurityUtils.getCurrentUserId();
        if (!product.getUserId().equals(userId)) throw new BusinessException("只能修改自己发布的商品");
        BeanUtils.copyProperties(dto, product);
        product.setImages(ImageJsonUtils.toJson(dto.getImages()));
        product.setId(productId);
        productMapper.updateById(product);
    }

    @Override
    @Transactional
    public void offShelf(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) throw new BusinessException("商品不存在");
        Long userId = SecurityUtils.getCurrentUserId();
        if (!product.getUserId().equals(userId) && !SecurityUtils.isAdmin())
            throw new BusinessException("只能下架自己发布的商品");
        product.setStatus(2);
        productMapper.updateById(product);
    }

    @Override
    public List<Category> getCategoryTree() {
        List<Category> all = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort));
        List<Category> roots = all.stream()
                .filter(c -> c.getParentId() == null || c.getParentId() == 0)
                .collect(Collectors.toList());
        roots.forEach(r -> r.setChildren(getChildren(r.getId(), all)));
        return roots;
    }

    @Override
    public PageResult<ProductVO> pageByUserId(Long userId, int page, int size) {
        Page<Product> p = productMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Product>().eq(Product::getUserId, userId)
                        .orderByDesc(Product::getCreateTime));
        return PageResult.fromMPage(p, this::toVO);
    }

    private List<Category> getChildren(Long parentId, List<Category> all) {
        return all.stream()
                .filter(c -> parentId.equals(c.getParentId()))
                .peek(c -> c.setChildren(getChildren(c.getId(), all)))
                .collect(Collectors.toList());
    }

    private ProductVO toVO(Product p) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(p, vo);
        vo.setImages(ImageJsonUtils.fromJson(p.getImages()));

        if (p.getCategoryId() != null) {
            Category cat = categoryMapper.selectById(p.getCategoryId());
            if (cat != null) vo.setCategoryName(cat.getName());
        }
        if (p.getUserId() != null) {
            User u = userMapper.selectById(p.getUserId());
            if (u != null) { vo.setUserNickname(u.getNickname()); vo.setUserAvatar(u.getAvatar()); }
        }
        vo.setConditionDesc(DictConstants.desc(DictConstants.PRODUCT_CONDITION, p.getCondition()));
        vo.setTradeWayDesc(DictConstants.desc(DictConstants.PRODUCT_TRADE_WAY, p.getTradeWay()));
        return vo;
    }
}
