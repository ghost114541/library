package com.library.admin.repository;

import com.library.admin.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 图书数据访问接口
 * 提供图书实体的数据库操作和查询方法
 */
@Repository // 标识为Spring数据访问组件
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    /**
     * 根据ISBN号查询图书
     * @param isbn 国际标准书号
     * @return 包含图书的Optional对象，可能为空
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * 根据书名模糊查询图书（不区分大小写）
     * @param title 书名关键字
     * @param pageable 分页参数
     * @return 分页的图书列表
     */
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    /**
     * 根据作者名模糊查询图书（不区分大小写）
     * @param author 作者名关键字
     * @param pageable 分页参数
     * @return 分页的图书列表
     */
    Page<Book> findByAuthorContainingIgnoreCase(String author, Pageable pageable);

    /**
     * 根据分类精确查询图书（不区分大小写）
     * @param category 图书分类
     * @param pageable 分页参数
     * @return 分页的图书列表
     */
    Page<Book> findByCategoryIgnoreCase(String category, Pageable pageable);

    /**
     * 检查指定ISBN的图书是否存在
     * @param isbn 国际标准书号
     * @return 存在返回true，否则返回false
     */
    boolean existsByIsbn(String isbn);
}