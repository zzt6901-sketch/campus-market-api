package com.campus.trading.modules.message.service;

import com.campus.trading.common.PageResult;
import com.campus.trading.modules.message.dto.MessageVO;

/**
 * 消息服务接口
 */
public interface MessageService {

    /** 发送消息 */
    void send(MessageVO message);

    /** 消息列表 */
    PageResult<MessageVO> page(int page, int size);

    /** 未读消息数量 */
    long unreadCount();

    /** 标记已读 */
    void markRead(Long messageId);

    /** 全部标为已读 */
    void markAllRead();
}
