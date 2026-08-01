package com.campus.trading.admin.controller;

import com.campus.trading.common.PageResult;
import com.campus.trading.common.Result;
import com.campus.trading.modules.order.dto.OrderVO;
import com.campus.trading.modules.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员 — 订单管理
 */
@Tag(name = "管理后台 — 订单管理")
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {

    private final OrderService orderService;

    @Operation(summary = "订单列表")
    @GetMapping
    public Result<PageResult<OrderVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "状态: 0-待付款, 1-已付款, 2-已发货, 3-已完成, 4-已取消")
            @RequestParam(required = false) Integer status) {
        return Result.success(orderService.pageAll(page, size, status));
    }
}
