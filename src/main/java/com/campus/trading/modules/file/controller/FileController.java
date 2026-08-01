package com.campus.trading.modules.file.controller;

import com.campus.trading.common.Result;
import com.campus.trading.modules.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件控制器
 */
@Tag(name = "文件模块", description = "图片上传")
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(summary = "上传图片")
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(
            @Parameter(description = "图片文件", required = true) @RequestParam("file") MultipartFile file) {
        String url = fileService.upload(file);
        return Result.success("上传成功", Map.of("url", url));
    }
}
