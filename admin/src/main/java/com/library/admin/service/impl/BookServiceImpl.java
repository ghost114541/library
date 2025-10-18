package com.library.admin.service.impl;

import com.library.admin.dto.BookDto;
import com.library.admin.entity.Book;
import com.library.admin.exception.ResourceAlreadyExistsException;
import com.library.admin.exception.ResourceNotFoundException;
import com.library.admin.repository.BookRepository;
import com.library.admin.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * 书籍服务实现类
 * 提供书籍数据的增删改查和分页查询功能
 */
@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository; // 书籍数据访问对象

    /**
     * 分页查询书籍信息
     * 支持动态搜索条件过滤
     *
     * @param searchCriteria 搜索条件对象（包含标题/作者/ISBN等筛选参数）
     * @param pageable 分页参数（页码/每页数量/排序方式）
     * @return 包含书籍数据的分页结果
     */
    @Override
    public Page<BookDto> getAllBooks(BookDto.SearchCriteria searchCriteria, Pageable pageable) {
        Specification<Book> spec = buildBookSpecification(searchCriteria);
        return bookRepository.findAll(spec, pageable).map(this::mapToDto);
    }

    /**
     * 根据ID获取书籍详情
     *
     * @param bookId 书籍唯一标识
     * @return 书籍数据传输对象
     * @throws ResourceNotFoundException 如果书籍不存在
     */
    @Override
    public BookDto getBookById(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));
        return mapToDto(book);
    }

    /**
     * 创建新书籍
     *
     * @param bookRequest 书籍请求数据
     * @return 创建后的书籍数据
     * @throws ResourceAlreadyExistsException 如果ISBN已存在
     */
    @Override
    @Transactional
    public BookDto createBook(BookDto.BookRequest bookRequest) {
        // 检查ISBN是否已存在
        if (bookRepository.existsByIsbn(bookRequest.getIsbn())) {
            throw new ResourceAlreadyExistsException("Book", "isbn", bookRequest.getIsbn());
        }

        Book book = new Book();
        book.setTitle(bookRequest.getTitle());
        book.setAuthor(bookRequest.getAuthor());
        book.setIsbn(bookRequest.getIsbn());
        book.setPublisher(bookRequest.getPublisher());
        book.setPublishDate(bookRequest.getPublishDate());
        book.setStock(bookRequest.getStock());
        book.setCategory(bookRequest.getCategory());
        book.setTags(bookRequest.getTags());
        book.setDescription(bookRequest.getDescription());
        book.setStatus(Book.BookStatus.AVAILABLE);

        Book savedBook = bookRepository.save(book);
        return mapToDto(savedBook);
    }

    /**
     * 更新书籍信息
     *
     * @param bookId 书籍ID
     * @param bookRequest 更新请求数据
     * @return 更新后的书籍数据
     * @throws ResourceNotFoundException 如果书籍不存在
     * @throws ResourceAlreadyExistsException 如果修改后的新ISBN已存在
     */
    @Override
    @Transactional
    public BookDto updateBook(Long bookId, BookDto.BookRequest bookRequest) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));

        // 检查ISBN是否被修改且新ISBN是否已存在
        if (!book.getIsbn().equals(bookRequest.getIsbn()) && bookRepository.existsByIsbn(bookRequest.getIsbn())) {
            throw new ResourceAlreadyExistsException("Book", "isbn", bookRequest.getIsbn());
        }

        book.setTitle(bookRequest.getTitle());
        book.setAuthor(bookRequest.getAuthor());
        book.setIsbn(bookRequest.getIsbn());
        book.setPublisher(bookRequest.getPublisher());
        book.setPublishDate(bookRequest.getPublishDate());
        book.setStock(bookRequest.getStock());
        book.setCategory(bookRequest.getCategory());
        book.setTags(bookRequest.getTags());
        book.setDescription(bookRequest.getDescription());

        // 根据库存状态更新书籍可用状态
        if (bookRequest.getStock() <= 0) {
            book.setStatus(Book.BookStatus.UNAVAILABLE);
        } else {
            book.setStatus(Book.BookStatus.AVAILABLE);
        }

        Book updatedBook = bookRepository.save(book);
        return mapToDto(updatedBook);
    }

    /**
     * 删除书籍
     *
     * @param bookId 书籍ID
     * @throws ResourceNotFoundException 如果书籍不存在
     */
    @Override
    @Transactional
    public void deleteBook(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Book", "id", bookId);
        }

        bookRepository.deleteById(bookId);
    }

    /**
     * 构建JPA动态查询条件
     * 支持多字段组合查询（标题/作者/ISBN/分类等）
     *
     * @param criteria 查询条件对象
     * @return JPA Specification查询条件
     */
    private Specification<Book> buildBookSpecification(BookDto.SearchCriteria criteria) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria != null) {
                if (StringUtils.hasText(criteria.getTitle())) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("title")),
                            "%" + criteria.getTitle().toLowerCase() + "%"
                    ));
                }

                if (StringUtils.hasText(criteria.getAuthor())) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("author")),
                            "%" + criteria.getAuthor().toLowerCase() + "%"
                    ));
                }

                if (StringUtils.hasText(criteria.getIsbn())) {
                    predicates.add(criteriaBuilder.like(
                            root.get("isbn"),
                            "%" + criteria.getIsbn() + "%"
                    ));
                }

                if (StringUtils.hasText(criteria.getCategory())) {
                    predicates.add(criteriaBuilder.equal(
                            criteriaBuilder.lower(root.get("category")),
                            criteria.getCategory().toLowerCase()
                    ));
                }

                if (StringUtils.hasText(criteria.getPublisher())) {
                    predicates.add(criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("publisher")),
                            "%" + criteria.getPublisher().toLowerCase() + "%"
                    ));
                }

                if (criteria.getStatus() != null) {
                    predicates.add(criteriaBuilder.equal(
                            root.get("status"),
                            criteria.getStatus()
                    ));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 将书籍实体对象转换为数据传输对象（DTO）
     *
     * @param book 书籍实体
     * @return 对应的DTO对象
     */
    private BookDto mapToDto(Book book) {
        BookDto dto = new BookDto();
        dto.setBookId(book.getBookId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setIsbn(book.getIsbn());
        dto.setPublisher(book.getPublisher());
        dto.setPublishDate(book.getPublishDate());
        dto.setStock(book.getStock());
        dto.setCategory(book.getCategory());
        dto.setTags(book.getTags());
        dto.setDescription(book.getDescription());
        dto.setStatus(book.getStatus());
        return dto;
    }
}