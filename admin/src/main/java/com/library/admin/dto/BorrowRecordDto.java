package com.library.admin.dto;

import com.library.admin.entity.BorrowRecord;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 借阅记录数据传输对象（DTO）
 * 用于封装借阅记录相关API请求和响应数据
 * 包含借阅记录信息、请求参数和搜索条件类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowRecordDto {
    /**
     * 借阅记录ID
     * 唯一标识符（由系统自动生成）
     */
    private Long recordId;

    /**
     * 用户ID
     * 关联到借阅用户的唯一标识
     */
    private Long userId;

    /**
     * 用户名
     * 借阅用户的显示名称
     */
    private String username;

    /**
     * 图书ID
     * 被借阅图书的唯一标识
     */
    private Long bookId;

    /**
     * 图书标题
     * 被借阅图书的显示名称
     */
    private String bookTitle;

    /**
     * 借阅时间
     * 格式：yyyy-MM-dd HH:mm:ss
     * 记录图书被借出的具体时间
     */
    private LocalDateTime borrowDate;

    /**
     * 应还时间
     * 格式：yyyy-MM-dd HH:mm:ss
     * 借阅人应归还图书的截止时间
     */
    private LocalDateTime dueDate;

    /**
     * 归还时间
     * 格式：yyyy-MM-dd HH:mm:ss
     * 实际归还图书的时间（可为空表示未归还）
     */
    private LocalDateTime returnDate;

    /**
     * 借阅状态
     * 枚举类型（BorrowRecord.BorrowStatus）
     * 可能取值：
     * - BORROWED（已借出）
     * - RETURNED（已归还）
     * - LOST（图书遗失）
     * - OVERDUE（已逾期）
     */
    private BorrowRecord.BorrowStatus status;

    /**
     * 借阅请求参数类
     * 用于封装POST请求中的借书数据
     * 包含必填字段和验证规则
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BorrowRequest {
        /**
         * 图书ID
         * 必填字段（不能为空）
         * 标识要借阅的图书
         */
        @NotNull(message = "Book ID is required")
        private Long bookId;

        /**
         * 应还时间
         * 必填字段，要求：
         * - 必须是当前时间或未来时间
         * - 格式：yyyy-MM-dd HH:mm:ss
         */
        @FutureOrPresent(message = "Due date must be in the present or future")
        private LocalDateTime dueDate;
    }

    /**
     * 还书请求参数类
     * 用于封装还书操作的请求数据
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReturnRequest {
        /**
         * 借阅记录ID
         * 必填字段（不能为空）
         * 标识要操作的借阅记录
         */
        @NotNull(message = "Record ID is required")
        private Long recordId;
    }

    /**
     * 借阅记录搜索条件类
     * 用于封装GET请求中的搜索参数
     * 支持多条件组合查询
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchCriteria {
        /**
         * 按用户ID搜索
         * 查找指定用户的借阅记录
         */
        private Long userId;

        /**
         * 按图书ID搜索
         * 查找指定图书的借阅记录
         */
        private Long bookId;

        /**
         * 按借阅状态搜索
         * 枚举类型（BorrowRecord.BorrowStatus）
         * 可能取值：
         * - BORROWED（已借出）
         * - RETURNED（已归还）
         * - LOST（图书遗失）
         * - OVERDUE（已逾期）
         */
        private BorrowRecord.BorrowStatus status;

        /**
         * 按是否逾期搜索
         * true: 查找逾期记录
         * false: 查找未逾期记录
         * null: 不过滤逾期状态
         */
        private Boolean overdue;
    }
}