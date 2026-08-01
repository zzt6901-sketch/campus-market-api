package com.campus.trading.modules.file.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 */
public interface FileService {

    /**
     * 上传文件，返回访问 URL
     */
    String upload(MultipartFile file);
}
