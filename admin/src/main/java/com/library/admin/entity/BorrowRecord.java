package com.library.admin.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 借阅记录实体类
 * 映射数据库表 borrow_records，记录图书借阅和归还信息
 * 包含用户与图书的关联关系及状态信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "borrow_records")
public class BorrowRecord {

    /**
     * 借阅记录ID
     * 主键字段，由数据库自动生成（自增）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    /**
     * 借阅用户
     * 多对一关联（User实体）
     * FetchType.LAZY：延迟加载用户信息
     * 关联字段：user_id（外键）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 借阅图书
     * 多对一关联（Book实体）
     * FetchType.LAZY：延迟加载图书信息
     * 关联字段：book_id（外键）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    /**
     * 借阅时间
     * 必填字段，格式：yyyy-MM-dd HH:mm:ss
     * 记录图书被借出的具体时间
     */
    @Column(name = "borrow_date", nullable = false)
    private LocalDateTime borrowDate;

    /**
     * 应还时间
     * 必填字段，格式：yyyy-MM-dd HH:mm:ss
     * 借阅人应归还图书的截止时间
     */
    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    /**
     * 实际归还时间
     * 可选字段，允许为空
     * 格式：yyyy-MM-dd HH:mm:ss
     * 为空表示图书尚未归还
     */
    @Column(name = "return_date")
    private LocalDateTime returnDate;

    /**
     * 借阅状态
     * 枚举类型（BorrowStatus）
     * 存储方式：STRING（存储枚举的字符串表示）
     * 默认值：BORROWED（已借出）
     */
    @Enumerated(EnumType.STRING)
    private BorrowStatus status = BorrowStatus.BORROWED;

    /**
     * 记录创建时间
     * 自动填充字段，由数据库在创建记录时生成
     * 格式：yyyy-MM-dd HH:mm:ss
     */
    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    /**
     * 记录更新时间
     * 自动填充字段，由数据库在更新记录时自动更新
     * 格式：yyyy-MM-dd HH:mm:ss
     */
    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 借阅状态枚举
     * 定义借阅记录的当前状态
     */
    public enum BorrowStatus {
        /**
         * BORROWED：已借出
         * 表示图书当前处于借阅状态
         */
        BORROWED,

        /**
         * RETURNED：已归还
         * 表示图书已按期归还
         */
        RETURNED,

        /**
         * OVERDUE：已逾期
         * 表示图书未在规定期限内归还
         */
        OVERDUE
    }
}