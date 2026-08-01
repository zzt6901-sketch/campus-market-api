package com.campus.trading.common;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.List;

/**
 * 图片 JSON 序列化工具
 */
public final class ImageJsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ImageJsonUtils() {}

    /** List→JSON 字符串 */
    public static String toJson(List<String> images) {
        if (images == null || images.isEmpty()) return "[]";
        try { return MAPPER.writeValueAsString(images); } catch (Exception e) { return "[]"; }
    }

    /** JSON 字符串→List */
    public static List<String> fromJson(String imagesJson) {
        if (StrUtil.isBlank(imagesJson) || "[]".equals(imagesJson)) return Collections.emptyList();
        try { return MAPPER.readValue(imagesJson, new TypeReference<List<String>>() {}); } catch (Exception e) { return Collections.emptyList(); }
    }

    /** 获取第一张图片 URL */
    public static String firstImage(String imagesJson) {
        List<String> images = fromJson(imagesJson);
        return images.isEmpty() ? null : images.get(0);
    }
}
