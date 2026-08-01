package com.campus.trading.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trading.common.BusinessException;
import com.campus.trading.modules.user.dto.LoginDTO;
import com.campus.trading.modules.user.dto.RegisterDTO;
import com.campus.trading.modules.user.dto.UserVO;
import com.campus.trading.modules.user.entity.User;
import com.campus.trading.modules.user.mapper.UserMapper;
import com.campus.trading.modules.user.service.UserService;
import com.campus.trading.security.JwtUtils;
import com.campus.trading.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public void register(RegisterDTO dto) {
        // 检查用户名是否已存在
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已被注册");
        }

        // 创建用户
        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .school(dto.getSchool())
                .role(0)      // 默认普通用户
                .status(0)    // 默认正常状态
                .build();

        userMapper.insert(user);
    }

    @Override
    public String login(LoginDTO dto) {
        // Spring Security 认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

        User user = (User) authentication.getPrincipal();
        return jwtUtils.generateToken(user.getId(), user.getUsername());
    }

    @Override
    public UserVO getCurrentUserInfo() {
        User user = SecurityUtils.getCurrentUser();
        if (user == null) {
            throw new BusinessException(401, "未登录");
        }
        return toVO(user);
    }

    @Override
    @Transactional
    public void updateInfo(User updateUser) {
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            throw new BusinessException(401, "未登录");
        }
        updateUser.setId(currentUser.getId());
        // 不允许修改用户名、密码、角色
        updateUser.setUsername(null);
        updateUser.setPassword(null);
        updateUser.setRole(null);
        updateUser.setStatus(null);
        userMapper.updateById(updateUser);
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    /**
     * Entity → VO（脱敏）
     */
    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
