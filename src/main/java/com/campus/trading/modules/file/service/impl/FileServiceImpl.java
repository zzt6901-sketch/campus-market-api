package com.campus.trading.modules.file.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.campus.trading.common.BusinessException;
import com.campus.trading.modules.file.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * 文件服务实现 — 本地磁盘存储
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload.path:./uploads/}")
    private String uploadPath;

    @Value("${file.upload.max-size:10485760}")
    private long maxSize;

    /** 允许的图片格式 */
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp", "bmp");

    @Override
    public String upload(MultipartFile file) {
        // 空文件检查
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }

        // 文件大小检查
        if (file.getSize() > maxSize) {
            throw new BusinessException("文件大小不能超过 " + (maxSize / 1024 / 1024) + "MB");
        }

        // 后缀检查
        String originalFilename = file.getOriginalFilename();
        String extension = FileUtil.extName(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException("不支持的图片格式，仅支持: " + String.join(", ", ALLOWED_EXTENSIONS));
        }

        try {
            // 按日期分目录
            String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String fileName = IdUtil.fastSimpleUUID() + "." + extension;
            String relativePath = dateDir + "/" + fileName;

            // 完整存储路径
            File destFile = new File(uploadPath + relativePath);
            FileUtil.mkParentDirs(destFile);

            // 写入磁盘
            file.transferTo(destFile);

            // 返回访问 URL
            String url = "/uploads/" + relativePath.replace("\\", "/");
            log.info("文件上传成功: {} -> {}", originalFilename, url);
            return url;

        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new BusinessException("文件上传失败，请重试");
        }
    }
}
