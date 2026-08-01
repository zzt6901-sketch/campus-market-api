package com.campus.trading.modules.order.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trading.common.BusinessException;
import com.campus.trading.common.*;
import com.campus.trading.modules.order.dto.OrderVO;
import com.campus.trading.modules.order.entity.Order;
import com.campus.trading.modules.order.mapper.OrderMapper;
import com.campus.trading.modules.order.service.OrderService;
import com.campus.trading.modules.product.entity.Product;
import com.campus.trading.modules.product.mapper.ProductMapper;
import com.campus.trading.modules.user.entity.User;
import com.campus.trading.modules.user.mapper.UserMapper;
import com.campus.trading.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务实现
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final UserMapper userMapper;


    @Override
    @Transactional
    public OrderVO create(Long productId, String remark) {
        Long buyerId = SecurityUtils.getCurrentUserId();

        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus() != 0) {
            throw new BusinessException("商品不存在或已售出");
        }

        if (product.getUserId().equals(buyerId)) {
            throw new BusinessException("不能购买自己发布的商品");
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(IdUtil.getSnowflakeNextIdStr());
        order.setProductId(productId);
        order.setBuyerId(buyerId);
        order.setSellerId(product.getUserId());
        order.setAmount(product.getPrice());
        order.setStatus(0);  // 待付款
        order.setRemark(remark);
        orderMapper.insert(order);

        // 锁定商品状态（标记已售）
        product.setStatus(1);
        productMapper.updateById(product);

        return toVO(order);
    }

    @Override
    public OrderVO detail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        Long userId = SecurityUtils.getCurrentUserId();
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)
                && !SecurityUtils.isAdmin()) {
            throw new BusinessException("无权查看此订单");
        }

        return toVO(order);
    }

    @Override
    @Transactional
    public void pay(Long orderId) {
        Order order = getAndCheckOrder(orderId);
        if (!order.getBuyerId().equals(SecurityUtils.getCurrentUserId())) {
            throw new BusinessException("只能支付自己的订单");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不正确，无法付款");
        }
        order.setStatus(1);  // 已付款
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void ship(Long orderId) {
        Order order = getAndCheckOrder(orderId);
        if (!order.getSellerId().equals(SecurityUtils.getCurrentUserId())) {
            throw new BusinessException("只能操作自己卖出的订单");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException("订单状态不正确，无法发货");
        }
        order.setStatus(2);  // 已发货
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void complete(Long orderId) {
        Order order = getAndCheckOrder(orderId);
        if (!order.getBuyerId().equals(SecurityUtils.getCurrentUserId())) {
            throw new BusinessException("只能确认自己购买的订单");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException("订单状态不正确，无法确认收货");
        }
        order.setStatus(3);  // 已完成
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void cancel(Long orderId) {
        Order order = getAndCheckOrder(orderId);
        Long userId = SecurityUtils.getCurrentUserId();

        if (!order.getBuyerId().equals(userId) && !SecurityUtils.isAdmin()) {
            throw new BusinessException("只能取消自己的订单");
        }
        // 只有待付款和已付款（未发货）可以取消
        if (order.getStatus() > 1) {
            throw new BusinessException("订单当前状态无法取消");
        }

        order.setStatus(4);  // 已取消
        orderMapper.updateById(order);

        // 恢复商品状态为在售
        Product product = productMapper.selectById(order.getProductId());
        if (product != null && product.getStatus() == 1) {
            product.setStatus(0);
            productMapper.updateById(product);
        }
    }

    @Override
    public PageResult<OrderVO> pageByBuyer(Long userId, int page, int size) {
        Page<Order> orderPage = orderMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getBuyerId, userId)
                        .orderByDesc(Order::getCreateTime));

        List<OrderVO> voList = orderPage.getRecords().stream()
                .map(this::toVO).collect(Collectors.toList());
        return PageResult.of(orderPage.getTotal(), orderPage.getCurrent(), orderPage.getSize(), voList);
    }

    @Override
    public PageResult<OrderVO> pageBySeller(Long userId, int page, int size) {
        Page<Order> orderPage = orderMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getSellerId, userId)
                        .orderByDesc(Order::getCreateTime));

        List<OrderVO> voList = orderPage.getRecords().stream()
                .map(this::toVO).collect(Collectors.toList());
        return PageResult.of(orderPage.getTotal(), orderPage.getCurrent(), orderPage.getSize(), voList);
    }

    @Override
    public PageResult<OrderVO> pageAll(int page, int size, Integer status) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .orderByDesc(Order::getCreateTime);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        Page<Order> orderPage = orderMapper.selectPage(new Page<>(page, size), wrapper);

        List<OrderVO> voList = orderPage.getRecords().stream()
                .map(this::toVO).collect(Collectors.toList());
        return PageResult.of(orderPage.getTotal(), orderPage.getCurrent(), orderPage.getSize(), voList);
    }

    // ======== 私有方法 ========

    private Order getAndCheckOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }

    private OrderVO toVO(Order order) {
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(order, vo);
        vo.setStatusDesc(DictConstants.desc(DictConstants.ORDER_STATUS, order.getStatus()));

        // 商品信息
        Product product = productMapper.selectById(order.getProductId());
        if (product != null) {
            vo.setProductTitle(product.getTitle());
            vo.setProductImage(ImageJsonUtils.firstImage(product.getImages()));
        }

        // 买家信息
        User buyer = userMapper.selectById(order.getBuyerId());
        if (buyer != null) {
            vo.setBuyerNickname(buyer.getNickname());
        }

        // 卖家信息
        User seller = userMapper.selectById(order.getSellerId());
        if (seller != null) {
            vo.setSellerNickname(seller.getNickname());
        }

        return vo;
    }
}
