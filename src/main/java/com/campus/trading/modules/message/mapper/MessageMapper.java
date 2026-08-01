package com.campus.trading.modules.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.trading.modules.message.entity.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息 Mapper
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
