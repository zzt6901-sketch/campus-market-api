package com.campus.trading.modules.favorite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.trading.modules.favorite.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * 收藏 Mapper
 */
@Mapper
public interface FavoriteMapper extends BaseMapper<Favorite> {
}
