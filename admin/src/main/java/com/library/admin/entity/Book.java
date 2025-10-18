package com.library.admin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 图书实体类
 * 映射数据库表 books，包含图书的核心信息和状态
 * 使用 JPA 注解进行数据库字段映射
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
public class Book {

    /**
     * 图书ID
     * 主键字段，由数据库自动生成（自增）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long bookId;

    /**
     * 图书标题
     * 必填字段，不能为空
     * 示例：Java编程思想
     */
    @Column(nullable = false)
    private String title;

    /**
     * 图书作者
     * 必填字段，不能为空
     * 示例：Bruce Eckel
     */
    @Column(nullable = false)
    private String author;

    /**
     * 国际标准书号（ISBN）
     * 必填字段，不能为空且具有唯一性
     * 格式要求：10位或13位数字（实际校验需配合业务逻辑）
     * 示例：9787536676542（13位）或 753667654X（10位）
     */
    @Column(nullable = false, unique = true)
    private String isbn;

    /**
     * 出版社名称
     * 可选字段，允许为空
     * 示例：机械工业出版社
     */
    private String publisher;

    /**
     * 出版日期
     * 格式：yyyy-MM-dd
     * 示例：2023-05-15
     */
    @Column(name = "publish_date")
    private LocalDate publishDate;

    /**
     * 库存数量
     * 默认值：0
     * 允许值：0及以上整数
     */
    private Integer stock = 0;

    /**
     * 图书分类
     * 可选字段，允许为空
     * 示例：编程/文学/历史
     */
    private String category;

    /**
     * 图书标签
     * 可选字段，允许为空
     * 多个标签用逗号分隔（如：Java,编程,计算机）
     */
    private String tags;

    /**
     * 图书描述信息
     * 使用TEXT类型存储较长内容
     * 可包含图书简介、目录等详细信息
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 图书状态
     * 枚举类型（BookStatus）
     * 可能取值：
     * - AVAILABLE（可借阅）
     * - UNAVAILABLE（不可借阅）
     * 默认值：AVAILABLE
     */
    @Enumerated(EnumType.STRING)
    private BookStatus status = BookStatus.AVAILABLE;

    /**
     * 创建时间
     * 自动填充字段，由数据库在创建记录时生成
     * 格式：yyyy-MM-dd HH:mm:ss
     */
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     * 自动填充字段，由数据库在更新记录时自动更新
     * 格式：yyyy-MM-dd HH:mm:ss
     */
    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 图书状态枚举
     * 定义图书的可用性状态
     */
    public enum BookStatus {
        /**
         * 可借阅状态：图书在馆且可被借出
         */
        AVAILABLE,

        /**
         * 不可借阅状态：图书已被借出、维护中或遗失
         */
        UNAVAILABLE
    }
}