package com.campus.trading.modules.message.controller;

import com.campus.trading.common.PageResult;
import com.campus.trading.common.Result;
import com.campus.trading.modules.message.dto.MessageVO;
import com.campus.trading.modules.message.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 消息控制器
 */
@Tag(name = "消息模块", description = "系统通知、买家咨询、订单消息")
@RestController
@RequestMapping("/api/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "发送消息")
    @PostMapping
    public Result<Void> send(@RequestBody MessageVO message) {
        messageService.send(message);
        return Result.success();
    }

    @Operation(summary = "消息列表")
    @GetMapping
    public Result<PageResult<MessageVO>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        return Result.success(messageService.page(page, size));
    }

    @Operation(summary = "未读消息数量")
    @GetMapping("/unread/count")
    public Result<Long> unreadCount() {
        return Result.success(messageService.unreadCount());
    }

    @Operation(summary = "标记已读")
    @PutMapping("/{id}/read")
    public Result<Void> markRead(@Parameter(description = "消息ID") @PathVariable Long id) {
        messageService.markRead(id);
        return Result.success();
    }

    @Operation(summary = "全部标为已读")
    @PutMapping("/read-all")
    public Result<Void> markAllRead() {
        messageService.markAllRead();
        return Result.success();
    }
}
