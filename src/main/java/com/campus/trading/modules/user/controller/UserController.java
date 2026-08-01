package com.campus.trading.modules.user.controller;

import com.campus.trading.common.PageResult;
import com.campus.trading.common.Result;
import com.campus.trading.modules.product.dto.ProductVO;
import com.campus.trading.modules.product.service.ProductService;
import com.campus.trading.modules.user.dto.LoginDTO;
import com.campus.trading.modules.user.dto.RegisterDTO;
import com.campus.trading.modules.user.dto.UserVO;
import com.campus.trading.modules.user.entity.User;
import com.campus.trading.modules.user.service.UserService;
import com.campus.trading.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户控制器
 */
@Tag(name = "用户模块", description = "注册、登录、个人信息管理")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ProductService productService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, String>> login(@Valid @RequestBody LoginDTO dto) {
        String token = userService.login(dto);
        return Result.success("登录成功", Map.of("token", token));
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<UserVO> info() {
        UserVO userVO = userService.getCurrentUserInfo();
        return Result.success(userVO);
    }

    @Operation(summary = "修改个人信息")
    @PutMapping("/info")
    public Result<Void> updateInfo(@RequestBody User user) {
        userService.updateInfo(user);
        return Result.success();
    }

    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> updatePassword(
            @Parameter(description = "旧密码") @RequestParam String oldPassword,
            @Parameter(description = "新密码") @RequestParam String newPassword) {
        Long userId = SecurityUtils.getCurrentUserId();
        userService.updatePassword(userId, oldPassword, newPassword);
        return Result.success();
    }

    @Operation(summary = "我发布的商品列表")
    @GetMapping("/products")
    public Result<PageResult<ProductVO>> myProducts(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        return Result.success(productService.pageByUserId(userId, page, size));
    }
}
