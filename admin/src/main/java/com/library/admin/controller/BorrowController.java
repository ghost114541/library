package com.library.admin.controller;

import com.library.admin.dto.ApiResponse;
import com.library.admin.dto.BorrowRecordDto;
import com.library.admin.service.BorrowRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 借阅记录控制器类 - 提供图书借阅相关接口
 * 所有接口统一路径前缀：/api/borrow
 */
@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

    /**
     * 借阅记录服务组件 - 用于调用借阅相关的业务逻辑
     */
    @Autowired
    private BorrowRecordService borrowRecordService;

    /**
     * 图书借阅接口（需用户认证）
     * POST 请求 /api/borrow
     *
     * @param authentication Spring Security认证对象（自动注入）
     * @param borrowRequest 借阅请求参数（包含验证注解）
     * @return 包含借阅记录的ApiResponse响应对象（状态码201 Created）
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BorrowRecordDto>> borrowBook(
            Authentication authentication,
            @Valid @RequestBody BorrowRecordDto.BorrowRequest borrowRequest) {
        // 通过认证对象获取当前用户信息，完成借阅操作
        BorrowRecordDto borrowRecordDto = borrowRecordService.borrowBook(authentication, borrowRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Book borrowed successfully", borrowRecordDto));
    }

    /**
     * 图书归还接口（需用户认证）
     * PUT 请求 /api/borrow/{recordId}/return
     *
     * @param authentication Spring Security认证对象（自动注入）
     * @param recordId 借阅记录ID（路径变量）
     * @return 包含归还结果的ApiResponse响应对象
     */
    @PutMapping("/{recordId}/return")
    public ResponseEntity<ApiResponse<BorrowRecordDto>> returnBook(
            Authentication authentication,
            @PathVariable Long recordId) {
        // 构建归还请求对象并设置借阅记录ID
        BorrowRecordDto.ReturnRequest returnRequest = new BorrowRecordDto.ReturnRequest();
        returnRequest.setRecordId(recordId);

        // 调用服务层完成归还操作
        BorrowRecordDto borrowRecordDto = borrowRecordService.returnBook(authentication, returnRequest);
        return ResponseEntity.ok(ApiResponse.success("Book returned successfully", borrowRecordDto));
    }

    /**
     * 获取当前用户借阅记录（需用户认证）
     * GET 请求 /api/borrow/user
     *
     * @param authentication Spring Security认证对象（自动注入）
     * @param searchCriteria 搜索条件（绑定到BorrowRecordDto.SearchCriteria对象）
     * @param pageable 分页参数（默认每页10条，按借阅日期排序）
     * @return 包含分页借阅记录的ApiResponse响应对象
     */
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<Page<BorrowRecordDto>>> getUserBorrowRecords(
            Authentication authentication,
            @ModelAttribute BorrowRecordDto.SearchCriteria searchCriteria,
            @PageableDefault(size = 10, sort = "borrowDate") Pageable pageable) {
        // 通过认证对象获取当前用户信息，查询其借阅记录
        Page<BorrowRecordDto> records = borrowRecordService.getUserBorrowRecords(authentication, searchCriteria, pageable);
        return ResponseEntity.ok(ApiResponse.success(records));
    }

    /**
     * 获取所有借阅记录（仅管理员权限）
     * GET 请求 /api/borrow/admin
     *
     * @param searchCriteria 搜索条件（绑定到BorrowRecordDto.SearchCriteria对象）
     * @param pageable 分页参数（默认每页10条，按借阅日期排序）
     * @return 包含分页借阅记录的ApiResponse响应对象
     */
    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Page<BorrowRecordDto>>> getAllBorrowRecords(
            @ModelAttribute BorrowRecordDto.SearchCriteria searchCriteria,
            @PageableDefault(size = 10, sort = "borrowDate") Pageable pageable) {
        // 管理员权限查询所有借阅记录
        Page<BorrowRecordDto> records = borrowRecordService.getAllBorrowRecords(searchCriteria, pageable);
        return ResponseEntity.ok(ApiResponse.success(records));
    }
}