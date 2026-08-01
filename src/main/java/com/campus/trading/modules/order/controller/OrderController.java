package com.campus.trading.modules.order.controller;

import com.campus.trading.common.PageResult;
import com.campus.trading.common.Result;
import com.campus.trading.modules.order.dto.OrderVO;
import com.campus.trading.modules.order.service.OrderService;
import com.campus.trading.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 */
@Tag(name = "订单模块", description = "创建订单、付款、发货、收货、取消")
@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "创建订单（购买商品）")
    @PostMapping
    public Result<OrderVO> create(
            @Parameter(description = "商品ID") @RequestParam Long productId,
            @Parameter(description = "备注") @RequestParam(required = false) String remark) {
        OrderVO vo = orderService.create(productId, remark);
        return Result.success("下单成功", vo);
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{id}")
    public Result<OrderVO> detail(@Parameter(description = "订单ID") @PathVariable Long id) {
        OrderVO vo = orderService.detail(id);
        return Result.success(vo);
    }

    @Operation(summary = "确认付款")
    @PutMapping("/{id}/pay")
    public Result<Void> pay(@Parameter(description = "订单ID") @PathVariable Long id) {
        orderService.pay(id);
        return Result.success();
    }

    @Operation(summary = "卖家发货")
    @PutMapping("/{id}/ship")
    public Result<Void> ship(@Parameter(description = "订单ID") @PathVariable Long id) {
        orderService.ship(id);
        return Result.success();
    }

    @Operation(summary = "确认收货")
    @PutMapping("/{id}/complete")
    public Result<Void> complete(@Parameter(description = "订单ID") @PathVariable Long id) {
        orderService.complete(id);
        return Result.success();
    }

    @Operation(summary = "取消订单")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@Parameter(description = "订单ID") @PathVariable Long id) {
        orderService.cancel(id);
        return Result.success();
    }

    @Operation(summary = "我买到的订单")
    @GetMapping("/buy")
    public Result<PageResult<OrderVO>> buyList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(orderService.pageByBuyer(userId, page, size));
    }

    @Operation(summary = "我卖出的订单")
    @GetMapping("/sell")
    public Result<PageResult<OrderVO>> sellList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(orderService.pageBySeller(userId, page, size));
    }
}
