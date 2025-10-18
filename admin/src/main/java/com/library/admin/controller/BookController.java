package com.library.admin.controller;

import com.library.admin.dto.ApiResponse;
import com.library.admin.dto.BookDto;
import com.library.admin.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 图书管理控制器类 - 提供图书信息的CRUD操作接口
 * 所有接口统一路径前缀：/api/books
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    /**
     * 图书服务组件 - 用于调用图书相关的业务逻辑
     */
    @Autowired
    private BookService bookService;

    /**
     * 获取所有图书分页列表
     * GET 请求 /api/books
     *
     * @param searchCriteria 搜索条件（绑定到BookDto.SearchCriteria对象）
     * @param pageable 分页参数（默认每页10条，按图书ID排序）
     * @return 包含分页图书数据的ApiResponse响应对象
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<BookDto>>> getAllBooks(
            @ModelAttribute BookDto.SearchCriteria searchCriteria,
            @PageableDefault(size = 10, sort = "bookId") Pageable pageable) {
        Page<BookDto> books = bookService.getAllBooks(searchCriteria, pageable);
        return ResponseEntity.ok(ApiResponse.success(books));
    }

    /**
     * 根据图书ID获取详细信息
     * GET 请求 /api/books/{bookId}
     *
     * @param bookId 要查询的图书ID
     * @return 包含图书详细信息的ApiResponse响应对象
     */
    @GetMapping("/{bookId}")
    public ResponseEntity<ApiResponse<BookDto>> getBookById(@PathVariable Long bookId) {
        BookDto bookDto = bookService.getBookById(bookId);
        return ResponseEntity.ok(ApiResponse.success(bookDto));
    }

    /**
     * 创建新图书（仅管理员权限）
     * POST 请求 /api/books
     *
     * @param bookRequest 图书创建请求参数（包含验证注解）
     * @return 包含创建结果的ApiResponse响应对象（状态码201 Created）
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookDto>> createBook(@Valid @RequestBody BookDto.BookRequest bookRequest) {
        BookDto bookDto = bookService.createBook(bookRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Book created successfully", bookDto));
    }

    /**
     * 更新图书信息（仅管理员权限）
     * PUT 请求 /api/books/{bookId}
     *
     * @param bookId 要更新的图书ID
     * @param bookRequest 图书更新请求参数（包含验证注解）
     * @return 包含更新结果的ApiResponse响应对象
     */
    @PutMapping("/{bookId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookDto>> updateBook(
            @PathVariable Long bookId,
            @Valid @RequestBody BookDto.BookRequest bookRequest) {
        BookDto bookDto = bookService.updateBook(bookId, bookRequest);
        return ResponseEntity.ok(ApiResponse.success("Book updated successfully", bookDto));
    }

    /**
     * 删除指定图书（仅管理员权限）
     * DELETE 请求 /api/books/{bookId}
     *
     * @param bookId 要删除的图书ID
     * @return 包含操作结果的ApiResponse响应对象
     */
    @DeleteMapping("/{bookId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.ok(ApiResponse.success("Book deleted successfully", null));
    }
}