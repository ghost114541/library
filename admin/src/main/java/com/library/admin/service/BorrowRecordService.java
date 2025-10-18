package com.library.admin.service;

import com.library.admin.dto.BorrowRecordDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

/**
 * 借阅记录服务接口
 * 提供图书借阅、归还及借阅记录查询功能
 */
public interface BorrowRecordService {
    /**
     * 用户借阅图书
     *
     * @param authentication 认证信息（包含当前用户）
     * @param borrowRequest 借阅请求数据（图书ID等）
     * @return 生成的借阅记录
     */
    BorrowRecordDto borrowBook(Authentication authentication, BorrowRecordDto.BorrowRequest borrowRequest);

    /**
     * 用户归还图书
     *
     * @param authentication 认证信息（包含当前用户）
     * @param returnRequest 归还请求数据（借阅记录ID）
     * @return 更新后的借阅记录（标记为已归还）
     */
    BorrowRecordDto returnBook(Authentication authentication, BorrowRecordDto.ReturnRequest returnRequest);

    /**
     * 获取当前用户的借阅记录
     *
     * @param authentication 认证信息（包含当前用户）
     * @param searchCriteria 查询条件（图书名称/状态等）
     * @param pageable 分页参数（页码/每页数量）
     * @return 用户的借阅记录分页结果
     */
    Page<BorrowRecordDto> getUserBorrowRecords(Authentication authentication, BorrowRecordDto.SearchCriteria searchCriteria, Pageable pageable);

    /**
     * 管理员查询所有借阅记录
     *
     * @param searchCriteria 查询条件（用户/图书/状态等）
     * @param pageable 分页参数（页码/每页数量）
     * @return 所有借阅记录的分页结果
     */
    Page<BorrowRecordDto> getAllBorrowRecords(BorrowRecordDto.SearchCriteria searchCriteria, Pageable pageable);
}