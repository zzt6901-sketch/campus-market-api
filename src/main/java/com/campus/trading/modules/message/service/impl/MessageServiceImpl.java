package com.campus.trading.modules.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trading.common.BusinessException;
import com.campus.trading.common.PageResult;
import com.campus.trading.modules.message.dto.MessageVO;
import com.campus.trading.modules.message.entity.Message;
import com.campus.trading.modules.message.mapper.MessageMapper;
import com.campus.trading.modules.message.service.MessageService;
import com.campus.trading.modules.product.entity.Product;
import com.campus.trading.modules.product.mapper.ProductMapper;
import com.campus.trading.modules.user.entity.User;
import com.campus.trading.modules.user.mapper.UserMapper;
import com.campus.trading.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消息服务实现
 */
@Service
@RequiredArgsConstructor
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    private final MessageMapper messageMapper;
    private final UserMapper userMapper;
    private final ProductMapper productMapper;

    private static final Map<Integer, String> TYPE_MAP = Map.of(
            0, "系统通知", 1, "买家咨询", 2, "订单通知");

    @Override
    @Transactional
    public void send(MessageVO vo) {
        Long fromUserId = SecurityUtils.getCurrentUserId();
        Message message = Message.builder()
                .fromUserId(fromUserId)
                .toUserId(vo.getToUserId())
                .productId(vo.getProductId())
                .content(vo.getContent())
                .type(vo.getType() != null ? vo.getType() : 1)
                .isRead(0)
                .build();
        messageMapper.insert(message);
    }

    @Override
    public PageResult<MessageVO> page(int page, int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        Page<Message> msgPage = messageMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getToUserId, userId)
                        .orderByDesc(Message::getCreateTime));

        List<MessageVO> voList = msgPage.getRecords().stream()
                .map(this::toVO).collect(Collectors.toList());
        return PageResult.of(msgPage.getTotal(), msgPage.getCurrent(), msgPage.getSize(), voList);
    }

    @Override
    public long unreadCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        return messageMapper.selectCount(
                new LambdaQueryWrapper<Message>()
                        .eq(Message::getToUserId, userId)
                        .eq(Message::getIsRead, 0));
    }

    @Override
    @Transactional
    public void markRead(Long messageId) {
        Message message = messageMapper.selectById(messageId);
        if (message == null) {
            throw new BusinessException("消息不存在");
        }
        if (!message.getToUserId().equals(SecurityUtils.getCurrentUserId())) {
            throw new BusinessException("无权操作此消息");
        }
        message.setIsRead(1);
        messageMapper.updateById(message);
    }

    @Override
    @Transactional
    public void markAllRead() {
        Long userId = SecurityUtils.getCurrentUserId();
        messageMapper.update(null, new LambdaUpdateWrapper<Message>()
                .eq(Message::getToUserId, userId)
                .eq(Message::getIsRead, 0)
                .set(Message::getIsRead, 1));
    }

    private MessageVO toVO(Message message) {
        MessageVO vo = new MessageVO();
        vo.setId(message.getId());
        vo.setFromUserId(message.getFromUserId());
        vo.setToUserId(message.getToUserId());
        vo.setProductId(message.getProductId());
        vo.setContent(message.getContent());
        vo.setType(message.getType());
        vo.setTypeDesc(TYPE_MAP.getOrDefault(message.getType(), "未知"));
        vo.setIsRead(message.getIsRead());
        vo.setCreateTime(message.getCreateTime());

        // 发送者信息
        if (message.getFromUserId() != null && message.getFromUserId() > 0) {
            User fromUser = userMapper.selectById(message.getFromUserId());
            if (fromUser != null) {
                vo.setFromUserNickname(fromUser.getNickname());
            }
        } else {
            vo.setFromUserNickname("系统");
        }

        // 关联商品信息
        if (message.getProductId() != null) {
            Product product = productMapper.selectById(message.getProductId());
            if (product != null) {
                vo.setProductTitle(product.getTitle());
            }
        }

        return vo;
    }
}
