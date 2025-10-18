package com.library.admin.service;

import com.library.admin.dto.BookDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 图书服务接口
 * 提供图书信息的增删改查及分页查询功能
 */
public interface BookService {
    /**
     * 分页查询图书信息
     *
     * @param searchCriteria 查询条件（书名/作者/ISBN等）
     * @param pageable 分页参数（页码/每页数量）
     * @return 匹配条件的图书分页结果
     */
    Page<BookDto> getAllBooks(BookDto.SearchCriteria searchCriteria, Pageable pageable);

    /**
     * 根据ID获取图书详情
     *
     * @param bookId 图书ID
     * @return 对应ID的图书信息
     */
    BookDto getBookById(Long bookId);

    /**
     * 创建新书
     *
     * @param bookRequest 创建请求数据（书名/作者/ISBN/库存等）
     * @return 创建成功的图书信息
     */
    BookDto createBook(BookDto.BookRequest bookRequest);

    /**
     * 更新图书信息
     *
     * @param bookId 要更新的图书ID
     * @param bookRequest 更新请求数据（允许更新书名/作者/库存等）
     * @return 更新后的图书信息
     */
    BookDto updateBook(Long bookId, BookDto.BookRequest bookRequest);

    /**
     * 删除图书
     *
     * @param bookId 要删除的图书ID
     */
    void deleteBook(Long bookId);
}