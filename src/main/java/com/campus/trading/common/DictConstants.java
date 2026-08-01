package com.campus.trading.common;

import java.util.Map;
import java.util.LinkedHashMap;

/**
 * 业务字典常量 —— 替代散落在各 Service 中的重复 Map 定义
 */
public final class DictConstants {

    private DictConstants() {}

    /** 商品成色 */
    public static final Map<Integer, String> PRODUCT_CONDITION = Map.of(
            0, "全新", 1, "几乎全新", 2, "轻微使用痕迹", 3, "明显使用痕迹");

    /** 交易方式 */
    public static final Map<Integer, String> PRODUCT_TRADE_WAY = Map.of(
            0, "仅自提", 1, "可快递");

    /** 订单状态 */
    public static final Map<Integer, String> ORDER_STATUS = new LinkedHashMap<>() {{
        put(0, "待付款"); put(1, "已付款"); put(2, "已发货");
        put(3, "已完成"); put(4, "已取消");
    }};

    /** 消息类型 */
    public static final Map<Integer, String> MESSAGE_TYPE = Map.of(
            0, "系统通知", 1, "买家咨询", 2, "订单通知");

    /** 商品状态 */
    public static final Map<Integer, String> PRODUCT_STATUS = Map.of(
            0, "在售", 1, "已售", 2, "已下架");

    /** 安全地获取描述 */
    public static String desc(Map<Integer, String> map, Integer key) {
        return map.getOrDefault(key, "未知");
    }
}
