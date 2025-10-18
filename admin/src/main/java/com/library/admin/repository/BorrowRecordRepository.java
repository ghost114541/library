package com.library.admin.repository;

import com.library.admin.entity.Book;
import com.library.admin.entity.BorrowRecord;
import com.library.admin.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 借阅记录数据访问接口
 * 提供借阅记录的数据库操作和复杂查询方法
 */
@Repository // 标识为Spring数据访问组件
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long>, JpaSpecificationExecutor<BorrowRecord> {

    /**
     * 根据用户查询借阅记录（分页）
     * @param user 用户对象
     * @param pageable 分页参数
     * @return 该用户的所有借阅记录分页列表
     */
    Page<BorrowRecord> findByUser(User user, Pageable pageable);

    /**
     * 根据用户和状态查询借阅记录（分页）
     * @param user 用户对象
     * @param status 借阅状态
     * @param pageable 分页参数
     * @return 该用户指定状态的借阅记录分页列表
     */
    Page<BorrowRecord> findByUserAndStatus(User user, BorrowRecord.BorrowStatus status, Pageable pageable);

    /**
     * 根据状态查询借阅记录（分页）
     * @param status 借阅状态
     * @param pageable 分页参数
     * @return 指定状态的所有借阅记录分页列表
     */
    Page<BorrowRecord> findByStatus(BorrowRecord.BorrowStatus status, Pageable pageable);

    /**
     * 根据图书和状态查询借阅记录
     * @param book 图书对象
     * @param status 借阅状态
     * @return 该图书指定状态的借阅记录列表
     */
    List<BorrowRecord> findByBookAndStatus(Book book, BorrowRecord.BorrowStatus status);

    /**
     * 查询所有逾期未还的借阅记录（自定义JPQL查询）
     * @param currentDate 当前日期时间
     * @param pageable 分页参数
     * @return 逾期未还的借阅记录分页列表
     */
    @Query("SELECT br FROM BorrowRecord br WHERE br.status = 'BORROWED' AND br.dueDate < :currentDate")
    Page<BorrowRecord> findOverdueRecords(LocalDateTime currentDate, Pageable pageable);

    /**
     * 统计指定图书ID的已借出数量（自定义JPQL查询）
     * @param bookId 图书ID
     * @return 该图书的已借出数量
     */
    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.book.bookId = :bookId AND br.status = 'BORROWED'")
    int countBorrowedBooksByBookId(Long bookId);
}