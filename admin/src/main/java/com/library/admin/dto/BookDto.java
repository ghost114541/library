package com.library.admin.dto;

import com.library.admin.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDate;

/**
 * 图书数据传输对象（DTO）
 * 用于封装图书相关API请求和响应数据
 * 包含基础图书信息和搜索条件类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    /**
     * 图书ID
     * 唯一标识符（由系统自动生成）
     */
    private Long bookId;

    /**
     * 图书标题
     * 必填字段（不能为空字符串）
     */
    @NotBlank(message = "Title is required")
    private String title;

    /**
     * 图书作者
     * 必填字段（不能为空字符串）
     */
    @NotBlank(message = "Author is required")
    private String author;

    /**
     * 国际标准书号（ISBN）
     * 必填字段，格式要求：
     * - 10位或13位纯数字
     * - 示例：9787536676542（13位）或 753667654X（10位）
     */
    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "^\\d{10}|\\d{13}$", message = "ISBN must be 10 or 13 digits")
    private String isbn;

    /**
     * 出版社名称
     * 可选字段（允许为空）
     */
    private String publisher;

    /**
     * 出版日期
     * 格式：yyyy-MM-dd
     * 可选字段（允许为空）
     */
    private LocalDate publishDate;

    /**
     * 库存数量
     * 必填字段，要求：
     * - 必须为非负整数
     * - 允许值：0及以上
     */
    @PositiveOrZero(message = "Stock cannot be negative")
    private Integer stock;

    /**
     * 图书分类
     * 可选字段（允许为空）
     * 示例：文学/技术/历史等
     */
    private String category;

    /**
     * 图书标签（多个用逗号分隔）
     * 可选字段（允许为空）
     * 示例：小说,经典,文学
     */
    private String tags;

    /**
     * 图书描述信息
     * 可选字段（允许为空）
     * 包含图书内容简介等
     */
    private String description;

    /**
     * 图书状态
     * 枚举类型（Book.BookStatus）
     * 可能取值：
     * - AVAILABLE（可借阅）
     * - BORROWED（已借出）
     * - LOST（遗失）
     * - MAINTENANCE（维护中）
     */
    private Book.BookStatus status;

    /**
     * 图书创建/更新请求参数类
     * 用于封装POST/PUT请求中的图书数据
     * 包含必填字段和验证规则
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookRequest {
        /**
         * 图书标题
         * 必填字段（不能为空字符串）
         */
        @NotBlank(message = "Title is required")
        private String title;

        /**
         * 图书作者
         * 必填字段（不能为空字符串）
         */
        @NotBlank(message = "Author is required")
        private String author;

        /**
         * 国际标准书号（ISBN）
         * 必填字段，格式要求：
         * - 10位或13位纯数字
         * - 示例：9787536676542（13位）或 753667654X（10位）
         */
        @NotBlank(message = "ISBN is required")
        @Pattern(regexp = "^\\d{10}|\\d{13}$", message = "ISBN must be 10 or 13 digits")
        private String isbn;

        /**
         * 出版社名称
         * 可选字段（允许为空）
         */
        private String publisher;

        /**
         * 出版日期
         * 格式：yyyy-MM-dd
         * 可选字段（允许为空）
         */
        private LocalDate publishDate;

        /**
         * 库存数量
         * 必填字段，要求：
         * - 必须为非负整数
         * - 允许值：0及以上
         */
        @NotNull(message = "Stock quantity is required")
        @PositiveOrZero(message = "Stock cannot be negative")
        private Integer stock;

        /**
         * 图书分类
         * 可选字段（允许为空）
         * 示例：文学/技术/历史等
         */
        private String category;

        /**
         * 图书标签（多个用逗号分隔）
         * 可选字段（允许为空）
         * 示例：小说,经典,文学
         */
        private String tags;

        /**
         * 图书描述信息
         * 可选字段（允许为空）
         * 包含图书内容简介等
         */
        private String description;
    }

    /**
     * 图书搜索条件类
     * 用于封装GET请求中的搜索参数
     * 支持多条件组合查询
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchCriteria {
        /**
         * 按书名搜索（模糊匹配）
         * 示例：输入"红楼梦"可搜索包含该词的所有图书
         */
        private String title;

        /**
         * 按作者搜索（模糊匹配）
         * 示例：输入"曹雪芹"可搜索包含该词的所有图书
         */
        private String author;

        /**
         * 按ISBN精确搜索
         * 示例：输入完整ISBN号进行精确匹配
         */
        private String isbn;

        /**
         * 按分类搜索
         * 示例：输入"文学"可搜索该分类下的所有图书
         */
        private String category;

        /**
         * 按出版社搜索
         * 示例：输入"人民文学出版社"可搜索该出版社出版的图书
         */
        private String publisher;

        /**
         * 按图书状态搜索
         * 枚举类型（Book.BookStatus）
         * 可能取值：
         * - AVAILABLE（可借阅）
         * - BORROWED（已借出）
         * - LOST（遗失）
         * - MAINTENANCE（维护中）
         */
        private Book.BookStatus status;
    }
}