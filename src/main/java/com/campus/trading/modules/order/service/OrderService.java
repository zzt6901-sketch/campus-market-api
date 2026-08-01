package com.campus.trading.modules.order.service;

import com.campus.trading.common.PageResult;
import com.campus.trading.modules.order.dto.OrderVO;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 创建订单
     */
    OrderVO create(Long productId, String remark);

    /**
     * 订单详情
     */
    OrderVO detail(Long orderId);

    /**
     * 确认付款
     */
    void pay(Long orderId);

    /**
     * 卖家发货
     */
    void ship(Long orderId);

    /**
     * 确认收货
     */
    void complete(Long orderId);

    /**
     * 取消订单
     */
    void cancel(Long orderId);

    /**
     * 我买到的订单
     */
    PageResult<OrderVO> pageByBuyer(Long userId, int page, int size);

    /**
     * 我卖出的订单
     */
    PageResult<OrderVO> pageBySeller(Long userId, int page, int size);

    /**
     * 所有订单（管理员）
     */
    PageResult<OrderVO> pageAll(int page, int size, Integer status);
}
