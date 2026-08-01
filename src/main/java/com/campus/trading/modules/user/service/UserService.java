package com.campus.trading.modules.user.service;

import com.campus.trading.common.PageResult;
import com.campus.trading.modules.user.dto.LoginDTO;
import com.campus.trading.modules.user.dto.RegisterDTO;
import com.campus.trading.modules.user.dto.UserVO;
import com.campus.trading.modules.user.entity.User;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     */
    void register(RegisterDTO dto);

    /**
     * 用户登录，返回 JWT Token
     */
    String login(LoginDTO dto);

    /**
     * 获取当前用户信息
     */
    UserVO getCurrentUserInfo();

    /**
     * 修改个人信息
     */
    void updateInfo(User user);

    /**
     * 修改密码
     */
    void updatePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);
}
